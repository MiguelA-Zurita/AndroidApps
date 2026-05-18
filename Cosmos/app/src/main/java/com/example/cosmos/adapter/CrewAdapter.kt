package com.example.cosmos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmos.R

class CrewAdapter(
    private val crewMembers: List<String?>,
    private val onEditClick: (Int) -> Unit
) : RecyclerView.Adapter<CrewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCrewName: TextView = view.findViewById(R.id.tv_crew_name)
        val btnEditCrew: TextView = view.findViewById(R.id.btn_edit_crew)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_crew_member, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCrewName.text = crewMembers[position]
        holder.btnEditCrew.setOnClickListener {
            onEditClick(position)
        }
    }

    override fun getItemCount() = crewMembers.size
}
