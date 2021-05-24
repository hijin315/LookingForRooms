package com.jinny.lookingforrooms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RoomViewPagerAdpater(val itemClicked: (RoomsModel) -> Unit) :
    ListAdapter<RoomsModel, RoomViewPagerAdpater.ItemViewHolder>(differ) {
    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(roomsModel: RoomsModel) {
            val titleTextView = view.findViewById<TextView>(R.id.item_title_textView)
            val hourTextView = view.findViewById<TextView>(R.id.item_hour_textView)
            val itemImageView = view.findViewById<ImageView>(R.id.item_imageView)

            titleTextView.text = roomsModel.title
            if(!roomsModel.hourInfo.isNullOrBlank()) hourTextView.text = roomsModel.hourInfo
            view.setOnClickListener {
                itemClicked(roomsModel)
            }
            Glide.with(itemImageView.context)
                .load(roomsModel.imgUrl)
                .into(itemImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.item_for_viewpager, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val differ = object : DiffUtil.ItemCallback<RoomsModel>() {
            override fun areItemsTheSame(oldItem: RoomsModel, newItem: RoomsModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: RoomsModel, newItem: RoomsModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}