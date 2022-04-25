package com.app.synchealth.utils

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions

object NSFWDetector {

    private const val LABEL_SFW = "nude"
    private const val LABEL_NSFW = "nonnude"
    private const val CONFIDENCE_THRESHOLD: Float = 0.7F

    private val localModel = FirebaseAutoMLLocalModel.Builder()
        .setAssetFilePath("automl/manifest.json")
        .build()

    private val options =
        FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel).build()
    private val interpreter = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options)

    fun isNSFW(
        bitmap: Bitmap,
        confidenceThreshold: Float = CONFIDENCE_THRESHOLD,
        callback: (Boolean, String, Float, Bitmap) -> Unit
    ) {
        var threshold = confidenceThreshold

        if (threshold < 0 && threshold > 1) {
            threshold = CONFIDENCE_THRESHOLD
        }
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        interpreter.processImage(image).addOnSuccessListener { labels ->
            try {
                val label = labels[0]
                when (label.text) {
                    LABEL_SFW -> {
                        if (label.confidence > threshold) {
                            callback(true, LABEL_SFW, label.confidence, bitmap)
                        } else {
                            callback(false, LABEL_SFW, label.confidence, bitmap)
                        }
                    }
                    LABEL_NSFW -> {
                        if (label.confidence < (1 - threshold)) {
                            callback(true, LABEL_NSFW, label.confidence, bitmap)
                        } else {
                            callback(false, LABEL_NSFW, label.confidence, bitmap)
                        }
                    }
                    else -> {
                        callback(false, "", 0.0F, bitmap)
                    }
                }
            } catch (e: Exception) {
                Log.e("NSFW", e.localizedMessage ?: "NSFW Scan Error")
                callback(false, "", 0.0F, bitmap)
            }
        }.addOnFailureListener { e ->
            Log.e("NSFW", e.localizedMessage ?: "NSFW Scan Error")
            callback(false, "", 0.0F, bitmap)
        }
    }

}