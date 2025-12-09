package com.kmp.vayone.util

class MD5 {
    companion object {
        private val S = intArrayOf(
            7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
            5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
            4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
            6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21
        )

        private val K = IntArray(64) { i ->
            ((1L shl 32) * kotlin.math.abs(kotlin.math.sin((i + 1).toDouble()))).toLong().toInt()
        }

        fun hash(input: String): String {
            val bytes = input.encodeToByteArray()
            return hash(bytes)
        }

        fun hash(input: ByteArray): String {
            var a0 = 0x67452301
            var b0 = 0xEFCDAB89.toInt()
            var c0 = 0x98BADCFE.toInt()
            var d0 = 0x10325476

            val msgLen = input.size
            val numBlocks = ((msgLen + 8) ushr 6) + 1
            val totalLen = numBlocks shl 6
            val paddingBytes = ByteArray(totalLen - msgLen)
            paddingBytes[0] = 0x80.toByte()

            var bits = (msgLen.toLong() shl 3)
            for (i in 0 until 8) {
                paddingBytes[paddingBytes.size - 8 + i] = bits.toByte()
                bits = bits ushr 8
            }

            val msg = input + paddingBytes

            for (i in 0 until numBlocks) {
                val block = msg.sliceArray(i * 64 until (i + 1) * 64)
                val M = IntArray(16)
                for (j in 0 until 16) {
                    M[j] = (block[j * 4].toInt() and 0xFF) or
                            ((block[j * 4 + 1].toInt() and 0xFF) shl 8) or
                            ((block[j * 4 + 2].toInt() and 0xFF) shl 16) or
                            ((block[j * 4 + 3].toInt() and 0xFF) shl 24)
                }

                var A = a0
                var B = b0
                var C = c0
                var D = d0

                for (j in 0 until 64) {
                    val f: Int
                    val g: Int
                    when (j) {
                        in 0..15 -> {
                            f = (B and C) or (B.inv() and D)
                            g = j
                        }
                        in 16..31 -> {
                            f = (D and B) or (D.inv() and C)
                            g = (5 * j + 1) % 16
                        }
                        in 32..47 -> {
                            f = B xor C xor D
                            g = (3 * j + 5) % 16
                        }
                        else -> {
                            f = C xor (B or D.inv())
                            g = (7 * j) % 16
                        }
                    }

                    val temp = D
                    D = C
                    C = B
                    B = B + leftRotate(A + f + K[j] + M[g], S[j])
                    A = temp
                }

                a0 += A
                b0 += B
                c0 += C
                d0 += D
            }

            return toHexString(a0) + toHexString(b0) + toHexString(c0) + toHexString(d0)
        }

        private fun leftRotate(x: Int, n: Int): Int {
            return (x shl n) or (x ushr (32 - n))
        }

        private fun toHexString(value: Int): String {
            val bytes = byteArrayOf(
                (value and 0xFF).toByte(),
                ((value shr 8) and 0xFF).toByte(),
                ((value shr 16) and 0xFF).toByte(),
                ((value shr 24) and 0xFF).toByte()
            )
            return bytes.joinToString("") { byte ->
                val hex = (byte.toInt() and 0xFF).toString(16)
                if (hex.length == 1) "0$hex" else hex
            }
        }
    }
}