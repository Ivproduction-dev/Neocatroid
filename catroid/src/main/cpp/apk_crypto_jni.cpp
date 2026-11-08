#include <jni.h>
#include <android/log.h>
#include <cstdint>
#include <cstring>
#include <string>
#include <vector>

namespace {

constexpr const char *LOG_TAG = "apk_crypto";

constexpr size_t CERT_HASH_SIZE = 32;
constexpr size_t KEY_MATERIAL_SIZE = 64;
constexpr size_t SALT_SIZE = 16;
constexpr size_t TAG_SIZE = 8;
constexpr size_t WRAPPED_SIZE = 64;
constexpr size_t CHECKSUM_SIZE = 32;
constexpr size_t BLOB_SIZE = 136;
constexpr size_t BLOB_CHECKSUM_OFFSET = 88;
constexpr size_t BLOB_WRAPPED_OFFSET = 24;
constexpr size_t BLOB_SALT_OFFSET = 8;
constexpr size_t BLOB_TAG_OFFSET = 0;

const uint8_t PEPPER[32] __attribute__((used)) = {
    0x4e, 0x43, 0x56, 0x34, 0x5f, 0x50, 0x45, 0x50,
    0x50, 0x45, 0x52, 0x5f, 0x53, 0x4c, 0x4f, 0x54,
    0x5f, 0x56, 0x31, 0x21, 0xa5, 0x5a, 0xc3, 0x3c,
    0x5a, 0xa5, 0x3c, 0xc3, 0x00, 0x00, 0x00, 0x00
};

uint8_t g_pepper_override[32] = {0};
bool g_pepper_override_set = false;

void setPepperOverride(const uint8_t *pepper, size_t length) {
    if (length == 32) {
        memcpy(g_pepper_override, pepper, 32);
        g_pepper_override_set = true;
    }
}

const uint8_t* getActivePepper() {
    return g_pepper_override_set ? g_pepper_override : PEPPER;
}

const uint32_t SHA256_K[64] = {
    0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5,
    0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
    0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
    0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
    0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
    0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
    0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
    0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
    0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
    0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
    0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3,
    0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
    0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
    0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
    0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
    0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
};

struct Sha256Ctx {
    uint32_t state[8];
    uint64_t length;
    uint8_t buffer[64];
    size_t bufferLength;
};

inline uint32_t rotateRight(uint32_t value, int count) {
    return (value >> count) | (value << (32 - count));
}

void sha256Init(Sha256Ctx *ctx) {
    ctx->state[0] = 0x6a09e667;
    ctx->state[1] = 0xbb67ae85;
    ctx->state[2] = 0x3c6ef372;
    ctx->state[3] = 0xa54ff53a;
    ctx->state[4] = 0x510e527f;
    ctx->state[5] = 0x9b05688c;
    ctx->state[6] = 0x1f83d9ab;
    ctx->state[7] = 0x5be0cd19;
    ctx->length = 0;
    ctx->bufferLength = 0;
}

void sha256Transform(Sha256Ctx *ctx, const uint8_t *block) {
    uint32_t w[64];
    for (int i = 0; i < 16; i++) {
        w[i] = (static_cast<uint32_t>(block[i * 4]) << 24) |
               (static_cast<uint32_t>(block[i * 4 + 1]) << 16) |
               (static_cast<uint32_t>(block[i * 4 + 2]) << 8) |
               static_cast<uint32_t>(block[i * 4 + 3]);
    }
    for (int i = 16; i < 64; i++) {
        uint32_t s0 = rotateRight(w[i - 15], 7) ^ rotateRight(w[i - 15], 18) ^ (w[i - 15] >> 3);
        uint32_t s1 = rotateRight(w[i - 2], 17) ^ rotateRight(w[i - 2], 19) ^ (w[i - 2] >> 10);
        w[i] = w[i - 16] + s0 + w[i - 7] + s1;
    }

    uint32_t a = ctx->state[0];
    uint32_t b = ctx->state[1];
    uint32_t c = ctx->state[2];
    uint32_t d = ctx->state[3];
    uint32_t e = ctx->state[4];
    uint32_t f = ctx->state[5];
    uint32_t g = ctx->state[6];
    uint32_t h = ctx->state[7];

    for (int i = 0; i < 64; i++) {
        uint32_t s1 = rotateRight(e, 6) ^ rotateRight(e, 11) ^ rotateRight(e, 25);
        uint32_t ch = (e & f) ^ (~e & g);
        uint32_t temp1 = h + s1 + ch + SHA256_K[i] + w[i];
        uint32_t s0 = rotateRight(a, 2) ^ rotateRight(a, 13) ^ rotateRight(a, 22);
        uint32_t maj = (a & b) ^ (a & c) ^ (b & c);
        uint32_t temp2 = s0 + maj;
        h = g;
        g = f;
        f = e;
        e = d + temp1;
        d = c;
        c = b;
        b = a;
        a = temp1 + temp2;
    }

    ctx->state[0] += a;
    ctx->state[1] += b;
    ctx->state[2] += c;
    ctx->state[3] += d;
    ctx->state[4] += e;
    ctx->state[5] += f;
    ctx->state[6] += g;
    ctx->state[7] += h;
}

void sha256Update(Sha256Ctx *ctx, const uint8_t *data, size_t length) {
    if (length == 0) return;
    ctx->length += length;
    size_t offset = 0;
    while (offset < length) {
        size_t space = 64 - ctx->bufferLength;
        size_t take = (length - offset < space) ? (length - offset) : space;
        memcpy(ctx->buffer + ctx->bufferLength, data + offset, take);
        ctx->bufferLength += take;
        offset += take;
        if (ctx->bufferLength == 64) {
            sha256Transform(ctx, ctx->buffer);
            ctx->bufferLength = 0;
        }
    }
}

void sha256Final(Sha256Ctx *ctx, uint8_t *out) {
    uint64_t bitLength = ctx->length * 8;
    uint8_t pad = 0x80;
    sha256Update(ctx, &pad, 1);
    uint8_t zero = 0;
    while (ctx->bufferLength != 56) {
        sha256Update(ctx, &zero, 1);
    }
    uint8_t lengthBytes[8];
    for (int i = 0; i < 8; i++) {
        lengthBytes[7 - i] = static_cast<uint8_t>((bitLength >> (i * 8)) & 0xFF);
    }
    sha256Update(ctx, lengthBytes, 8);
    for (int i = 0; i < 8; i++) {
        out[i * 4] = static_cast<uint8_t>((ctx->state[i] >> 24) & 0xFF);
        out[i * 4 + 1] = static_cast<uint8_t>((ctx->state[i] >> 16) & 0xFF);
        out[i * 4 + 2] = static_cast<uint8_t>((ctx->state[i] >> 8) & 0xFF);
        out[i * 4 + 3] = static_cast<uint8_t>(ctx->state[i] & 0xFF);
    }
}

void sha256(const uint8_t *data, size_t length, uint8_t *out) {
    Sha256Ctx ctx;
    sha256Init(&ctx);
    sha256Update(&ctx, data, length);
    sha256Final(&ctx, out);
}

void hmacSha256(const uint8_t *key, size_t keyLength, const uint8_t *data, size_t dataLength, uint8_t *out) {
    uint8_t normalized[64];
    memset(normalized, 0, sizeof(normalized));
    if (keyLength > 64) {
        sha256(key, keyLength, normalized);
    } else {
        memcpy(normalized, key, keyLength);
    }

    uint8_t innerPad[64];
    uint8_t outerPad[64];
    for (int i = 0; i < 64; i++) {
        innerPad[i] = static_cast<uint8_t>(normalized[i] ^ 0x36);
        outerPad[i] = static_cast<uint8_t>(normalized[i] ^ 0x5c);
    }

    uint8_t innerHash[32];
    Sha256Ctx ctx;
    sha256Init(&ctx);
    sha256Update(&ctx, innerPad, 64);
    sha256Update(&ctx, data, dataLength);
    sha256Final(&ctx, innerHash);

    sha256Init(&ctx);
    sha256Update(&ctx, outerPad, 64);
    sha256Update(&ctx, innerHash, 32);
    sha256Final(&ctx, out);

    memset(normalized, 0, sizeof(normalized));
    memset(innerPad, 0, sizeof(innerPad));
    memset(outerPad, 0, sizeof(outerPad));
    memset(innerHash, 0, sizeof(innerHash));
}

void secureZero(void *data, size_t length) {
    volatile uint8_t *ptr = static_cast<volatile uint8_t *>(data);
    while (length > 0) {
        *ptr++ = 0;
        length--;
    }
}

bool constantTimeEquals(const uint8_t *a, const uint8_t *b, size_t length) {
    uint8_t diff = 0;
    for (size_t i = 0; i < length; i++) {
        diff |= static_cast<uint8_t>(a[i] ^ b[i]);
    }
    return diff == 0;
}

void appendU64(std::vector<uint8_t> &out, uint64_t value) {
    for (int i = 7; i >= 0; i--) {
        out.push_back(static_cast<uint8_t>((value >> (i * 8)) & 0xFF));
    }
}

void deriveWrapKey(
    const uint8_t *certHash,
    const std::string &packageName,
    int64_t versionCode,
    const uint8_t *salt,
    const uint8_t *tag,
    uint8_t *out
) {
    std::vector<uint8_t> material;
    material.reserve(CERT_HASH_SIZE + packageName.size() + 8 + SALT_SIZE + TAG_SIZE);
    material.insert(material.end(), certHash, certHash + CERT_HASH_SIZE);
    material.insert(material.end(), packageName.begin(), packageName.end());
    appendU64(material, static_cast<uint64_t>(versionCode));
    material.insert(material.end(), salt, salt + SALT_SIZE);
    material.insert(material.end(), tag, tag + TAG_SIZE);
    hmacSha256(getActivePepper(), 32, material.data(), material.size(), out);
    secureZero(material.data(), material.size());
}

void buildKeystream(const uint8_t *wrapKey, uint8_t *out) {
    uint8_t labelOne = 1;
    uint8_t labelTwo = 2;
    hmacSha256(wrapKey, 32, &labelOne, 1, out);
    hmacSha256(wrapKey, 32, &labelTwo, 1, out + 32);
}

bool unwrapBlob(
    const uint8_t *certHash,
    const std::string &packageName,
    int64_t versionCode,
    const uint8_t *blob,
    uint8_t *keyOut
) {
    uint8_t checksum[CHECKSUM_SIZE];
    sha256(blob, BLOB_CHECKSUM_OFFSET, checksum);
    if (!constantTimeEquals(checksum, blob + BLOB_CHECKSUM_OFFSET, CHECKSUM_SIZE)) {
        secureZero(checksum, sizeof(checksum));
        return false;
    }

    uint8_t wrapKey[32];
    uint8_t keystream[64];
    deriveWrapKey(certHash, packageName, versionCode,
                  blob + BLOB_SALT_OFFSET, blob + BLOB_TAG_OFFSET, wrapKey);
    buildKeystream(wrapKey, keystream);
    for (size_t i = 0; i < WRAPPED_SIZE; i++) {
        keyOut[i] = static_cast<uint8_t>(blob[BLOB_WRAPPED_OFFSET + i] ^ keystream[i]);
    }

    secureZero(checksum, sizeof(checksum));
    secureZero(wrapKey, sizeof(wrapKey));
    secureZero(keystream, sizeof(keystream));
    return true;
}

bool blobFromJava(JNIEnv *env, jbyteArray array, uint8_t *out, size_t expected) {
    if (array == nullptr) return false;
    jsize length = env->GetArrayLength(array);
    if (length != static_cast<jsize>(expected)) return false;
    jbyte *data = env->GetByteArrayElements(array, nullptr);
    if (data == nullptr) return false;
    memcpy(out, data, expected);
    env->ReleaseByteArrayElements(array, data, JNI_ABORT);
    return true;
}

jbyteArray bytesToJava(JNIEnv *env, const uint8_t *data, size_t length) {
    jbyteArray result = env->NewByteArray(static_cast<jsize>(length));
    if (result == nullptr) return nullptr;
    env->SetByteArrayRegion(result, 0, static_cast<jsize>(length), reinterpret_cast<const jbyte *>(data));
    return result;
}

bool readRuntimeBinding(
    JNIEnv *env,
    jobject context,
    std::string &packageName,
    int64_t &versionCode,
    uint8_t *certHashOut
) {
    jclass contextClass = env->GetObjectClass(context);
    if (contextClass == nullptr) return false;

    jmethodID getPackageName = env->GetMethodID(contextClass, "getPackageName", "()Ljava/lang/String;");
    jmethodID getPackageManager = env->GetMethodID(contextClass, "getPackageManager",
                                                    "()Landroid/content/pm/PackageManager;");
    if (getPackageName == nullptr || getPackageManager == nullptr) return false;

    jstring packageString = static_cast<jstring>(env->CallObjectMethod(context, getPackageName));
    jobject packageManager = env->CallObjectMethod(context, getPackageManager);
    if (env->ExceptionCheck() || packageString == nullptr || packageManager == nullptr) {
        env->ExceptionClear();
        return false;
    }

    const char *packageChars = env->GetStringUTFChars(packageString, nullptr);
    if (packageChars == nullptr) return false;
    packageName.assign(packageChars);
    env->ReleaseStringUTFChars(packageString, packageChars);

    jclass managerClass = env->GetObjectClass(packageManager);
    jmethodID getPackageInfo = env->GetMethodID(
        managerClass, "getPackageInfo",
        "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    if (getPackageInfo == nullptr) return false;

    jobject packageInfo = env->CallObjectMethod(packageManager, getPackageInfo, packageString,
                                                static_cast<jint>(0x08000000));
    if (env->ExceptionCheck() || packageInfo == nullptr) {
        env->ExceptionClear();
        return false;
    }

    jclass packageInfoClass = env->GetObjectClass(packageInfo);
    jfieldID versionField = env->GetFieldID(packageInfoClass, "longVersionCode", "J");
    if (versionField != nullptr) {
        versionCode = env->GetLongField(packageInfo, versionField);
    } else {
        env->ExceptionClear();
        jfieldID legacyVersionField = env->GetFieldID(packageInfoClass, "versionCode", "I");
        if (legacyVersionField == nullptr) return false;
        versionCode = env->GetIntField(packageInfo, legacyVersionField);
    }

    jfieldID signingInfoField = env->GetFieldID(
        packageInfoClass, "signingInfo", "Landroid/content/pm/SigningInfo;");
    if (signingInfoField == nullptr) return false;
    jobject signingInfo = env->GetObjectField(packageInfo, signingInfoField);
    if (signingInfo == nullptr) return false;

    jclass signingInfoClass = env->GetObjectClass(signingInfo);
    jmethodID getSigners = env->GetMethodID(signingInfoClass, "getApkContentsSigners",
                                            "()[Landroid/content/pm/Signature;");
    if (getSigners == nullptr) return false;
    jobjectArray signers = static_cast<jobjectArray>(env->CallObjectMethod(signingInfo, getSigners));
    if (env->ExceptionCheck() || signers == nullptr) {
        env->ExceptionClear();
        return false;
    }
    if (env->GetArrayLength(signers) < 1) return false;

    jobject signature = env->GetObjectArrayElement(signers, 0);
    if (signature == nullptr) return false;
    jclass signatureClass = env->GetObjectClass(signature);
    jmethodID toByteArray = env->GetMethodID(signatureClass, "toByteArray", "()[B");
    if (toByteArray == nullptr) return false;

    jbyteArray der = static_cast<jbyteArray>(env->CallObjectMethod(signature, toByteArray));
    if (env->ExceptionCheck() || der == nullptr) {
        env->ExceptionClear();
        return false;
    }

    jsize derLength = env->GetArrayLength(der);
    if (derLength <= 0) return false;
    jbyte *derBytes = env->GetByteArrayElements(der, nullptr);
    if (derBytes == nullptr) return false;
    sha256(reinterpret_cast<const uint8_t *>(derBytes), static_cast<size_t>(derLength), certHashOut);
    env->ReleaseByteArrayElements(der, derBytes, JNI_ABORT);
    return true;
}

bool selfTest() {
    static const uint8_t expectedSha[32] = {
        0xba, 0x78, 0x16, 0xbf, 0x8f, 0x01, 0xcf, 0xea,
        0x41, 0x41, 0x40, 0xde, 0x5d, 0xae, 0x22, 0x23,
        0xb0, 0x03, 0x61, 0xa3, 0x96, 0x17, 0x7a, 0x9c,
        0xb4, 0x10, 0xff, 0x61, 0xf2, 0x00, 0x15, 0xad
    };
    uint8_t shaOut[32];
    const uint8_t abc[] = {'a', 'b', 'c'};
    sha256(abc, sizeof(abc), shaOut);
    if (!constantTimeEquals(shaOut, expectedSha, 32)) return false;

    static const uint8_t expectedHmac[32] = {
        0xf7, 0xbc, 0x83, 0xf4, 0x30, 0x53, 0x84, 0x24,
        0xb1, 0x32, 0x98, 0xe6, 0xaa, 0x6f, 0xb1, 0x43,
        0xef, 0x4d, 0x59, 0xa1, 0x49, 0x46, 0x17, 0x59,
        0x97, 0x47, 0x9d, 0xbc, 0x2d, 0x1a, 0x3c, 0xd8
    };
    const uint8_t hmacKey[] = {'k', 'e', 'y'};
    const char *message = "The quick brown fox jumps over the lazy dog";
    uint8_t hmacOut[32];
    hmacSha256(hmacKey, sizeof(hmacKey),
               reinterpret_cast<const uint8_t *>(message), strlen(message), hmacOut);
    return constantTimeEquals(hmacOut, expectedHmac, 32);
}

}

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *, void *) {
    if (!selfTest()) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Self-test failed");
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_org_catrobat_catroid_apkbuildV3_NativeKeyVault_nativeWrapForBuild(
    JNIEnv *env,
    jobject,
    jbyteArray certHash,
    jstring packageName,
    jlong versionCode,
    jbyteArray keyMaterial,
    jbyteArray salt,
    jbyteArray tag
) {
    uint8_t cert[CERT_HASH_SIZE];
    uint8_t key[KEY_MATERIAL_SIZE];
    uint8_t saltBytes[SALT_SIZE];
    uint8_t tagBytes[TAG_SIZE];
    if (!blobFromJava(env, certHash, cert, CERT_HASH_SIZE)) return nullptr;
    if (!blobFromJava(env, keyMaterial, key, KEY_MATERIAL_SIZE)) return nullptr;
    if (!blobFromJava(env, salt, saltBytes, SALT_SIZE)) return nullptr;
    if (!blobFromJava(env, tag, tagBytes, TAG_SIZE)) return nullptr;
    if (packageName == nullptr) return nullptr;

    const char *packageChars = env->GetStringUTFChars(packageName, nullptr);
    if (packageChars == nullptr) return nullptr;
    std::string package(packageChars);
    env->ReleaseStringUTFChars(packageName, packageChars);

    uint8_t blob[BLOB_SIZE];
    memset(blob, 0, sizeof(blob));
    memcpy(blob + BLOB_TAG_OFFSET, tagBytes, TAG_SIZE);
    memcpy(blob + BLOB_SALT_OFFSET, saltBytes, SALT_SIZE);

    uint8_t wrapKey[32];
    uint8_t keystream[64];
    deriveWrapKey(cert, package, static_cast<int64_t>(versionCode), saltBytes, tagBytes, wrapKey);
    buildKeystream(wrapKey, keystream);
    for (size_t i = 0; i < WRAPPED_SIZE; i++) {
        blob[BLOB_WRAPPED_OFFSET + i] = static_cast<uint8_t>(key[i] ^ keystream[i]);
    }
    sha256(blob, BLOB_CHECKSUM_OFFSET, blob + BLOB_CHECKSUM_OFFSET);

    jbyteArray result = bytesToJava(env, blob, BLOB_SIZE);

    secureZero(cert, sizeof(cert));
    secureZero(key, sizeof(key));
    secureZero(saltBytes, sizeof(saltBytes));
    secureZero(tagBytes, sizeof(tagBytes));
    secureZero(blob, sizeof(blob));
    secureZero(wrapKey, sizeof(wrapKey));
    secureZero(keystream, sizeof(keystream));
    return result;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_org_catrobat_catroid_apkbuildV3_NativeKeyVault_nativeVerifyForBuild(
    JNIEnv *env,
    jobject,
    jbyteArray certHash,
    jstring packageName,
    jlong versionCode,
    jbyteArray keyMaterial,
    jbyteArray blob
) {
    uint8_t cert[CERT_HASH_SIZE];
    uint8_t key[KEY_MATERIAL_SIZE];
    uint8_t blobBytes[BLOB_SIZE];
    if (!blobFromJava(env, certHash, cert, CERT_HASH_SIZE)) return JNI_FALSE;
    if (!blobFromJava(env, keyMaterial, key, KEY_MATERIAL_SIZE)) return JNI_FALSE;
    if (!blobFromJava(env, blob, blobBytes, BLOB_SIZE)) return JNI_FALSE;
    if (packageName == nullptr) return JNI_FALSE;

    const char *packageChars = env->GetStringUTFChars(packageName, nullptr);
    if (packageChars == nullptr) return JNI_FALSE;
    std::string package(packageChars);
    env->ReleaseStringUTFChars(packageName, packageChars);

    uint8_t unwrapped[KEY_MATERIAL_SIZE];
    bool ok = unwrapBlob(cert, package, static_cast<int64_t>(versionCode), blobBytes, unwrapped);
    if (ok) {
        ok = constantTimeEquals(unwrapped, key, KEY_MATERIAL_SIZE);
    }
    if (!ok) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Key blob verification failed");
    }

    secureZero(cert, sizeof(cert));
    secureZero(key, sizeof(key));
    secureZero(blobBytes, sizeof(blobBytes));
    secureZero(unwrapped, sizeof(unwrapped));
    return ok ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_org_catrobat_catroid_apkbuildV3_NativeKeyVault_nativeUnwrapForRuntime(
    JNIEnv *env,
    jobject,
    jobject context,
    jbyteArray blob
) {
    uint8_t blobBytes[BLOB_SIZE];
    if (!blobFromJava(env, blob, blobBytes, BLOB_SIZE)) return nullptr;

    std::string packageName;
    int64_t versionCode = 0;
    uint8_t cert[CERT_HASH_SIZE];
    memset(cert, 0, sizeof(cert));
    if (!readRuntimeBinding(env, context, packageName, versionCode, cert)) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Runtime signing binding unavailable");
        secureZero(blobBytes, sizeof(blobBytes));
        secureZero(cert, sizeof(cert));
        return nullptr;
    }

    uint8_t key[KEY_MATERIAL_SIZE];
    bool ok = unwrapBlob(cert, packageName, versionCode, blobBytes, key);
    if (!ok) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Key blob unwrap failed");
    }
    jbyteArray result = ok ? bytesToJava(env, key, KEY_MATERIAL_SIZE) : nullptr;

    secureZero(blobBytes, sizeof(blobBytes));
    secureZero(cert, sizeof(cert));
    secureZero(key, sizeof(key));
    return result;
}

extern "C" JNIEXPORT void JNICALL
Java_org_catrobat_catroid_apkbuildV3_NativeKeyVault_nativeSetPepperOverride(
    JNIEnv *env,
    jobject,
    jbyteArray pepper
) {
    uint8_t pepperBytes[32];
    if (!blobFromJava(env, pepper, pepperBytes, 32)) return;
    setPepperOverride(pepperBytes, 32);
    secureZero(pepperBytes, sizeof(pepperBytes));
}
