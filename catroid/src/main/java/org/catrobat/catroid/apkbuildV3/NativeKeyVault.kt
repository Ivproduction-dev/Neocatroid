package org.catrobat.catroid.apkbuildV3

import android.content.Context
import android.util.Log

object NativeKeyVault {
    private const val TAG = "NativeKeyVault"

    const val CERT_HASH_SIZE = 32
    const val BLOB_SIZE = 136
    const val TAG_SIZE = 8
    const val SALT_SIZE = 16
    const val PEPPER_SIZE = 32

    private val loaded: Boolean by lazy {
        try {
            System.loadLibrary("apk_crypto")
            true
        } catch (t: Throwable) {
            Log.e(TAG, "Native crypto library unavailable", t)
            false
        }
    }

    private external fun nativeWrapForBuild(
        certSha256: ByteArray,
        packageName: String,
        versionCode: Long,
        keyMaterial: ByteArray,
        salt: ByteArray,
        tag: ByteArray
    ): ByteArray?

    private external fun nativeVerifyForBuild(
        certSha256: ByteArray,
        packageName: String,
        versionCode: Long,
        keyMaterial: ByteArray,
        blob: ByteArray
    ): Boolean

    private external fun nativeUnwrapForRuntime(context: Context, blob: ByteArray): ByteArray?

    private external fun nativeSetPepperOverride(pepper: ByteArray)

    fun setPepperOverride(pepper: ByteArray) {
        require(pepper.size == PEPPER_SIZE) { "Invalid pepper size: ${pepper.size}, expected $PEPPER_SIZE" }
        if (!loaded) throw IllegalStateException("Native crypto library is not available")
        nativeSetPepperOverride(pepper)
    }

    fun wrapForBuild(
        certSha256: ByteArray,
        packageName: String,
        versionCode: Long,
        keyMaterial: ByteArray,
        salt: ByteArray,
        tag: ByteArray
    ): ByteArray {
        require(certSha256.size == CERT_HASH_SIZE) { "Invalid certificate hash size" }
        require(packageName.isNotBlank()) { "Package name is empty" }
        require(versionCode >= 0) { "Invalid version code" }
        require(keyMaterial.size == KeyMaterialV4.SIZE) { "Invalid key material size" }
        require(salt.size == SALT_SIZE) { "Invalid salt size" }
        require(tag.size == TAG_SIZE) { "Invalid tag size" }
        if (!loaded) throw IllegalStateException("Native crypto library is not available")
        return nativeWrapForBuild(certSha256, packageName, versionCode, keyMaterial, salt, tag)
            ?: throw IllegalStateException("Native key wrapping failed")
    }

    fun verifyForBuild(
        certSha256: ByteArray,
        packageName: String,
        versionCode: Long,
        keyMaterial: ByteArray,
        blob: ByteArray
    ): Boolean {
        if (certSha256.size != CERT_HASH_SIZE) return false
        if (packageName.isBlank()) return false
        if (versionCode < 0) return false
        if (keyMaterial.size != KeyMaterialV4.SIZE) return false
        if (blob.size != BLOB_SIZE) return false
        if (!loaded) return false
        return try {
            nativeVerifyForBuild(certSha256, packageName, versionCode, keyMaterial, blob)
        } catch (t: Throwable) {
            Log.e(TAG, "Native key verification failed", t)
            false
        }
    }

    fun unwrapForRuntime(context: Context, blob: ByteArray): ByteArray? {
        if (blob.size != BLOB_SIZE) return null
        if (!loaded) return null
        return try {
            nativeUnwrapForRuntime(context, blob)
        } catch (t: Throwable) {
            Log.e(TAG, "Native key unwrap failed", t)
            null
        }
    }
}
