package com.example.cosmos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmos.R

class PlayerBoardAdapter(private var playerPos: Pair<Int, Int>, private var planets: List<Pair<Int, Int>>) : RecyclerView.Adapter<PlayerBoardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMarker: TextView = view.findViewById(R.id.tv_marker)
        val ivPlanet: ImageView = view.findViewById(R.id.iv_planet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player_board_square, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val x = position % 10
        val y = position / 10
        
        if (x == playerPos.first && y == playerPos.second) {
            holder.tvMarker.text = holder.itemView.context.getString(R.string.player_marker)
            holder.tvMarker.visibility = View.VISIBLE
        } else {
            holder.tvMarker.text = ""
            holder.tvMarker.visibility = View.GONE
        }

        val hasPlanet = planets.any { it.first == x && it.second == y }
        if (hasPlanet) {
            holder.ivPlanet.visibility = View.VISIBLE
            holder.ivPlanet.setImageResource(R.drawable.planeta)
        } else {
            holder.ivPlanet.visibility = View.GONE
        }
    }

    override fun getItemCount() = 100

    fun updatePlayerPos(newPos: Pair<Int, Int>) {
        val oldPos = playerPos
        playerPos = newPos
        notifyItemChanged(oldPos.first + oldPos.second * 10)
        notifyItemChanged(playerPos.first + playerPos.second * 10)
    }

    fun updateData(newPos: Pair<Int, Int>, newPlanets: List<Pair<Int, Int>>) {
        playerPos = newPos
        planets = newPlanets
        notifyDataSetChanged()
    }
}
