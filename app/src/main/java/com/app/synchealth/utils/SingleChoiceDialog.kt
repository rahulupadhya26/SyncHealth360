package com.app.synchealth.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AlertDialog

object SingleChoiceDialog {

    lateinit var selectedItem: String
    fun displaySingleSelectionPopUp(
        context: Context,
        listItems: Array<String>,
        bitmap: Bitmap?
    ) {
        val mBuilder = AlertDialog.Builder(context)
        mBuilder.setTitle("Choose an item")
        mBuilder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->
            selectedItem = listItems[i]
        }
        // Set the neutral/cancel button click listener
        mBuilder.setNeutralButton("Cancel") { dialog, which ->
            // Do something when click the neutral button
            dialog.cancel()
        }

        mBuilder.setPositiveButton("Ok") { dialog, which ->
            // Do something when click the neutral button
            dialog.cancel()
            if (selectedItem.equals("Feed") || selectedItem.equals("Facebook") || selectedItem.equals(
                    "Instagram"
                )
            ) {
               // handleItemClick.onSingleChoiceClick(selectedItem, bitmap!!)
            }
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }
}