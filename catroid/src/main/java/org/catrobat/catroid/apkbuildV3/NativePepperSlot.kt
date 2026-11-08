package org.catrobat.catroid.apkbuildV3

internal object NativePepperSlot {
    val SENTINEL = byteArrayOf(
        0x4e, 0x43, 0x56, 0x34, 0x5f, 0x50, 0x45, 0x50,
        0x50, 0x45, 0x52, 0x5f, 0x53, 0x4c, 0x4f, 0x54,
        0x5f, 0x56, 0x31, 0x21, 0xa5.toByte(), 0x5a, 0xc3.toByte(), 0x3c,
        0x5a, 0xa5.toByte(), 0x3c, 0xc3.toByte(), 0x00, 0x00, 0x00, 0x00
    )

    val PEPPER_LIB_REGEX = Regex("^lib/[^/]+/libapk_crypto\\.so$")

    fun patchCount(bytes: ByteArray): Int {
        if (bytes.size < SENTINEL.size) return 0
        var count = 0
        var from = 0
        while (true) {
            from = indexOf(bytes, SENTINEL, from)
            if (from < 0) break
            count++
            from += SENTINEL.size
        }
        return count
    }

    fun patch(bytes: ByteArray, pepper: ByteArray): ByteArray {
        require(pepper.size == NativeKeyVault.PEPPER_SIZE) { "pepper must be ${NativeKeyVault.PEPPER_SIZE} bytes" }
        require(SENTINEL.size == NativeKeyVault.PEPPER_SIZE) { "sentinel size mismatch" }
        val occurrences = patchCount(bytes)
        require(occurrences == 1) { "expected exactly one pepper slot, found $occurrences" }
        val copy = bytes.copyOf()
        val at = indexOf(copy, SENTINEL, 0)
        System.arraycopy(pepper, 0, copy, at, NativeKeyVault.PEPPER_SIZE)
        return copy
    }

    private fun indexOf(haystack: ByteArray, needle: ByteArray, fromIndex: Int): Int {
        val limit = haystack.size - needle.size
        if (fromIndex > limit) return -1
        outer@ for (i in fromIndex..limit) {
            for (j in needle.indices) {
                if (haystack[i + j] != needle[j]) continue@outer
            }
            return i
        }
        return -1
    }
}
