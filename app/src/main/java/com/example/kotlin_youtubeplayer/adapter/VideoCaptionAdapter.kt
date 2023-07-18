package com.example.kotlin_youtubeplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlin_youtubeplayer.data.VideoDetailRes
import com.example.kotlin_youtubeplayer.databinding.ItemCaptionBinding

class VideoCaptionAdapter(val captionList: ArrayList<VideoDetailRes.Captions>): RecyclerView.Adapter<VideoCaptionAdapter.ViewHolder>() {

    inner class ViewHolder (val binding: ItemCaptionBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoCaptionAdapter.ViewHolder =
        ViewHolder(ItemCaptionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            val item = captionList[position]

            binding.tvCaption.text = item.content
            binding.tvNumber.text = position.toString()
        }
    }

    override fun getItemCount(): Int = captionList.size

}