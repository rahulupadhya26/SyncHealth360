package com.app.synchealth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.controller.OnSuggestedVideoClickListener
import com.app.synchealth.databinding.LayoutItemSuggestedVideosBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class SuggestedVideoAdapter(
    val context: Context,
    val list: ArrayList<String>,
    private val adapterItemClickListener: OnSuggestedVideoClickListener?
) :
    RecyclerView.Adapter<SuggestedVideoAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SuggestedVideoAdapter.ViewHolder {
        val binding = LayoutItemSuggestedVideosBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SuggestedVideoAdapter.ViewHolder, position: Int) {
        val item = list[position]
        Glide.with(context).load(item).transform(CenterCrop(), RoundedCorners(5))
            .into(holder.binding.imgSuggestedVideo)
        holder.binding.imgSuggestedVideo.setOnClickListener {
            adapterItemClickListener!!.OnSuggestedVideoClickListener(item)
        }
    }

    inner class ViewHolder(val binding: LayoutItemSuggestedVideosBinding) :
        RecyclerView.ViewHolder(binding.root)
}