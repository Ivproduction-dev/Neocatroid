package org.catrobat.catroid.apkbuildV3

import android.content.Context
import android.util.Log
import java.io.File
import java.security.KeyStore
import java.security.MessageDigest
import java.security.cert.X509Certificate

object SigningKeyManager {
    private const val TAG = "SigningKeyManager"
    const val ALIAS = "neocatroidv3"
    private const val DIR_NAME = "v3_signing"
    private const val KEYSTORE_FILE_NAME = "release_key.p12"
    private const val PREFS_NAME = "v3_signing"
    private const val PASSWORD_KEY = "keystore_password"

    fun keystorePassword(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        var password = prefs.getString(PASSWORD_KEY, "").orEmpty()
        if (password.isEmpty()) {
            val random = java.security.SecureRandom()
            val alphabet = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            password = (1..32).map { alphabet[random.nextInt(alphabet.size)] }.joinToString("")
            prefs.edit().putString(PASSWORD_KEY, password).apply()
        }
        return password
    }

    fun ensureKeystore(context: Context, generate: (File, String, String) -> Unit): File {
        val dir = File(context.filesDir, DIR_NAME)
        dir.mkdirs()
        val file = File(dir, KEYSTORE_FILE_NAME)
        if (file.exists() && loadsOk(file, keystorePassword(context))) {
            return file
        }
        if (file.exists()) {
            file.delete()
        }
        generate(file, ALIAS, keystorePassword(context))
        Log.i(TAG, "Постоянный ключ подписи создан: ${file.absolutePath}")
        return file
    }

    fun certificateSha256(keystoreFile: File, password: String): ByteArray {
        val keyStore = KeyStore.getInstance("PKCS12")
        keystoreFile.inputStream().use { keyStore.load(it, password.toCharArray()) }
        val certificate = keyStore.getCertificate(ALIAS) as? X509Certificate
            ?: throw IllegalStateException("Signing certificate not found")
        return MessageDigest.getInstance("SHA-256").digest(certificate.encoded)
    }

    fun ensureCertificateSha256(context: Context, generate: (File, String, String) -> Unit): ByteArray {
        val keystore = ensureKeystore(context, generate)
        return certificateSha256(keystore, keystorePassword(context))
    }

    private fun loadsOk(file: File, password: String): Boolean {
        return runCatching {
            val keyStore = KeyStore.getInstance("PKCS12")
            file.inputStream().use { keyStore.load(it, password.toCharArray()) }
            keyStore.containsAlias(ALIAS)
        }.getOrDefault(false)
    }
}
