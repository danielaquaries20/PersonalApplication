package com.daniel.personalapplication.activity.recycler_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.daniel.personalapplication.R
import com.daniel.personalapplication.activity.ProfileActivity
import com.daniel.personalapplication.data.database.Note

class AdapterRvNote(
    private val context: Context,
    private val onItemClick: (position: Int, data: Note) -> Unit
) : RecyclerView.Adapter<AdapterRvNote.Companion.NoteViewHolder>() {

    private var listData = emptyList<Note>()

    companion object {
        class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvTitle: TextView = view.findViewById(R.id.tv_title)
            val tvNote: TextView = view.findViewById(R.id.tv_note)
            val ivPhoto: ImageView = view.findViewById(R.id.iv_photo)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_data_note, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentItem = listData[position]

        holder.tvTitle.text = currentItem.title
        holder.tvNote.text = currentItem.note

        if (currentItem.photo.isEmpty()) {
            holder.ivPhoto.isVisible = false
        } else {
            val photo = ProfileActivity().stringToBitmap(currentItem.photo)
            photo?.let {
                holder.ivPhoto.setImageBitmap(it)
            }
        }

        holder.itemView.setOnClickListener { onItemClick(position, currentItem) }
    }

    fun setData(list: List<Note>) {
        this.listData = list
        notifyDataSetChanged()
    }
}