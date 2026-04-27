package com.example.tictactoe.ui

import android.content.Context
import android.graphics.*
import android.view.View

class TicTacToeBoard @JvmOverloads constructor(
    context: Context,
    attrs: android.util.AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 12f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val board = Array(3) { IntArray(3) { 0 } } // 0: empty, 1: X, 2: O
    private var isXTurn = true

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        val cellW = w / 3
        val cellH = h / 3

        // Draw grid lines
        canvas.drawLine(cellW, 0f, cellW, h, paint)
        canvas.drawLine(cellW * 2, 0f, cellW * 2, h, paint)
        canvas.drawLine(0f, cellH, w, cellH, paint)
        canvas.drawLine(0f, cellH * 2, w, cellH * 2, paint)

        // Draw X and O
        for (row in 0..2) {
            for (col in 0..2) {
                val left = col * cellW
                val top = row * cellH
                val right = (col + 1) * cellW
                val bottom = (row + 1) * cellH
                val padding = 40f

                if (board[row][col] == 1) {
                    // Draw X
                    canvas.drawLine(left + padding, top + padding, right - padding, bottom - padding, paint)
                    canvas.drawLine(right - padding, top + padding, left + padding, bottom - padding, paint)
                } else if (board[row][col] == 2) {
                    // Draw O
                    val centerX = left + cellW / 2
                    val centerY = top + cellH / 2
                    val radius = Math.min(cellW, cellH) / 2 - padding
                    canvas.drawCircle(centerX, centerY, radius, paint)
                }
            }
        }
    }

    override fun onTouchEvent(event: android.view.MotionEvent): Boolean {
        if (event.action == android.view.MotionEvent.ACTION_DOWN) {
            val cellW = width.toFloat() / 3
            val cellH = height.toFloat() / 3

            val col = (event.x / cellW).toInt()
            val row = (event.y / cellH).toInt()

            if (row in 0..2 && col in 0..2 && board[row][col] == 0) {
                board[row][col] = if (isXTurn) 1 else 2
                isXTurn = !isXTurn
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun reset() {
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j] = 0
            }
        }
        isXTurn = true
        invalidate()
    }
}