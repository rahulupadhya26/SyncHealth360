package com.app.synchealth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.data.Articles
import com.app.synchealth.databinding.LayoutItemArticlesBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class ArticlesAdapter(val context: Context, val list: List<Articles>) :
    RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesAdapter.ViewHolder {
        val binding = LayoutItemArticlesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val item = list[position]
            textArticleTitle.text = item.title
            textArticleDescription.text = item.description
            textArticleLink.text = item.feed_url
            Glide.with(context).load(item.image_url).transform(CenterCrop(), RoundedCorners(5))
                .into(imgArticle)
        }
    }

    inner class ViewHolder(val binding: LayoutItemArticlesBinding) :
        RecyclerView.ViewHolder(binding.root)
}