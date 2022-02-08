package com.ythosa.tetris

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    var tvHighScore: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val btnNewGame = findViewById<Button>(R.id.btn_new_game)
        btnNewGame.setOnClickListener(this::onBtnNewGameClick)

        val btnResetScore = findViewById<Button>(R.id.btn_reset_score)
        btnResetScore.setOnClickListener(this::onBtnResetScoreClick)

        val btnExit = findViewById<Button>(R.id.btn_exit)
        btnExit.setOnClickListener(this::onBtnExitClick)

        tvHighScore = findViewById(R.id.tv_high_score)
    }

    private fun onBtnNewGameClick(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    private fun onBtnResetScoreClick(view: View) {

    }

    private fun onBtnExitClick(view: View) {
        exitProcess(0)
    }
}
