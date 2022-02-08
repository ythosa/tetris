package com.ythosa.tetris.storage

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(ctx: Context) {
    private val highScoreName = "HIGH_SCORE"
    private val highScoreDefaultValue = 0

    var data: SharedPreferences = ctx.getSharedPreferences(
        "APP_PREFERENCES", Context.MODE_PRIVATE
    )

    fun saveHighScore(highScore: Int) {
        data.edit().putInt(highScoreName, highScore).apply()
    }

    fun getHighScore(): Int {
        return data.getInt(highScoreName, highScoreDefaultValue)
    }

    fun clearHighScore() {
        data.edit().putInt(highScoreName, highScoreDefaultValue).apply()
    }
}
