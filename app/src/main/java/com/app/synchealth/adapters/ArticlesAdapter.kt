package com.app.synchealth.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.Articles
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.facebook.react.bridge.ReactApplicationContext
import kotlinx.android.synthetic.main.layout_item_articles.view.*

class ArticlesAdapter (val context: Context, val list: List<Articles>) :
    RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {
    val rctAes = RCTAes(ReactApplicationContext(context))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesAdapter.ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_articles, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ArticlesAdapter.ViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item.title
        holder.description.text = item.description
        holder.link.text = item.feed_url
        /*val imageBytes = Base64.decode(item.image, 0)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)*/
        Glide.with(context).load(item.image_url).transform(CenterCrop(), RoundedCorners(5))
            .into(holder.image)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.text_article_title
        val description: TextView = itemView.text_article_description
        val link: TextView = itemView.text_article_link
        val image: ImageView = itemView.img_article
    }
}