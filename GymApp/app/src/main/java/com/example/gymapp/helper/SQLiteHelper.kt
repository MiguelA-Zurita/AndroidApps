package com.example.gymapp.helper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.gymapp.model.Exercise
import com.example.gymapp.model.ExercisePlan
import com.example.gymapp.model.Plan
import java.sql.Date
import java.text.DateFormat
import java.time.LocalDate

class SQLiteHelper(context: Context) : SQLiteOpenHelper
    (context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        initTables(db)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        dropTables(db)
        initTables(db)
    }

    fun initTables(db: SQLiteDatabase?) {
        db?.execSQL(SQL_INIT_EXERCISE)
        db?.execSQL(SQL_INIT_PLAN)
        db?.execSQL(SQL_INIT_EXERCISEPLAN)
        
        val commonExercises = listOf("Press de banca", "Sentadillas", "Peso muerto", "Press militar", "Dominadas") //Ejercicios que por ahora no se utilizan, pendiente de implementacion
        commonExercises.forEach { name ->
            val values = ContentValues().apply {
                put(Exercise.NAME_COLUMN, name)
            }
            db?.insert(Exercise.TABLE_NAME, null, values)
        }
    }

    fun dropTables(db: SQLiteDatabase?) {
        db?.execSQL(SQL_DROP_EXERCISE)
        db?.execSQL(SQL_DROP_PLAN)
        db?.execSQL(SQL_DROP_EXERCISEPLAN)
    }

    fun insert(o: Any): Long {
        val db = writableDatabase
        val id: Long
        ContentValues().apply {
            when (true) {
                (o is Exercise) -> {
                    put(Exercise.NAME_COLUMN, o.name)
                    id = db.insert(Exercise.TABLE_NAME, null, this)
                }

                (o is Plan) -> {
                    put(Plan.NAME_COLUMN, o.name)
                    put(Plan.WEEKS_COLUMN, o.weeks)
                    put(Plan.DAYS_PER_WEEK_COLUMN, o.daysPerWeek)
                    put(Plan.ENABLED_COLUMN, if (o.enabled) 1 else 0)
                    id = db.insert(Plan.TABLE_NAME, null, this)
                }

                (o is ExercisePlan) -> {
                    put(ExercisePlan.EXERCISE_ID_COLUMN, o.exerciseId)
                    put(ExercisePlan.PLAN_ID_COLUMN, o.planId)
                    put(ExercisePlan.REPETITIONS_COLUMN, o.repetitions)
                    put(ExercisePlan.WEIGHT_COLUMN, o.weight)
                    put(ExercisePlan.SERIES_COLUMN, o.seriesNo)
                    put(ExercisePlan.DATE_COLUMN, o.date.toString())
                    id = db.insert(ExercisePlan.TABLE_NAME, null, this)
                }

                else -> {
                    id = 0L
                }
            }
        }
        return id
    }

    fun getAll(clazz: Class<*>): List<Any> {
        val db = readableDatabase
        val list: MutableList<Any> = mutableListOf()
        val cursor = when (clazz) {
            Exercise::class.java -> db.rawQuery("SELECT * FROM ${Exercise.TABLE_NAME}", null)
            Plan::class.java -> db.rawQuery("SELECT * FROM ${Plan.TABLE_NAME}", null)
            ExercisePlan::class.java -> db.rawQuery(
                "SELECT * FROM ${ExercisePlan.TABLE_NAME}",
                null
            )

            else -> null
        }
        cursor.use { cursor ->
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    when (clazz) {
                        Exercise::class.java -> list.add(
                            Exercise(
                                cursor.getLong(cursor.getColumnIndexOrThrow(Exercise.ID_COLUMN)),
                                cursor.getString(cursor.getColumnIndexOrThrow(Exercise.NAME_COLUMN))
                            )
                        )

                        Plan::class.java -> list.add(
                            Plan(
                                cursor.getLong(cursor.getColumnIndexOrThrow(Plan.ID_COLUMN)),
                                cursor.getString(cursor.getColumnIndexOrThrow(Plan.NAME_COLUMN)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(Plan.WEEKS_COLUMN)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(Plan.DAYS_PER_WEEK_COLUMN)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(Plan.ENABLED_COLUMN)) == 1
                            )
                        )

                        ExercisePlan::class.java -> list.add(
                            ExercisePlan(
                                cursor.getLong(cursor.getColumnIndexOrThrow(ExercisePlan.EXERCISE_ID_COLUMN)),
                                cursor.getLong(cursor.getColumnIndexOrThrow(ExercisePlan.PLAN_ID_COLUMN)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(ExercisePlan.REPETITIONS_COLUMN)),
                                cursor.getFloat(cursor.getColumnIndexOrThrow(ExercisePlan.WEIGHT_COLUMN)),
                                cursor.getInt(cursor.getColumnIndexOrThrow(ExercisePlan.SERIES_COLUMN)),
                                Date.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ExercisePlan.DATE_COLUMN)))
                            )
                        )
                    }
                }
            }
        }
        return list
    }

    fun getById(clazz: Class<*>, id: Long): Any? {
        val db = readableDatabase
        val cursor = when (clazz) {
            Exercise::class.java -> db.query(
                Exercise.TABLE_NAME,
                arrayOf(Exercise.ID_COLUMN, Exercise.NAME_COLUMN),
                "${Exercise.ID_COLUMN}=?",
                arrayOf(id.toString()),
                null,
                null,
                "${Exercise.ID_COLUMN} DESC"
            )

            Plan::class.java -> db.query(
                Plan.TABLE_NAME,
                arrayOf(
                    Plan.ID_COLUMN,
                    Plan.NAME_COLUMN,
                    Plan.WEEKS_COLUMN,
                    Plan.DAYS_PER_WEEK_COLUMN,
                    Plan.ENABLED_COLUMN
                ),
                "${Plan.ID_COLUMN}=?",
                arrayOf(id.toString()),
                null,
                null,
                "${Plan.ID_COLUMN} DESC"
            )

            ExercisePlan::class.java -> db.query(
                ExercisePlan.TABLE_NAME,
                arrayOf(
                    ExercisePlan.EXERCISE_ID_COLUMN,
                    ExercisePlan.PLAN_ID_COLUMN,
                    ExercisePlan.REPETITIONS_COLUMN,
                    ExercisePlan.WEIGHT_COLUMN,
                    ExercisePlan.SERIES_COLUMN,
                    ExercisePlan.DATE_COLUMN
                ),
                "${ExercisePlan.EXERCISE_ID_COLUMN}=?",
                arrayOf(id.toString()),
                null,
                null,
                "${ExercisePlan.EXERCISE_ID_COLUMN} DESC"
            )
            else -> null
        }
        if (cursor != null && cursor.moveToFirst()) {
            return when (clazz) {
                Exercise::class.java -> Exercise(
                    cursor.getLong(
                        cursor.getColumnIndexOrThrow(
                            Exercise.ID_COLUMN
                        )
                    ), cursor.getString(cursor.getColumnIndexOrThrow(Exercise.NAME_COLUMN))
                )

                Plan::class.java -> Plan(
                    cursor.getLong(cursor.getColumnIndexOrThrow(Plan.ID_COLUMN)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Plan.NAME_COLUMN)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Plan.WEEKS_COLUMN)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Plan.DAYS_PER_WEEK_COLUMN)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(Plan.ENABLED_COLUMN)) == 1
                )

                ExercisePlan::class.java -> ExercisePlan(
                    cursor.getLong(cursor.getColumnIndexOrThrow(ExercisePlan.EXERCISE_ID_COLUMN)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(ExercisePlan.PLAN_ID_COLUMN)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(ExercisePlan.REPETITIONS_COLUMN)),
                    cursor.getFloat(cursor.getColumnIndexOrThrow(ExercisePlan.WEIGHT_COLUMN)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(ExercisePlan.SERIES_COLUMN)),
                    Date.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ExercisePlan.DATE_COLUMN)))
                )
                else -> null
            }
        }
        else return null
    }

    fun update(o: Any): Int {
        val db = writableDatabase
        val rows = when (o) {
            is Exercise -> {
                val values = ContentValues().apply {
                    put(Exercise.NAME_COLUMN, o.name)
                }
                db.update(
                    Exercise.TABLE_NAME,
                    values,
                    "${Exercise.ID_COLUMN}=?",
                    arrayOf(o.id.toString())
                )
            }

            is Plan -> {
                val values = ContentValues().apply {
                    put(Plan.NAME_COLUMN, o.name)
                    put(Plan.WEEKS_COLUMN, o.weeks)
                    put(Plan.DAYS_PER_WEEK_COLUMN, o.daysPerWeek)
                    put(Plan.ENABLED_COLUMN, if (o.enabled) 1 else 0)
                }
                db.update(
                    Plan.TABLE_NAME,
                    values,
                    "${Plan.ID_COLUMN}=?",
                    arrayOf(o.id.toString())
                )
            }

            is ExercisePlan -> {
                val values = ContentValues().apply {
                    put(ExercisePlan.REPETITIONS_COLUMN, o.repetitions)
                    put(ExercisePlan.WEIGHT_COLUMN, o.weight)
                }
                db.update(
                    ExercisePlan.TABLE_NAME,
                    values,
                    "${ExercisePlan.EXERCISE_ID_COLUMN}=? AND ${ExercisePlan.PLAN_ID_COLUMN}=? AND ${ExercisePlan.SERIES_COLUMN}=? AND ${ExercisePlan.DATE_COLUMN}=?",
                    arrayOf(
                        o.exerciseId.toString(),
                        o.planId.toString(),
                        o.seriesNo.toString(),
                        o.date.toString()
                    )
                )
            }
            else -> 0
        }
        return rows
    }

    fun delete(clazz: Class<*>, id: Long): Int {
        val db = writableDatabase
        return when (clazz) {
            Exercise::class.java -> db.delete(Exercise.TABLE_NAME, "${Exercise.ID_COLUMN}=?", arrayOf(id.toString()))
            Plan::class.java -> {
                // Also delete related exercise plans
                db.delete(ExercisePlan.TABLE_NAME, "${ExercisePlan.PLAN_ID_COLUMN}=?", arrayOf(id.toString()))
                db.delete(Plan.TABLE_NAME, "${Plan.ID_COLUMN}=?", arrayOf(id.toString()))
            }
            ExercisePlan::class.java -> db.delete(ExercisePlan.TABLE_NAME, "${ExercisePlan.EXERCISE_ID_COLUMN}=?", arrayOf(id.toString()))
            else -> 0
        }
    }

    fun getActivePlan(): Plan? {
        val db = readableDatabase
        val cursor = db.query(
            Plan.TABLE_NAME,
            null,
            "${Plan.ENABLED_COLUMN}=?",
            arrayOf("1"),
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return Plan(
                    it.getLong(it.getColumnIndexOrThrow(Plan.ID_COLUMN)),
                    it.getString(it.getColumnIndexOrThrow(Plan.NAME_COLUMN)),
                    it.getInt(it.getColumnIndexOrThrow(Plan.WEEKS_COLUMN)),
                    it.getInt(it.getColumnIndexOrThrow(Plan.DAYS_PER_WEEK_COLUMN)),
                    it.getInt(it.getColumnIndexOrThrow(Plan.ENABLED_COLUMN)) == 1
                )
            }
        }
        return null
    }

    fun getExercisesForPlan(planId: Long): List<ExercisePlan> {
        val db = readableDatabase
        val list = mutableListOf<ExercisePlan>()
        val cursor = db.query(
            ExercisePlan.TABLE_NAME,
            null,
            "${ExercisePlan.PLAN_ID_COLUMN}=?",
            arrayOf(planId.toString()),
            null,
            null,
            "${ExercisePlan.DATE_COLUMN} ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    ExercisePlan(
                        it.getLong(it.getColumnIndexOrThrow(ExercisePlan.EXERCISE_ID_COLUMN)),
                        it.getLong(it.getColumnIndexOrThrow(ExercisePlan.PLAN_ID_COLUMN)),
                        it.getInt(it.getColumnIndexOrThrow(ExercisePlan.REPETITIONS_COLUMN)),
                        it.getFloat(it.getColumnIndexOrThrow(ExercisePlan.WEIGHT_COLUMN)),
                        it.getInt(it.getColumnIndexOrThrow(ExercisePlan.SERIES_COLUMN)),
                        Date.valueOf(it.getString(it.getColumnIndexOrThrow(ExercisePlan.DATE_COLUMN)))
                    )
                )
            }
        }
        return list
    }

    fun setActivePlan(planId: Long) {
        val db = writableDatabase
        try {
            val valuesAll = ContentValues().apply {
                put(Plan.ENABLED_COLUMN, 0)
            }
            db.update(Plan.TABLE_NAME, valuesAll, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val plan = getById(Plan::class.java, planId) as? Plan
        if (plan != null) {
            update(plan.copy(enabled = true))
        }
    }

    companion object {
        private const val DATABASE_NAME = "sqlito.db"
        private const val DATABASE_VERSION = 1
        private const val EXERCISE_NAME = "exercises"
        private const val PLAN_NAME = "plans"
        private const val EXERCISEPLAN_NAME = "exercise_plan"
        private const val SQL_INIT_EXERCISE = """
            CREATE TABLE IF NOT EXISTS ${Exercise.TABLE_NAME}(
                ${Exercise.ID_COLUMN} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Exercise.NAME_COLUMN} TEXT NOT NULL
            );
        """
        private const val SQL_INIT_PLAN = """
            CREATE TABLE IF NOT EXISTS ${Plan.TABLE_NAME}(
                ${Plan.ID_COLUMN} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${Plan.NAME_COLUMN} TEXT NOT NULL,
                ${Plan.WEEKS_COLUMN} INTEGER NOT NULL,
                ${Plan.DAYS_PER_WEEK_COLUMN} INTEGER NOT NULL,
                ${Plan.ENABLED_COLUMN} INTEGER NOT NULL
            );            
        """
        private const val SQL_INIT_EXERCISEPLAN = """
            CREATE TABLE  IF NOT EXISTS ${ExercisePlan.TABLE_NAME}(
                ${ExercisePlan.EXERCISE_ID_COLUMN} INTEGER NOT NULL,
                ${ExercisePlan.PLAN_ID_COLUMN} INTEGER NOT NULL,
                ${ExercisePlan.REPETITIONS_COLUMN} INTEGER NOT NULL,
                ${ExercisePlan.WEIGHT_COLUMN} REAL NOT NULL,
                ${ExercisePlan.SERIES_COLUMN} INTEGER NOT NULL,
                ${ExercisePlan.DATE_COLUMN} NUMERIC  NOT NULL,
                PRIMARY KEY(${ExercisePlan.EXERCISE_ID_COLUMN}, ${ExercisePlan.PLAN_ID_COLUMN}, ${ExercisePlan.SERIES_COLUMN}, ${ExercisePlan.DATE_COLUMN}),
                FOREIGN KEY (${ExercisePlan.EXERCISE_ID_COLUMN}) REFERENCES $EXERCISE_NAME(id),
                FOREIGN KEY (${ExercisePlan.PLAN_ID_COLUMN}) REFERENCES $PLAN_NAME(${Plan.ID_COLUMN})
            );            
        """
        private const val SQL_DROP_EXERCISE = """
            DROP TABLE IF EXISTS $EXERCISE_NAME;
        """
        private const val SQL_DROP_PLAN = """
            DROP TABLE IF EXISTS $PLAN_NAME;
        """
        private const val SQL_DROP_EXERCISEPLAN = """
            DROP TABLE IF EXISTS $EXERCISEPLAN_NAME;
        """
    }
}