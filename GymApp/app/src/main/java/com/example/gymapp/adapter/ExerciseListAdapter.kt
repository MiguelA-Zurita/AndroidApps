package com.example.gymapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.model.ExerciseList

class  ExerciseListAdapter (private val items: MutableList <ExerciseList>,
                           private val OnClick: (ExerciseList) -> Unit = {}
) : RecyclerView.Adapter<ExerciseListAdapter.ItemViewHolder>(){

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val img: ImageView = itemView.findViewById(R.id.rv_iv_icon)
        val title: TextView = itemView.findViewById(R.id.rv_tv_title)
        val description: TextView = itemView.findViewById(R.id.rv_tv_desc)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExerciseListAdapter.ItemViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rv_item, parent, false)
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: ExerciseListAdapter.ItemViewHolder,
        position: Int
    ) {
        val e = items [position]
        holder.title.text = e.titulo
        holder.description.text = e.descripcion
        holder.img.setImageResource(e.imagen)
        holder.itemView.setOnClickListener { OnClick(e) }
    }

    override fun getItemCount(): Int = items.size
}
