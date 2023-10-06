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
import com.app.synchealth.databinding.LayoutItemImageGridBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class ImageGrid(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {


    @SuppressLint("InflateParams")
    fun showImages(imageList: List<String>) {
        val binding = LayoutItemImageGridBinding.inflate(LayoutInflater.from(context))
        val view: View = binding.root
        val params = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        view.layoutParams = params
        val image1 = binding.img1
        val image2 = binding.img2
        val image3 = binding.img3
        val image4 = binding.img4
        val image5 = binding.img5
        val layout3: LinearLayout = binding.layout3
        val layout5: LinearLayout = binding.layout5
        val textCount3: TextView = binding.text3
        val textCount5: TextView = binding.text5

        when (imageList.size) {
            1 -> {
                displayImage(imageList[0], image1)
            }

            2 -> {
                displayImage(imageList[0], image1)
                displayImage(imageList[1], image2)
            }

            3 -> {
                displayImage(imageList[0], image1)
                displayImage(imageList[1], image2)
                displayImage(imageList[2], image3)

            }

            4 -> {
                displayImage(imageList[0], image1)
                displayImage(imageList[1], image2)
                displayImage(imageList[2], image3)
                if (imageList.size >= 4)
                    displayCount(layout3, textCount3, imageList.size - 3)
            }

            else -> {
                displayImage(imageList[0], image1)
                displayImage(imageList[1], image2)
                displayImage(imageList[2], image3)
                displayImage(imageList[3], image4)
                displayImage(imageList[4], image5)
                if (imageList.size > 5)
                    displayCount(layout5, textCount5, imageList.size - 5)
            }

        }
        removeAllViews()
        addView(view)
    }

    private fun displayImage(imgUrl: String, imageView: ImageView) {
        imageView.visibility = View.VISIBLE
        Glide.with(context).load(imgUrl).transform(CenterCrop(), RoundedCorners(20)).into(imageView)
    }

    private fun displayCount(layout: LinearLayout, textView: TextView, count: Int) {
        layout.visibility = View.VISIBLE
        textView.visibility = View.VISIBLE
        val text = "+$count"
        textView.text = text
    }
}