package com.example.cosmos.interfaces

import com.example.cosmos.model.CrewRenameRequestDto
import com.example.cosmos.model.CrewRenameResponseDto
import com.example.cosmos.model.GameBoardResponseDto
import com.example.cosmos.model.JoinGameRequestDto
import com.example.cosmos.model.JoinGameResponseDto
import com.example.cosmos.model.MoveRequestDto
import com.example.cosmos.model.MoveResponseDto
import com.example.cosmos.model.MyStatusRequestDto
import com.example.cosmos.model.MyStatusResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT

interface CosmosApiService {

    @GET("game/all")
    suspend fun getGameBoard(): Response<GameBoardResponseDto>

    @HTTP(method = "GET", path = "game/mystatus", hasBody = true)
    suspend fun getMyStatus(@Body body: MyStatusRequestDto): Response<MyStatusResponseDto>

    @POST("game/join")
    suspend fun joinGame(@Body body: JoinGameRequestDto): Response<JoinGameResponseDto>

    @POST("ship/move")
    suspend fun sendMove(@Body body: MoveRequestDto): Response<MoveResponseDto>

    @PUT("crew/rename")
    suspend fun renameCrewmate(@Body body: CrewRenameRequestDto): Response<CrewRenameResponseDto>
}