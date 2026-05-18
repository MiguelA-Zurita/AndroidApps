package com.example.cosmos.model

data class JoinGameRequestDto(
    val nom_jugador: String,
)

data class JoinGameResponseDto(
    val id_nau: Int,
    val equip: String,
    val posicio_inicial: InitialPositionDto,
    val recursos: ResourcesDto,
    val timestamp_proxim_torn: String
)

data class InitialPositionDto(
    val x: Int,
    val y: Int
)

data class ResourcesDto(
    val quantitat_aliments: Int,
    val quantitat_armes: Int,
    val tripulacio: List<CrewMemberDto>
)

data class CrewMemberDto(
    val id_tripulant: Int,
    var nom: String,
    val estat_vital: Boolean
)

data class CrewRenameRequestDto(
    val id_nau: Int,
    val id_tripulant: Int,
    val nou_nom: String
)

data class CrewRenameResponseDto(
    val status: String,
    val missatge: String,
    val id_tripulant: Int,
    val nou_nom: String
)

data class MoveRequestDto(
    val id_nau: Int,
    val direccio: String
)

data class MoveResponseDto(
    val status: String,
    val message: String
)

data class GameBoardResponseDto(
    val torn_actual: Int,
    val estat_partida: String,
    val timestamp_proxim_torn: String,
    val taulell: List<BoardCellDto>
)

data class BoardCellDto(
    val casella_id: Int,
    val color_dominant: String,
    val naus_per_equip: ShipsPerTeamDto,
    val coordenades: CoordinatesDto,
    val planeta: Boolean
)

data class ShipsPerTeamDto(
    val rojo: Int,
    val azul: Int,
    val verde: Int
)

data class CoordinatesDto(
    val x: Int,
    val y: Int
)

data class MyStatusRequestDto(
    val id_nau: Int
)


data class MyStatusResponseDto(
    val id_nau: Int,
    val equip: String,
    val posicio: PositionDto,
    val recursos: ResourcesDto,
    val planeta: Boolean,
    val planetes: List<PlanetDto>,
    val timestamp_proxim_torn: String
)

data class PositionDto(
    val x: Int,
    val y: Int
)

data class PlanetDto(
    val cellId: Int,
    val x: Int,
    val y: Int
)

