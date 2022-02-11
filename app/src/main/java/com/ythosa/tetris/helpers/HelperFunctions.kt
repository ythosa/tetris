package com.ythosa.tetris.helpers

fun arrayOf2dOfByte(sizeOuter: Int, sizeInner: Int): Array<ByteArray> = Array(sizeOuter) {
    ByteArray(sizeInner)
}
