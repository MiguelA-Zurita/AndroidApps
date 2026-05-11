package com.example.cosmos

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GameActivity : AppCompatActivity() {
    private var playerX = 5
    private var playerY = 5
    private lateinit var adapter: PlayerBoardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val rvBoard = findViewById<RecyclerView>(R.id.rv_game_board)
        rvBoard.layoutManager = GridLayoutManager(this, 10)
        adapter = PlayerBoardAdapter(playerX to playerY)
        rvBoard.adapter = adapter

        findViewById<ImageButton>(R.id.btn_move_up).setOnClickListener {
            if (playerY > 0) {
                playerY--
                updateBoard()
            }
        }
        findViewById<ImageButton>(R.id.btn_move_down).setOnClickListener {
            if (playerY < 9) {
                playerY++
                updateBoard()
            }
        }
        findViewById<ImageButton>(R.id.btn_move_left).setOnClickListener {
            if (playerX > 0) {
                playerX--
                updateBoard()
            }
        }
        findViewById<ImageButton>(R.id.btn_move_right).setOnClickListener {
            if (playerX < 9) {
                playerX++
                updateBoard()
            }
        }

        findViewById<Button>(R.id.btn_view_resources).setOnClickListener {
            val intent = Intent(this, CurrentResourcesActivity::class.java)
            startActivity(intent)
        }

        val chatContainer = findViewById<LinearLayout>(R.id.chat_container)
        val etMessage = findViewById<EditText>(R.id.et_chat_message)
        findViewById<ImageButton>(R.id.btn_send_chat).setOnClickListener {
            val msg = etMessage.text.toString()
            if (msg.isNotEmpty()) {
                val tv = TextView(this)
                tv.text = "[Tú]: $msg"
                tv.setTextColor(Color.GREEN)
                tv.textSize = 10f
                chatContainer.addView(tv)
                etMessage.text.clear()
            }
        }
    }

    private fun updateBoard() {
        adapter = PlayerBoardAdapter(playerX to playerY)
        findViewById<RecyclerView>(R.id.rv_game_board).adapter = adapter
    }
}
