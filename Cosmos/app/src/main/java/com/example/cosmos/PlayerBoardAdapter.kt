package com.example.cosmos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlayerBoardAdapter(private val playerPos: Pair<Int, Int>) : RecyclerView.Adapter<PlayerBoardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMarker: TextView = view.findViewById(R.id.tv_marker)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player_board_square, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val x = position % 10
        val y = position / 10
        
        if (x == playerPos.first && y == playerPos.second) {
            holder.tvMarker.text = "T"
            holder.tvMarker.visibility = View.VISIBLE
        } else {
            holder.tvMarker.text = ""
            holder.tvMarker.visibility = View.GONE
        }
    }

    override fun getItemCount() = 100
}
