package com.app.synchealth.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.app.synchealth.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.synthetic.main.layout_item_image_grid.view.*

class ImageGrid(context: Context,attrs: AttributeSet?): LinearLayout(context,attrs) {


    @SuppressLint("InflateParams")
    fun showImages(imageList:List<String>)
    {
            val view: View = LayoutInflater.from(context).inflate(R.layout.layout_item_image_grid,null)
            val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT)
            view.layoutParams = params
            val image1 = view.img1
            val image2 = view.img2
            val image3 = view.img3
            val image4 = view.img4
            val image5 = view.img5
            val layout3:LinearLayout = view.layout3
            val layout5:LinearLayout = view.layout5
            val textCount3:TextView = view.text3
            val textCount5:TextView = view.text5

            when(imageList.size)
            {
                1 -> {
                    displayImage(imageList[0],image1)
                }
                2 -> {
                    displayImage(imageList[0],image1)
                    displayImage(imageList[1],image2)
                }
                3 -> {
                    displayImage(imageList[0],image1)
                    displayImage(imageList[1],image2)
                    displayImage(imageList[2],image3)

                }
                4 -> {
                    displayImage(imageList[0],image1)
                    displayImage(imageList[1],image2)
                    displayImage(imageList[2],image3)
                    if(imageList.size >=4)
                    displayCount(layout3,textCount3,imageList.size-3)
                }

                else -> {
                    displayImage(imageList[0],image1)
                    displayImage(imageList[1],image2)
                    displayImage(imageList[2],image3)
                    displayImage(imageList[3],image4)
                    displayImage(imageList[4],image5)
                    if(imageList.size >5)
                        displayCount(layout5,textCount5,imageList.size-5)
                }

            }
        removeAllViews()
        addView(view)
    }

    private fun displayImage(imgUrl:String, imageView: ImageView)
    {
        imageView.visibility = View.VISIBLE
        Glide.with(context).load(imgUrl).transform(CenterCrop(),RoundedCorners(20)).into(imageView)
    }

    private fun displayCount(layout:LinearLayout,textView: TextView,count:Int)
    {
        layout.visibility = View.VISIBLE
        textView.visibility = View.VISIBLE
        val text = "+$count"
        textView.text = text
    }
}