package com.example.cosmos.repository

import android.app.GameState
import android.util.Log
import com.example.cosmos.interfaces.CosmosApiService
import com.example.cosmos.model.CrewRenameRequestDto
import com.example.cosmos.model.CrewRenameResponseDto
import com.example.cosmos.model.GameBoardResponseDto
import com.example.cosmos.model.JoinGameRequestDto
import com.example.cosmos.model.JoinGameResponseDto
import com.example.cosmos.model.MoveRequestDto
import com.example.cosmos.model.MoveResponseDto
import com.example.cosmos.model.MyStatusRequestDto
import com.example.cosmos.model.MyStatusResponseDto
import retrofit2.HttpException
import retrofit2.Response

class CosmosRepository(
    private val apiService: CosmosApiService
) {

    suspend fun joinGame(playerName: String): JoinGameResponseDto {
        val response = apiService.joinGame(
            JoinGameRequestDto(nom_jugador = playerName)
        )
        return response.requireBody()
    }

    suspend fun renameCrewMember(
        shipId: Int,
        crewMemberId: Int,
        newName: String
    ): CrewRenameResponseDto {
        val response = apiService.renameCrewmate(
            CrewRenameRequestDto(
                id_nau = shipId,
                id_tripulant = crewMemberId,
                nou_nom = newName
            )
        )
        return response.requireBody()
    }

    suspend fun move(
        shipId: Int,
        direction: String
    ): MoveResponseDto {
        val response = apiService.sendMove(
            MoveRequestDto(
                id_nau = shipId,
                direccio = direction
            )
        )
        return response.requireBody()
    }

    suspend fun getGameBoard(): GameBoardResponseDto {
        val response = apiService.getGameBoard()
        return response.requireBody()
    }

    suspend fun getMyStatus(shipId: Int): MyStatusResponseDto {
        val response = apiService.getMyStatus(MyStatusRequestDto(id_nau = shipId))
        return response.requireBody()
    }

    private fun <T> Response<T>.requireBody(): T {
        if (isSuccessful) {
            return body() ?: throw IllegalStateException(
                "The response body is null"
            )
        } else {
            Log.d("HTTP ERROR", "HTTP ERROR: ${code()} - ${message()}")
            throw HttpException(this)
        }
    }
}