package org.catrobat.catroid.apkbuildV3

import java.security.SecureRandom
import javax.crypto.spec.SecretKeySpec

object KeyMaterialV4 {
    const val SIZE = 64
    const val ENC_KEY_SIZE = 32
    const val MAC_KEY_SIZE = 32

    fun generate(random: SecureRandom = SecureRandom()): ByteArray {
        return ByteArray(SIZE).also { random.nextBytes(it) }
    }

    fun encKeySpec(material: ByteArray): SecretKeySpec {
        require(material.size == SIZE) { "Invalid key material size: ${material.size}" }
        return SecretKeySpec(material, 0, ENC_KEY_SIZE, "AES")
    }

    fun macKeySpec(material: ByteArray): SecretKeySpec {
        require(material.size == SIZE) { "Invalid key material size: ${material.size}" }
        return SecretKeySpec(material, ENC_KEY_SIZE, MAC_KEY_SIZE, "HmacSHA256")
    }

    fun isValid(material: ByteArray?): Boolean {
        if (material == null || material.size != SIZE) return false
        return material.any { it.toInt() != 0 }
    }

    fun zero(material: ByteArray?) {
        material?.fill(0)
    }
}
