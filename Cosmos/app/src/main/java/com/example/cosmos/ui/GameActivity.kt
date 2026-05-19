package com.example.cosmos.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmos.adapter.PlayerBoardAdapter
import com.example.cosmos.R
import com.example.cosmos.helper.ConfigManager
import com.example.cosmos.helper.DataStoreHelper
import com.example.cosmos.helper.DatabaseProvider
import com.example.cosmos.helper.MyStatusRequester
import com.example.cosmos.helper.RetrofitHelper
import com.example.cosmos.model.EstadoTablero
import com.example.cosmos.model.MyStatusResponseDto
import com.example.cosmos.model.Nave
import com.example.cosmos.model.Tripulante
import com.example.cosmos.repository.CosmosRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class GameActivity : AppCompatActivity() {
    private var playerX = 5
    private var playerY = 5
    private var canMove = true
    private lateinit var adapter: PlayerBoardAdapter
    private lateinit var repository: CosmosRepository
    private var countdownJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val api = RetrofitHelper().getApi(this)

        repository = CosmosRepository(api)

        val rvBoard = findViewById<RecyclerView>(R.id.rv_game_board)
        rvBoard.layoutManager = GridLayoutManager(this, 10)
        adapter = PlayerBoardAdapter(playerX to playerY, emptyList())
        rvBoard.adapter = adapter

        addMovementListeners()

        startPeriodicUpdate()

        findViewById<Button>(R.id.btn_view_resources).setOnClickListener {
            val intent = Intent(this, CurrentResourcesActivity::class.java)
            startActivity(intent)
        }

        val chatContainer = findViewById<LinearLayout>(R.id.chat_container)
        val etMessage = findViewById<EditText>(R.id.et_chat_message)

        findViewById<ImageButton>(R.id.btn_send_chat).setOnClickListener {
            sendMessage(etMessage, chatContainer)
        }
    }

    private fun startPeriodicUpdate() {
        lifecycleScope.launch {
            while (isActive) {
                try {
                    val myStatusData = repository.getMyStatus(DataStoreHelper.getShipID(this@GameActivity).toInt())

                    val gameData = repository.getGameBoard()

                    withContext(Dispatchers.IO) {
                        syncDatabase(myStatusData)
                    }

                    updateBoard()

                    val nextTurnInstant = Instant.parse(gameData.timestamp_proxim_torn)

                    findViewById<TextView>(R.id.tv_turn_info).text =
                        getString(R.string.turn_format, gameData.torn_actual)

                    startCountdown(nextTurnInstant)

                    delay(10000)
                } catch (e: Exception) {
                    delay(5000)
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun startCountdown(targetInstant: Instant) {
        countdownJob?.cancel()
        countdownJob = lifecycleScope.launch {
            while (isActive) {
                val now = Clock.System.now()
                val remaining = targetInstant.toEpochMilliseconds() - now.toEpochMilliseconds()

                if (remaining <= 0) {
                    findViewById<TextView>(R.id.tv_next_turn_time).text =
                        getString(R.string.next_turn_format, "00:00:00")
                    break
                }

                val secondsTotal = remaining / 1000
                val hours = secondsTotal / 3600
                val minutes = (secondsTotal % 3600) / 60
                val seconds = secondsTotal % 60

                findViewById<TextView>(R.id.tv_next_turn_time).text =
                    getString(
                        R.string.next_turn_format,
                        String.format("%02d:%02d:%02d", hours, minutes, seconds)
                    )

                canMove = true
                delay(1000)
            }
        }
    }

    private fun sendMessage(etMessage: EditText, chatContainer: LinearLayout) {
        val msg = etMessage.text.toString()
        if (msg.isNotEmpty()) {
            val tv = TextView(this)
            tv.text = getString(R.string.chat_message_format, msg)

            lifecycleScope.launch {
                val textColor = DataStoreHelper.getTeamColor(this@GameActivity).let { team ->
                    when (team) {
                        "azul" -> Color.BLUE
                        "rojo" -> Color.RED
                        "verde" -> Color.GREEN
                        else -> Color.BLACK
                    }
                }
                tv.setTextColor(textColor)
            }
            tv.textSize = 13f
            chatContainer.addView(tv)
            etMessage.text.clear()
        }
    }

    private suspend fun updateBoard() = withContext(Dispatchers.IO) {

        val db = DatabaseProvider.create(this@GameActivity)
        try {

            db.getShipById(DataStoreHelper.getShipID(this@GameActivity).toInt())?.let { ship ->
                playerX = ship.posX
                playerY = ship.posY
            }

            var isShipAlive = false
            db.getCrewMembersByShip(DataStoreHelper.getShipID(this@GameActivity).toInt()).forEach { crewMember ->
                if (crewMember.estatVital) {
                    isShipAlive = true
                }
            }

            val planetsPos = mutableListOf<Pair<Int, Int>>()
            db.getAllBoardStatus().forEach { status ->
                status.takeIf { it.planeta }?.let {
                    planetsPos.add(it.x to it.y)
                }
            }

            withContext(Dispatchers.Main) {
                if (!isShipAlive) {
                    gameOver()
                }

                adapter.updateData(playerX to playerY, planetsPos)
            }
        } catch (e: Exception) {
            Log.d("GameActivity", "Error updating board: ${e.message} ")
        }
    }

    private fun addMovementListeners() {
        findViewById<ImageButton>(R.id.btn_move_up).setOnClickListener {
            if (!canMove) return@setOnClickListener
            movePlayer("UP")
            canMove = false
        }
        findViewById<ImageButton>(R.id.btn_move_down).setOnClickListener {
            if (!canMove) return@setOnClickListener
            movePlayer("DOWN")
            canMove = false
        }
        findViewById<ImageButton>(R.id.btn_move_left).setOnClickListener {
            if (!canMove) return@setOnClickListener
            movePlayer("LEFT")
            canMove = false
        }
        findViewById<ImageButton>(R.id.btn_move_right).setOnClickListener {
            if (!canMove) return@setOnClickListener
            movePlayer("RIGHT")
            canMove = false
        }
    }

    private fun syncDatabase(data: MyStatusResponseDto) {
        val db = DatabaseProvider.create(this)

        val shipEntity = Nave(
            data.id_nau,
            data.recursos.quantitat_aliments,
            data.recursos.quantitat_armes,
            data.posicio.x,
            data.posicio.y
        )

        db.updateShip(shipEntity)

        data.recursos.tripulacio.forEach { crewMember ->
            val crewMemberDB = Tripulante(
                idTripulant = crewMember.id_tripulant,
                nom = crewMember.nom,
                estatVital = crewMember.estat_vital,
                idNau = ConfigManager.loadConfig(this).playerSettings.nauID
            )
            db.updateCrewMember(crewMemberDB)
        }

        data.planetes.forEach { planet ->
            val boardStatusEntity = EstadoTablero(
                nauID = data.id_nau,
                casellaId = planet.cellId,
                planeta = data.planeta,
                x = planet.x,
                y = planet.y
            )
            db.updateOrInsertBoardStatus(boardStatusEntity)
        }
    }

    fun movePlayer(direction: String) = lifecycleScope.launch {
        try {
            val shipId = DataStoreHelper.getShipID(this@GameActivity).toInt()
            repository.move(shipId, direction)
        } catch (e: Exception) {
            Log.d("GameActivity", "Error moving player: ${e.message} ")
        }
    }

    fun gameOver(){
        Dialog(this).apply {
            setContentView(R.layout.dialog_game_over)
            setCancelable(false)
            findViewById<Button>(R.id.btn_play_again).setOnClickListener {
                dismiss()
                startActivity(Intent(context, ChooseNickActivity::class.java))
                finish()
            }
            findViewById<Button>(R.id.btn_back_to_main).setOnClickListener {
                dismiss()
                startActivity(Intent(context, MainActivity::class.java))
                finish()
            }
            show()
        }
    }
}
