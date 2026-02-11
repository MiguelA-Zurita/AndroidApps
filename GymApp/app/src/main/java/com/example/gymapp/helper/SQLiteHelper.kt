package com.example.gymapp.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context) : SQLiteOpenHelper
    (context, DATABASE_NAME, null, DATABASE_VERSION){
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_INIT)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        db?.execSQL(SQL_DROP)
    }

    companion object{
        private const val DATABASE_NAME = "sqlito.db"
        private const val DATABASE_VERSION = 1
        private const val EXERCISE_NAME = "exercises"
        private const val PLAN_NAME = "plans"
        private const val EXERCISEPLAN_NAME = "exercise_plan"
        private const val SQL_INIT = """
            
            CREATE TABLE $EXERCISE_NAME(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL
            );
            
            CREATE TABLE $PLAN_NAME(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                weeks INTEGER NOT NULL
            );
            
            CREATE TABLE $EXERCISEPLAN_NAME(
                idExercise INTEGER PRIMARY KEY,
                idPlan INTEGER PRIMARY KEY,
                repetitions INTEGER NOT NULL,
                weight REAL NOT NULL,
                seriesNo INTEGER NOT NULL,
                exerciseDate NUMERIC NOT NULL,
                FOREIGN KEY (idExercise) $EXERCISE_NAME(id),
                FOREIGN KEY (idPlan) $PLAN_NAME(id)
            );
        """
        private const val SQL_DROP = """
            DROP TABLE IF EXISTS $EXERCISEPLAN_NAME;
            DROP TABLE IF EXISTS $EXERCISE_NAME;
            DROP TABLE IF EXISTS $PLAN_NAME;
        """
    }
}