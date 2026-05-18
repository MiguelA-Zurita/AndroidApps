package com.example.cosmos.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmos.adapter.BoardAdapter
import com.example.cosmos.model.BoardCell
import com.example.cosmos.R
import com.example.cosmos.helper.RetrofitHelper
import com.example.cosmos.model.GameBoardResponseDto
import com.example.cosmos.repository.CosmosRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class ViewActivity : AppCompatActivity() {

    private lateinit var repository: CosmosRepository
    private lateinit var cells: List<BoardCell>
    private var countdownJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        val api = RetrofitHelper().getApi(this)

        repository = CosmosRepository(api)

        val rvBoard = findViewById<RecyclerView>(R.id.rv_board)
        rvBoard.layoutManager = GridLayoutManager(this, 10)

        startPeriodicUpdate()
    }

    private fun startPeriodicUpdate() {
        lifecycleScope.launch {
            while (isActive) {
                try {
                    val gameData = repository.getGameBoard()

                    updateBoard(gameData)

                    val nextTurnInstant = Instant.parse(gameData.timestamp_proxim_torn)

                    findViewById<TextView>(R.id.tv_turn_info).text = getString(R.string.turn_format, gameData.torn_actual)

                    startCountdown(nextTurnInstant)

                    delay(10000)
                } catch (e: Exception) {
                    delay(5000)
                }
            }
        }
    }

    private fun updateBoard(data : GameBoardResponseDto) {
        cells = data.taulell.map { dto ->
            BoardCell(
                teamOwner = when (dto.color_dominant) {
                    "azul" -> 1
                    "rojo" -> 2
                    "verde" -> 3
                    else -> 0
                },
                hasPlanet = dto.planeta,
                ships = intArrayOf(
                    dto.naus_per_equip.azul,
                    dto.naus_per_equip.rojo,
                    dto.naus_per_equip.verde
                )
            )
        }

        val rvBoard = findViewById<RecyclerView>(R.id.rv_board)
        rvBoard.adapter = BoardAdapter(cells)
        updateLegend(cells)
    }

    private fun startCountdown(targetInstant: Instant) {
        countdownJob?.cancel()
        countdownJob = lifecycleScope.launch {
            while (isActive) {
                val now = Clock.System.now()
                val remaining = targetInstant.toEpochMilliseconds() - now.toEpochMilliseconds()

                if (remaining <= 0) {
                    findViewById<TextView>(R.id.tv_next_turn_time).text = getString(R.string.next_turn_format, "00:00:00")
                    break
                }

                val secondsTotal = remaining / 1000
                val hours = secondsTotal / 3600
                val minutes = (secondsTotal % 3600) / 60
                val seconds = secondsTotal % 60

                findViewById<TextView>(R.id.tv_next_turn_time).text =
                    getString(R.string.next_turn_format, String.format("%02d:%02d:%02d", hours, minutes, seconds))

                delay(1000)
            }
        }
    }

    private fun updateLegend(cells: List<BoardCell>) {
        val counts = IntArray(4)
        cells.forEach { counts[it.teamOwner]++ }

        findViewById<TextView>(R.id.tv_legend_team1).text = getString(R.string.team_squares_format, 1, counts[1])
        findViewById<TextView>(R.id.tv_legend_team2).text = getString(R.string.team_squares_format, 2, counts[2])
        findViewById<TextView>(R.id.tv_legend_team3).text = getString(R.string.team_squares_format, 3, counts[3])
    }
}