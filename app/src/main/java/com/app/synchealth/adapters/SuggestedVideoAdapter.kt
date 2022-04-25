package com.app.synchealth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.controller.OnSuggestedVideoClickListener
import com.app.synchealth.crypto.RCTAes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.facebook.react.bridge.ReactApplicationContext
import kotlinx.android.synthetic.main.layout_item_suggested_videos.view.*

class SuggestedVideoAdapter (val context: Context, val list: ArrayList<String>, private val adapterItemClickListener: OnSuggestedVideoClickListener?) :
    RecyclerView.Adapter<SuggestedVideoAdapter.ViewHolder>() {
    val rctAes = RCTAes(ReactApplicationContext(context))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestedVideoAdapter.ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_suggested_videos, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SuggestedVideoAdapter.ViewHolder, position: Int) {
        val item = list[position]
        Glide.with(context).load(item).transform(CenterCrop(), RoundedCorners(5))
            .into(holder.suggestedVideo)
        holder.suggestedVideo.setOnClickListener {
            adapterItemClickListener!!.OnSuggestedVideoClickListener(item)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val suggestedVideo: ImageView = itemView.img_suggested_video
    }
}