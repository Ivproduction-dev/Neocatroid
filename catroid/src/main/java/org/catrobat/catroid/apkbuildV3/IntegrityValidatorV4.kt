package org.catrobat.catroid.apkbuildV3

import android.util.Log
import java.io.File

object IntegrityValidatorV4 {
    private const val TAG = "IntegrityValidatorV4"

    fun validate(encryptedFile: File, keyMaterial: ByteArray): Boolean {
        if (!KeyMaterialV4.isValid(keyMaterial)) return false
        return ProjectEncryptorV4.validate(encryptedFile, keyMaterial)
    }

    fun verifyKeyBlob(
        keyBlob: ByteArray,
        keyMaterial: ByteArray,
        certSha256: ByteArray,
        packageName: String,
        versionCode: Long
    ): Boolean {
        if (!KeyMaterialV4.isValid(keyMaterial)) return false
        return try {
            NativeKeyVault.verifyForBuild(
                certSha256 = certSha256,
                packageName = packageName,
                versionCode = versionCode,
                keyMaterial = keyMaterial,
                blob = keyBlob
            )
        } catch (e: Throwable) {
            Log.e(TAG, "Key blob verification failed", e)
            false
        }
    }
}
