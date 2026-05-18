package com.example.cosmos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmos.R
import com.example.cosmos.model.Tripulante

class ReadOnlyCrewAdapter(private val crewMembers: List<Tripulante>) : RecyclerView.Adapter<ReadOnlyCrewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCrewName: TextView = view.findViewById(R.id.tv_crew_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_crew_member_read_only, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvCrewName.text = crewMembers[position].nom
    }

    override fun getItemCount() = crewMembers.size
}
