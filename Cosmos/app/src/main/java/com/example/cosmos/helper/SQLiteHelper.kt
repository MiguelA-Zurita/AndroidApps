package com.example.cosmos.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.cosmos.model.EstadoTablero
import com.example.cosmos.model.Nave
import com.example.cosmos.model.Tripulante

class SQLiteHelper(context: Context, dbName: String) :
    SQLiteOpenHelper(context, dbName, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2

        const val TABLE_NAVE = "nave"
        const val COL_NAVE_ID = "id_nau"
        const val COL_NAVE_ALIMENTOS = "quantitat_aliments"
        const val COL_NAVE_ARMAS = "quantitat_armes"
        const val COL_NAVE_POS_X = "pos_x"
        const val COL_NAVE_POS_Y = "pos_y"

        const val TABLE_TRIPULANTE = "tripulante"
        const val COL_TRIP_ID = "id_tripulant"
        const val COL_TRIP_NAVE_ID = "id_nau"
        const val COL_TRIP_NOMBRE = "nom"
        const val COL_TRIP_ESTADO = "estat_vital"

        const val TABLE_ESTADO_TABLERO = "estado_tablero"
        const val COL_CASELLA_ID = "casella_id"
        const val COL_PLANETA = "presencia_planeta"

        const val COL_PLANETA_CELL_ID = "cell_id"
        const val COL_PLANETA_X = "pos_x"
        const val COL_PLANETA_Y = "pos_y"

        private const val SQL_CREATE_NAVE = """
            CREATE TABLE $TABLE_NAVE (
                $COL_NAVE_ID INTEGER PRIMARY KEY,
                $COL_NAVE_ALIMENTOS INTEGER NOT NULL CHECK($COL_NAVE_ALIMENTOS >= 0),
                $COL_NAVE_ARMAS INTEGER NOT NULL CHECK($COL_NAVE_ARMAS >= 0),
                $COL_NAVE_POS_X INTEGER NOT NULL CHECK($COL_NAVE_POS_X BETWEEN 0 AND 9),
                $COL_NAVE_POS_Y INTEGER NOT NULL CHECK($COL_NAVE_POS_Y BETWEEN 0 AND 9)
            )
        """

        private const val SQL_CREATE_TRIPULANTE = """
            CREATE TABLE $TABLE_TRIPULANTE (
                $COL_TRIP_ID INTEGER PRIMARY KEY,
                $COL_TRIP_NAVE_ID INTEGER NOT NULL,
                $COL_TRIP_NOMBRE TEXT NOT NULL,
                $COL_TRIP_ESTADO INTEGER NOT NULL CHECK($COL_TRIP_ESTADO IN (0,1)),
                FOREIGN KEY($COL_TRIP_NAVE_ID) REFERENCES $TABLE_NAVE($COL_NAVE_ID)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE
            )
        """

        private const val SQL_CREATE_ESTADO_TABLERO = """
            CREATE TABLE $TABLE_ESTADO_TABLERO (
                $COL_PLANETA_CELL_ID INTEGER PRIMARY KEY,
                $COL_NAVE_ID INTEGER NOT NULL,
                $COL_PLANETA INTEGER NOT NULL CHECK($COL_PLANETA IN (0,1)),
                $COL_PLANETA_X INTEGER NOT NULL CHECK($COL_PLANETA_X BETWEEN 0 AND 9),
                $COL_PLANETA_Y INTEGER NOT NULL CHECK($COL_PLANETA_Y BETWEEN 0 AND 9),
                FOREIGN KEY($COL_NAVE_ID) REFERENCES $TABLE_NAVE($COL_NAVE_ID)
                    ON DELETE CASCADE
                    ON UPDATE CASCADE
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys=ON")
        db.execSQL(SQL_CREATE_NAVE)
        db.execSQL(SQL_CREATE_TRIPULANTE)
        db.execSQL(SQL_CREATE_ESTADO_TABLERO)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAVE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRIPULANTE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ESTADO_TABLERO")
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.execSQL("PRAGMA foreign_keys=ON")
    }

    fun clearAllData() {
        val db = writableDatabase
        try {
            db.delete(TABLE_TRIPULANTE, null, null)
            db.delete(TABLE_NAVE, null, null)
            db.delete(TABLE_ESTADO_TABLERO, null, null)
        } catch (e: Exception) {
            Log.d("SQLiteHelper", "Error clearing data: ${e.message}")
        }
    }

    fun getShipById(shipId: Int): Nave? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAVE,
            arrayOf(
                COL_NAVE_ID,
                COL_NAVE_ALIMENTOS,
                COL_NAVE_ARMAS,
                COL_NAVE_POS_X,
                COL_NAVE_POS_Y
            ),
            "$COL_NAVE_ID = ?",
            arrayOf(shipId.toString()),
            null,
            null,
            null
        )

        var ship: Nave? = null

        cursor.use { c ->
            if (c.moveToFirst()) {
                val idIndex = c.getColumnIndexOrThrow(COL_NAVE_ID)
                val foodIndex = c.getColumnIndexOrThrow(COL_NAVE_ALIMENTOS)
                val weaponsIndex = c.getColumnIndexOrThrow(COL_NAVE_ARMAS)
                val posXIndex = c.getColumnIndexOrThrow(COL_NAVE_POS_X)
                val posYIndex = c.getColumnIndexOrThrow(COL_NAVE_POS_Y)

                ship = Nave(
                    idNau = c.getInt(idIndex),
                    quantitatAliments = c.getInt(foodIndex),
                    quantitatArmes = c.getInt(weaponsIndex),
                    posX = c.getInt(posXIndex),
                    posY = c.getInt(posYIndex)
                )
            }
        }

        db.close()
        return ship
    }

    fun updateShip(ship: Nave): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NAVE_ID, ship.idNau)
            put(COL_NAVE_ALIMENTOS, ship.quantitatAliments)
            put(COL_NAVE_ARMAS, ship.quantitatArmes)
            put(COL_NAVE_POS_X, ship.posX)
            put(COL_NAVE_POS_Y, ship.posY)
        }

        val rows = db.replace(TABLE_NAVE, null, values)

        db.close()
        return rows.toInt()
    }

    fun updateCrewMember(crewMember: Tripulante?): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TRIP_ID, crewMember?.idTripulant)
            put(COL_TRIP_NAVE_ID, crewMember?.idNau)
            put(COL_TRIP_NOMBRE, crewMember?.nom)
            put(COL_TRIP_ESTADO, if (crewMember?.estatVital == true) 1 else 0)
        }

        val rows = db.replace(TABLE_TRIPULANTE, null, values)

        db.close()
        return rows.toInt()
    }

    fun getCrewMember(crewId: Int): Tripulante? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TRIPULANTE,
            arrayOf(COL_TRIP_ID, COL_TRIP_NAVE_ID, COL_TRIP_NOMBRE, COL_TRIP_ESTADO),
            "$COL_TRIP_ID = ?",
            arrayOf(crewId.toString()),
            null, null, null
        )

        var crewMember: Tripulante? = null

        cursor.use { c ->
            if (c.moveToFirst()) {
                crewMember = Tripulante(
                    idTripulant = c.getInt(c.getColumnIndexOrThrow(COL_TRIP_ID)),
                    idNau = c.getInt(c.getColumnIndexOrThrow(COL_TRIP_NAVE_ID)),
                    nom = c.getString(c.getColumnIndexOrThrow(COL_TRIP_NOMBRE)),
                    estatVital = c.getInt(c.getColumnIndexOrThrow(COL_TRIP_ESTADO)) == 1
                )
            }
        }

        db.close()
        return crewMember
    }

    fun getCrewMembersByShip(nauId: Int): List<Tripulante> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_TRIPULANTE,
            arrayOf(COL_TRIP_ID, COL_TRIP_NOMBRE, COL_TRIP_ESTADO),
            "$COL_TRIP_NAVE_ID = ?",
            arrayOf(nauId.toString()),
            null, null, "$COL_TRIP_ID ASC"
        )
        val crewMembers = mutableListOf<Tripulante>()
        cursor.use { c ->
            while (c.moveToNext()) {
                crewMembers.add(
                    Tripulante(
                        idTripulant = c.getInt(c.getColumnIndexOrThrow(COL_TRIP_ID)),
                        idNau = nauId,
                        nom = c.getString(c.getColumnIndexOrThrow(COL_TRIP_NOMBRE)),
                        estatVital = c.getInt(c.getColumnIndexOrThrow(COL_TRIP_ESTADO)) == 1
                    )
                )
            }
        }
        return crewMembers
    }

    fun getAllBoardStatus(): List<EstadoTablero> {
        val db = readableDatabase
        val list = mutableListOf<EstadoTablero>()

        val cursor = db.query(
            TABLE_ESTADO_TABLERO,
            arrayOf(COL_CASELLA_ID, COL_PLANETA),
            null, null, null, null,
            "$COL_CASELLA_ID ASC"
        )

        cursor.use { c ->
            val planetIndex = c.getColumnIndexOrThrow(COL_PLANETA)

            while (c.moveToNext()) {
                val cellId = c.getColumnIndexOrThrow(COL_CASELLA_ID)
                list.add(
                    EstadoTablero(
                        casellaId = c.getInt(cellId),
                        nauID = c.getColumnIndexOrThrow(COL_NAVE_ID),
                        planeta = c.getInt(planetIndex) == 1,
                        x = c.getInt(c.getColumnIndexOrThrow(COL_PLANETA_X)),
                        y = c.getInt(c.getColumnIndexOrThrow(COL_PLANETA_Y))
                    )
                )
            }
        }

        db.close()
        return list
    }

    fun updateOrInsertBoardStatus(boardStatus: EstadoTablero) {
        val db = writableDatabase
        try {
            db.beginTransaction()
            val values = ContentValues().apply {
                put(COL_CASELLA_ID, boardStatus.casellaId)
                put(COL_NAVE_ID, boardStatus.nauID)
                put(COL_PLANETA, if (boardStatus.planeta) 1 else 0)
                put(COL_PLANETA_X, boardStatus.x)
                put(COL_PLANETA_Y, boardStatus.y)
            }
            db.replace(TABLE_ESTADO_TABLERO, null, values)
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.d("SQLiteHelper", "Error upserting board status: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }

    fun updateOrInsertAllBoardStatus(boardStatusList: List<EstadoTablero>) {
        val db = writableDatabase
        try {
            db.beginTransaction()
            boardStatusList.forEach { status ->
                val values = ContentValues().apply {
                    put(COL_CASELLA_ID, status.casellaId)
                    put(COL_NAVE_ID, status.nauID)
                    put(COL_PLANETA, if (status.planeta) 1 else 0)
                    put(COL_PLANETA_X, status.x)
                    put(COL_PLANETA_Y, status.y)
                }
                db.replace(TABLE_ESTADO_TABLERO, null, values)
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.d("SQLiteHelper", "Error upserting board status: ${e.message}")
        } finally {
            db.endTransaction()
        }
    }

}