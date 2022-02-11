package com.ythosa.tetris.models

import com.ythosa.tetris.helpers.arrayOf2dOfByte

class Frame(private val width: Int) {
    private val data: ArrayList<ByteArray> = ArrayList()

    fun addRow(byteStr: String): Frame {
        val row = ByteArray(byteStr.length)
        byteStr.indices.forEach {
            row[it] = "${byteStr[it]}".toByte()
        }
        data.add(row)

        return this
    }

    fun as2dByteArray(): Array<ByteArray> {
        val bytes = arrayOf2dOfByte(data.size, width)

        return data.toArray(bytes)
    }
}
