package com.example.kotlin_youtubeplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_youtubeplayer.data.VideoDetailRes
import com.example.kotlin_youtubeplayer.databinding.ItemCaptionBinding

class VideoCaptionAdapter(val captionList: ArrayList<VideoDetailRes.Captions>): RecyclerView.Adapter<VideoCaptionAdapter.ViewHolder>() {

    inner class ViewHolder (val binding: ItemCaptionBinding): RecyclerView.ViewHolder(binding.root)

    lateinit var onItemClick: ((Int, Double) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoCaptionAdapter.ViewHolder =
        ViewHolder(ItemCaptionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            val item = captionList[position]

            binding.apply {
                tvCaption.text = item.content
                tvNumber.text = position.toString()

                root.setBackgroundColor(root.context.resources.getColor(android.R.color.white, null))

                root.setOnClickListener {
                    onItemClick(position, item.miniSecond)
                }
            }
        }
    }

    override fun getItemCount(): Int = captionList.size

}