package com.example.planmytrip20.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.planmytrip20.R
import com.example.planmytrip20.classes.Item

class HomepageAdapter(
    private val itemList: List<Item>,
    private val onItemClickListener: OnItemClickListener,
    private val orientation: ItemListOrientation = ItemListOrientation.VERTICAL
) :
    RecyclerView.Adapter<HomepageAdapter.ViewHolder>() {

    // Interface for defining click listener
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the item_list layout and create a ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]

        // Use Glide to load the image from the URL
        Glide.with(holder.itemView)
            .load(currentItem.url)
            .into(holder.imageView)

        holder.textView.text = currentItem.Name
//        holder.textView2.text = currentItem.description


        // Set click listener for the item view
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(position)
        }

        // Set image resource and text for the item
//        holder.imageView.setImageResource(currentItem.imageRes)
//        holder.textView.text = currentItem.Name

        // Set orientation for the parent LinearLayout based on the provided ItemListOrientation
        holder.parentLinearLayout.orientation = when (orientation) {
            ItemListOrientation.VERTICAL -> LinearLayout.VERTICAL
            ItemListOrientation.HORIZONTAL -> LinearLayout.HORIZONTAL
        }

    }

    override fun getItemCount(): Int {
        // Return the total number of items in the list
        return itemList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textView)
        val parentLinearLayout: LinearLayout = itemView.findViewById(R.id.parentLinearLayout)

    }
}
