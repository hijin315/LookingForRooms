package com.jinny.lookingforrooms

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class RoomListAdpater : ListAdapter<RoomsModel, RoomListAdpater.ItemViewHolder>(differ) {
    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(roomsModel: RoomsModel) {
            val titleTextView = view.findViewById<TextView>(R.id.item_title_textView)
            val priceTextView = view.findViewById<TextView>(R.id.item_price_textView)
            val itemImageView = view.findViewById<ImageView>(R.id.item_imageView)

            titleTextView.text = roomsModel.title
            priceTextView.text = roomsModel.price
            Glide.with(itemImageView.context)
                .load(roomsModel.imgUrl)
                .transform(CenterCrop(), RoundedCorners(dpToPx(itemImageView.context,12)))
                .into(itemImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.item_rooms, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    // dp 값을 pixel 값으로 변경해주는 함수!
    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
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