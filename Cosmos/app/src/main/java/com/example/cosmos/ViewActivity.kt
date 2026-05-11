package com.example.cosmos

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Random

class ViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        val rvBoard = findViewById<RecyclerView>(R.id.rv_board)
        rvBoard.layoutManager = GridLayoutManager(this, 10)

        val cells = generateDummyData()
        rvBoard.adapter = BoardAdapter(cells)

        updateLegend(cells)
    }

    private fun generateDummyData(): List<BoardCell> {
        val random = Random()
        val cells = mutableListOf<BoardCell>()
        for (i in 0 until 100) {
            val owner = if (random.nextFloat() > 0.7f) random.nextInt(4) + 1 else 0
            val planet = random.nextFloat() > 0.9f
            val ships = IntArray(4) { if (random.nextFloat() > 0.8f) random.nextInt(5) else 0 }
            cells.add(BoardCell(owner, planet, ships))
        }
        return cells
    }

    private fun updateLegend(cells: List<BoardCell>) {
        val counts = IntArray(5) // 0 to 4
        cells.forEach { counts[it.teamOwner]++ }

        findViewById<TextView>(R.id.tv_legend_team1).text = "● Equipo 1: ${counts[1]} casillas"
        findViewById<TextView>(R.id.tv_legend_team2).text = "● Equipo 2: ${counts[2]} casillas"
        findViewById<TextView>(R.id.tv_legend_team3).text = "● Equipo 3: ${counts[3]} casillas"
        findViewById<TextView>(R.id.tv_legend_team4).text = "● Equipo 4: ${counts[4]} casillas"
    }
}