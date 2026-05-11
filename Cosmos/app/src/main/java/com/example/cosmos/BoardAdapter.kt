package com.example.cosmos

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class BoardAdapter(private val cells: List<BoardCell>) : RecyclerView.Adapter<BoardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: View = view.findViewById(R.id.square_container)
        val ivPlanet: ImageView = view.findViewById(R.id.iv_planet)
        val tvTeam1: TextView = view.findViewById(R.id.tv_team1_ships)
        val tvTeam2: TextView = view.findViewById(R.id.tv_team2_ships)
        val tvTeam3: TextView = view.findViewById(R.id.tv_team3_ships)
        val tvTeam4: TextView = view.findViewById(R.id.tv_team4_ships)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_board_square, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cell = cells[position]
        
        // Background color based on owner
        val colorRes = when (cell.teamOwner) {
            1 -> R.color.teamcolor_blue
            2 -> R.color.teamcolor_red
            3 -> R.color.teamcolor_pink
            4 -> R.color.teamcolor_green
            else -> android.R.color.transparent
        }
        holder.container.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, colorRes))

        // Planet visibility
        holder.ivPlanet.visibility = if (cell.hasPlanet) View.VISIBLE else View.GONE

        // Ship counts
        holder.tvTeam1.text = if (cell.ships[0] > 0) cell.ships[0].toString() else ""
        holder.tvTeam2.text = if (cell.ships[1] > 0) cell.ships[1].toString() else ""
        holder.tvTeam3.text = if (cell.ships[2] > 0) cell.ships[2].toString() else ""
        holder.tvTeam4.text = if (cell.ships[3] > 0) cell.ships[3].toString() else ""
    }

    override fun getItemCount() = cells.size
}