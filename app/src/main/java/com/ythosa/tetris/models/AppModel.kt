package com.ythosa.tetris.models

import android.graphics.Point
import com.ythosa.tetris.constants.CellConstants
import com.ythosa.tetris.constants.FieldConstants
import com.ythosa.tetris.helpers.arrayOf2dOfByte
import com.ythosa.tetris.storage.AppPreferences

class AppModel {
    var score: Int = 0
    private var preferences: AppPreferences? = null

    var currentBlock: Block? = null
    var currentState: String = Statuses.AWAITING_START.name

    private var field: Array<ByteArray> = arrayOf2dOfByte(
        FieldConstants.ROW_COUNT.value,
        FieldConstants.COLUMN_COUNT.value
    )

    fun setPreferences(preferences: AppPreferences?) {
        this.preferences = preferences
    }

    fun getCellStatus(row: Int, column: Int): Byte? {
        return field[row][column]
    }

    private fun setCellStatus(row: Int, column: Int, status: Byte?) {
        if (status != null) {
            field[row][column] = status
        }
    }

    fun isGameAwaitingStart(): Boolean {
        return this.currentState == Statuses.AWAITING_START.name
    }

    fun isGameActive(): Boolean {
        return this.currentState == Statuses.ACTIVE.name
    }

    fun isGameOver(): Boolean {
        return this.currentState == Statuses.OVER.name
    }

    private fun boostScore() {
        score += 10
        if (score > preferences?.getHighScore() as Int) {
            preferences?.saveHighScore(score)
        }
    }

    private fun generateNextBlock() {
        currentBlock = Block.createBlock()
    }

    private fun validTranslation(position: Point, shape: Array<ByteArray>): Boolean {
        return if (position.y < 0 || position.x < 0) {
            false
        } else if (position.y + shape.size > FieldConstants.ROW_COUNT.value) {
            false
        } else if (position.x + shape[0].size > FieldConstants.COLUMN_COUNT.value) {
            false
        } else {
            for (i in shape.indices) {
                for (j in shape[i].indices) {
                    val x = position.x + j
                    val y = position.y + i
                    if (CellConstants.EMPTY.value != shape[i][j]
                        && CellConstants.EMPTY.value != field[y][x]
                    )
                        return false
                }
            }
            true
        }
    }

    private fun moveValid(position: Point, frameNumber: Int?): Boolean {
        return validTranslation(
            position,
            currentBlock?.getShape(frameNumber as Int) as Array<ByteArray>
        )
    }

    fun generateField(action: String) {
        if (!isGameActive()) {
            return
        }

        resetField()
        var frameNumber: Int? = currentBlock?.frameNumber
        val coordinate: Point? = Point()
        coordinate?.x = currentBlock?.position?.x
        coordinate?.y = currentBlock?.position?.y

        when (action) {
            Motions.LEFT.name -> {
                coordinate?.x = currentBlock?.position?.x?.minus(1)
            }
            Motions.RIGHT.name -> {
                coordinate?.x = currentBlock?.position?.x?.plus(1)
            }
            Motions.DOWN.name -> {
                coordinate?.y = currentBlock?.position?.y?.plus(1)
            }
            Motions.ROTATE.name -> {
                frameNumber = currentBlock?.frameCount?.let {
                    frameNumber?.plus(1)?.rem(it)
                }
            }
        }

        if (!moveValid(coordinate as Point, frameNumber)) {
            translateBlock(
                currentBlock?.position as Point,
                currentBlock?.frameNumber as Int
            )
            if (action == Motions.DOWN.name) {
                boostScore()
                persistCellData()
                accessField()
                generateNextBlock()
                if (!blockAdditionPossible()) {
                    currentState = Statuses.OVER.name
                    currentBlock = null
                    resetField(false)
                }
            }
        } else if (frameNumber != null) {
            translateBlock(coordinate, frameNumber)
            currentBlock?.setState(frameNumber, coordinate)
        }
    }

    private fun resetField(ephemeralCellsOnly: Boolean = true) {
        for (i in 0 until FieldConstants.ROW_COUNT.value) {
            (0 until FieldConstants.COLUMN_COUNT.value)
                .filter { !ephemeralCellsOnly || field[i][it] == CellConstants.EPHEMERAL.value }
                .forEach { field[i][it] = CellConstants.EMPTY.value }
        }
    }

    private fun persistCellData() {
        for (i in field.indices) {
            for (j in field[i].indices) {
                var status = getCellStatus(i, j)
                if (status == CellConstants.EPHEMERAL.value) {
                    status = currentBlock?.getStaticValue()
                    setCellStatus(i, j, status)
                }
            }
        }
    }

    private fun accessField() {
        for (i in field.indices) {
            var emptyCells = 0

            for (j in field[i].indices) {
                if (getCellStatus(i, j) == CellConstants.EMPTY.value) {
                    emptyCells++
                }
            }

            if (emptyCells == 0) {
                shiftRows(i)
            }
        }
    }

    private fun translateBlock(position: Point, frameNumber: Int) {
        synchronized(field) {
            val shape: Array<ByteArray> = currentBlock?.getShape(frameNumber) ?: return

            for (i in shape.indices) {
                for (j in shape[i].indices) {
                    val x = position.x + j
                    val y = position.y + i

                    if (shape[i][j] != CellConstants.EMPTY.value) {
                        field[y][x] = shape[i][j]
                    }
                }
            }
        }
    }

    private fun blockAdditionPossible(): Boolean {
        if (!moveValid(currentBlock?.position as Point, currentBlock?.frameNumber))
            return false

        return true
    }

    private fun shiftRows(nToRow: Int) {
        if (nToRow > 0) {
            for (j in nToRow - 1 downTo 0) {
                for (m in field[j].indices) {
                    setCellStatus(j + 1, m, getCellStatus(j, m))
                }
            }
        }

        for (j in field[0].indices) {
            setCellStatus(0, j, CellConstants.EMPTY.value)
        }
    }

    fun startGame() {
        if (isGameActive())
            return
        currentState = Statuses.ACTIVE.name
        generateNextBlock()
    }

    fun restartGame() {
        resetModel()
        startGame()
    }

    fun endGame() {
        score = 0
        currentState = Statuses.OVER.name
    }

    private fun resetModel() {
        resetField(false)
        currentState = Statuses.AWAITING_START.name
        score = 0
    }

    enum class Statuses {
        AWAITING_START, ACTIVE, INACTIVE, OVER
    }

    enum class Motions {
        LEFT, RIGHT, DOWN, ROTATE
    }
}