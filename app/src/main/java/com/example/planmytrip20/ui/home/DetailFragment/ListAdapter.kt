package com.example.planmytrip20.ui.home.DetailFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.Item


class ListAdapter(private val itemList: List<Item>, private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    // Interface to handle item click events
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    // Called when RecyclerView needs a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the item layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    // Called to bind data to a ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the current item from the itemList
        val currentItem = itemList[position]

        // Use Glide to load the image from the URL
        Glide.with(holder.itemView)
            .load(currentItem.url)
            .into(holder.imageView)

//        holder.textView.text = currentItem.Name
        holder.textView.text = currentItem.description

        // Set click listener for the item view
        holder.itemView.setOnClickListener {
            // Notify the listener when an item is clicked
            onItemClickListener.onItemClick(position)
        }

        // Set the image resource and description for the item
//        holder.imageView.setImageResource(currentItem.imageRes)
//        holder.textView.text = currentItem.description
    }

    // Return the number of items in the itemList
    override fun getItemCount(): Int {
        return itemList.size
    }

    // ViewHolder class for caching views
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textView)
    }
}
