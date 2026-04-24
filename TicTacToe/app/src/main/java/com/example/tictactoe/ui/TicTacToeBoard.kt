package com.example.tictactoe.ui

import android.content.Context
import android.graphics.*
import android.view.View

class TicTacToeBoard(context: Context) : View(context) {
    fun OnDraw(canvas: Canvas){

        val paint = Paint()
        paint.color = Color.BLACK
        paint.strokeWidth = 12f
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
    }

}