#include <jni.h>
#include <string>
#include <vector>
#include <deque>
#include <mutex>
#include <thread>
#include <atomic>
#include <cstring>
#include <cstdlib>
#include <unistd.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/select.h>
#include <android/log.h>

#define LOG_TAG "net_udp"

namespace {

const char *PING_PREFIX = "PING:";
const char *PONG_PREFIX = "PONG:";
const char U_PREFIX = 'U';
const int MAX_DATAGRAM = 8192;
const int MAX_QUEUE = 200;

struct Message {
    long seq;
    std::string text;
    std::string fromIp;
    int fromPort;
};

struct Endpoint {
    int fd = -1;
    int boundPort = -1;
    std::string peerIp;
    int peerPort = -1;
    std::atomic<bool> running{false};
    std::thread recvThread;
    std::mutex mutex;
    std::deque<Message> queue;
    long lastSeq = -2;
    std::string lastText;
    long sendSeq = 0;
};

std::mutex g_mutex;
std::vector<Endpoint *> g_endpoints;

Endpoint *fromHandle(jlong handle) {
    return reinterpret_cast<Endpoint *>(handle);
}

std::string jstringToString(JNIEnv *env, jstring value) {
    if (!value) {
        return std::string();
    }
    const char *chars = env->GetStringUTFChars(value, nullptr);
    std::string result(chars ? chars : "");
    if (chars) {
        env->ReleaseStringUTFChars(value, chars);
    }
    return result;
}

bool startsWith(const std::string &value, const char *prefix) {
    size_t len = strlen(prefix);
    return value.size() >= len && value.compare(0, len, prefix) == 0;
}

int makeSocket() {
    int fd = socket(AF_INET, SOCK_DGRAM, 0);
    if (fd < 0) {
        return -1;
    }
    int reuse = 1;
    setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, &reuse, sizeof(reuse));
    int broadcast = 1;
    setsockopt(fd, SOL_SOCKET, SO_BROADCAST, &broadcast, sizeof(broadcast));
    return fd;
}

sockaddr_in makeAddr(const std::string &ip, int port) {
    sockaddr_in addr{};
    addr.sin_family = AF_INET;
    addr.sin_port = htons(static_cast<uint16_t>(port));
    inet_pton(AF_INET, ip.c_str(), &addr.sin_addr);
    return addr;
}

bool sendRaw(int fd, const std::string &ip, int port, const std::string &data) {
    if (data.size() > static_cast<size_t>(MAX_DATAGRAM)) {
        return false;
    }
    sockaddr_in addr = makeAddr(ip, port);
    ssize_t sent = sendto(fd, data.data(), data.size(), 0,
            reinterpret_cast<sockaddr *>(&addr), sizeof(addr));
    return sent == static_cast<ssize_t>(data.size());
}

void pushMessage(Endpoint *endpoint, long seq, const std::string &text,
        const std::string &fromIp, int fromPort) {
    std::lock_guard<std::mutex> lock(endpoint->mutex);
    if (seq == endpoint->lastSeq && text == endpoint->lastText) {
        return;
    }
    endpoint->lastSeq = seq;
    endpoint->lastText = text;
    Message message{seq, text, fromIp, fromPort};
    endpoint->queue.push_back(std::move(message));
    while (endpoint->queue.size() > static_cast<size_t>(MAX_QUEUE)) {
        endpoint->queue.pop_front();
    }
}

void recvLoop(Endpoint *endpoint) {
    std::vector<char> buffer(MAX_DATAGRAM);
    while (endpoint->running.load()) {
        fd_set readSet;
        FD_ZERO(&readSet);
        FD_SET(endpoint->fd, &readSet);
        timeval timeout{};
        timeout.tv_sec = 0;
        timeout.tv_usec = 100000;
        int ready = select(endpoint->fd + 1, &readSet, nullptr, nullptr, &timeout);
        if (!endpoint->running.load()) {
            break;
        }
        if (ready <= 0) {
            continue;
        }
        sockaddr_in sender{};
        socklen_t senderLen = sizeof(sender);
        ssize_t received = recvfrom(endpoint->fd, buffer.data(), buffer.size(), 0,
                reinterpret_cast<sockaddr *>(&sender), &senderLen);
        if (received <= 0) {
            continue;
        }
        std::string text(buffer.data(), static_cast<size_t>(received));
        if (text.empty()) {
            continue;
        }
        char ipText[INET_ADDRSTRLEN]{0};
        inet_ntop(AF_INET, &sender.sin_addr, ipText, sizeof(ipText));
        std::string fromIp(ipText);
        int fromPort = ntohs(sender.sin_port);
        if (startsWith(text, PING_PREFIX)) {
            std::string reply = std::string(PONG_PREFIX) + text.substr(strlen(PING_PREFIX));
            sendRaw(endpoint->fd, fromIp, fromPort, reply);
            continue;
        }
        if (startsWith(text, PONG_PREFIX)) {
            continue;
        }
        long seq = -1;
        std::string payload = text;
        if (!text.empty() && text[0] == U_PREFIX) {
            size_t cut = text.find(':', 1);
            if (cut != std::string::npos && cut > 1) {
                char *end = nullptr;
                long parsed = strtol(text.c_str() + 1, &end, 10);
                if (end && static_cast<size_t>(end - text.c_str()) == cut) {
                    seq = parsed;
                    payload = text.substr(cut + 1);
                }
            }
        }
        pushMessage(endpoint, seq, payload, fromIp, fromPort);
    }
}

void stopLocked(Endpoint *endpoint) {
    endpoint->running.store(false);
    if (endpoint->fd >= 0) {
        shutdown(endpoint->fd, SHUT_RDWR);
        close(endpoint->fd);
        endpoint->fd = -1;
    }
    endpoint->boundPort = -1;
}

}

extern "C" {

jlong Java_org_catrobat_catroid_content_UdpNative_nativeCreate(JNIEnv *env, jobject) {
    Endpoint *endpoint = new Endpoint();
    std::lock_guard<std::mutex> lock(g_mutex);
    g_endpoints.push_back(endpoint);
    return reinterpret_cast<jlong>(endpoint);
}

void Java_org_catrobat_catroid_content_UdpNative_nativeDestroy(JNIEnv *env, jobject, jlong handle) {
    Endpoint *endpoint = fromHandle(handle);
    if (!endpoint) {
        return;
    }
    {
        std::lock_guard<std::mutex> lock(endpoint->mutex);
        stopLocked(endpoint);
    }
    if (endpoint->recvThread.joinable()) {
        endpoint->recvThread.join();
    }
    {
        std::lock_guard<std::mutex> lock(g_mutex);
        for (auto it = g_endpoints.begin(); it != g_endpoints.end(); ++it) {
            if (*it == endpoint) {
                g_endpoints.erase(it);
                break;
            }
        }
    }
    delete endpoint;
}

jboolean Java_org_catrobat_catroid_content_UdpNative_nativeStart(JNIEnv *env, jobject, jlong handle, jint port) {
    Endpoint *endpoint = fromHandle(handle);
    if (!endpoint) {
        return JNI_FALSE;
    }
    if (endpoint->recvThread.joinable()) {
        endpoint->recvThread.join();
    }
    {
        std::lock_guard<std::mutex> lock(endpoint->mutex);
        stopLocked(endpoint);
        int fd = makeSocket();
        if (fd < 0) {
            return JNI_FALSE;
        }
        sockaddr_in addr{};
        addr.sin_family = AF_INET;
        addr.sin_port = htons(static_cast<uint16_t>(port));
        addr.sin_addr.s_addr = htonl(INADDR_ANY);
        if (bind(fd, reinterpret_cast<sockaddr *>(&addr), sizeof(addr)) != 0) {
            close(fd);
            return JNI_FALSE;
        }
        endpoint->fd = fd;
        endpoint->boundPort = port;
        endpoint->running.store(true);
    }
    try {
        endpoint->recvThread = std::thread(recvLoop, endpoint);
    } catch (...) {
        std::lock_guard<std::mutex> lock(endpoint->mutex);
        stopLocked(endpoint);
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

void Java_org_catrobat_catroid_content_UdpNative_nativeStop(JNIEnv *env, jobject, jlong handle) {
    Endpoint *endpoint = fromHandle(handle);
    if (!endpoint) {
        return;
    }
    {
        std::lock_guard<std::mutex> lock(endpoint->mutex);
        stopLocked(endpoint);
    }
    if (endpoint->recvThread.joinable()) {
        endpoint->recvThread.join();
    }
}

void Java_org_catrobat_catroid_content_UdpNative_nativeConnect(JNIEnv *env, jobject, jlong handle,
        jstring ip, jint port) {
    Endpoint *endpoint = fromHandle(handle);
    if (!endpoint) {
        return;
    }
    std::lock_guard<std::mutex> lock(endpoint->mutex);
    endpoint->peerIp = jstringToString(env, ip);
    endpoint->peerPort = port;
}

void Java_org_catrobat_catroid_content_UdpNative_nativeDisconnectPeer(JNIEnv *env, jobject, jlong handle) {
    Endpoint *endpoint = fromHandle(handle);
    if (!endpoint) {
        return;
    }
    std::lock_guard<std::mutex> lock(endpoint->mutex);
    endpoint->peerIp.clear();
    endpoint->peerPort = -1;
}

jstring Java_org_catrobat_catroid_content_UdpNative_nativeGetPeerIp(JNIEnv *env, jobject, jlong handle) {
    Endpoint *endpoint = fromHandle(handle);
    std::string ip;
    if (endpoint) {
        std::lock_guard<std::mutex> lock(endpoint->mutex);
        ip = endpoint->peerIp;
    }
    return env->NewStringUTF(ip.c_str());
}

jint Java_org_catrobat_catroid_content_UdpNative_nativeGetPeerPort(JNIEnv *env, jobject, jlong handle) {
    Endpoint *endpoint = fromHandle(handle);
    if (!endpoint) {
        return -1;
    }
    std::lock_guard<std::mutex> lock(endpoint->mutex);
    return endpoint->peerPort;
}

jint Java_org_catrobat_catroid_content_UdpNative_nativeGetBoundPort(JNIEnv *env, jobject, jlong handle) {
    Endpoint *endpoint = fromHandle(handle);
    if (!endpoint) {
        return -1;
    }
    std::lock_guard<std::mutex> lock(endpoint->mutex);
    return endpoint->boundPort;
}

jboolean Java_org_catrobat_catroid_content_UdpNative_nativeIsRunning(JNIEnv *env, jobject, jlong handle) {
    Endpoint *endpoint = fromHandle(handle);
    if (!endpoint) {
        return JNI_FALSE;
    }
    return endpoint->running.load() ? JNI_TRUE : JNI_FALSE;
}

jboolean Java_org_catrobat_catroid_content_UdpNative_nativeSendTo(JNIEnv *env, jobject, jlong handle,
        jstring ip, jint port, jstring text) {
    Endpoint *endpoint = fromHandle(handle);
    if (!endpoint) {
        return JNI_FALSE;
    }
    std::string targetIp = jstringToString(env, ip);
    std::string payload = jstringToString(env, text);
    long seq;
    int fd;
    {
        std::lock_guard<std::mutex> lock(endpoint->mutex);
        endpoint->sendSeq++;
        seq = endpoint->sendSeq;
        fd = endpoint->fd;
    }
    std::string data;
    data.reserve(payload.size() + 16);
    data.push_back(U_PREFIX);
    data += std::to_string(seq);
    data.push_back(':');
    data += payload;
    if (fd >= 0) {
        return sendRaw(fd, targetIp, port, data) ? JNI_TRUE : JNI_FALSE;
    }
    int tmp = makeSocket();
    if (tmp < 0) {
        return JNI_FALSE;
    }
    bool ok = sendRaw(tmp, targetIp, port, data);
    close(tmp);
    return ok ? JNI_TRUE : JNI_FALSE;
}

jboolean Java_org_catrobat_catroid_content_UdpNative_nativeSendToPeer(JNIEnv *env, jobject, jlong handle,
        jstring text) {
    Endpoint *endpoint = fromHandle(handle);
    if (!endpoint) {
        return JNI_FALSE;
    }
    std::string peerIp;
    int peerPort = -1;
    {
        std::lock_guard<std::mutex> lock(endpoint->mutex);
        peerIp = endpoint->peerIp;
        peerPort = endpoint->peerPort;
    }
    if (peerIp.empty() || peerPort < 0) {
        return JNI_FALSE;
    }
    return Java_org_catrobat_catroid_content_UdpNative_nativeSendTo(env, nullptr, handle,
            env->NewStringUTF(peerIp.c_str()), peerPort, text);
}

jobjectArray Java_org_catrobat_catroid_content_UdpNative_nativeDrain(JNIEnv *env, jobject, jlong handle) {
    Endpoint *endpoint = fromHandle(handle);
    jclass messageClass = env->FindClass("org/catrobat/catroid/content/UdpMessage");
    if (!messageClass) {
        return nullptr;
    }
    jmethodID constructor = env->GetMethodID(messageClass, "<init>", "(JLjava/lang/String;Ljava/lang/String;I)V");
    if (!constructor) {
        return nullptr;
    }
    std::vector<Message> taken;
    if (endpoint) {
        std::lock_guard<std::mutex> lock(endpoint->mutex);
        taken.assign(endpoint->queue.begin(), endpoint->queue.end());
        endpoint->queue.clear();
    }
    jobjectArray result = env->NewObjectArray(static_cast<jsize>(taken.size()), messageClass, nullptr);
    for (size_t i = 0; i < taken.size(); i++) {
        jstring text = env->NewStringUTF(taken[i].text.c_str());
        jstring fromIp = env->NewStringUTF(taken[i].fromIp.c_str());
        jobject item = env->NewObject(messageClass, constructor,
                static_cast<jlong>(taken[i].seq), text, fromIp, static_cast<jint>(taken[i].fromPort));
        env->SetObjectArrayElement(result, static_cast<jsize>(i), item);
        env->DeleteLocalRef(item);
        env->DeleteLocalRef(text);
        env->DeleteLocalRef(fromIp);
    }
    return result;
}

}
