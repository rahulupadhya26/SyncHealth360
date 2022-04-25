package com.app.synchealth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.data.Prescription
import com.app.synchealth.utils.Utils
import kotlinx.android.synthetic.main.layout_item_prescription.view.*

class PrescriptionAdapter(val context: Context, val list: ArrayList<Prescription>) :
    RecyclerView.Adapter<PrescriptionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PrescriptionAdapter.ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_item_prescription, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PrescriptionAdapter.ViewHolder, position: Int) {
        val item = list[position]
        holder.drug.text = item.drug
        holder.startDate.text = item.start_date
        holder.quantity.text = item.quantity
        holder.dosage.text = item.dosage
        var intervalTxt = "Consult doctor"
        when (item.interval) {
            Utils.CONST_1 -> intervalTxt = Utils.CONST_1_BID
            Utils.CONST_2 -> intervalTxt = Utils.CONST_2_TID
            Utils.CONST_3 -> intervalTxt = Utils.CONST_3_QID
            Utils.CONST_4 -> intervalTxt = Utils.CONST_4_Q3H
            Utils.CONST_5 -> intervalTxt = Utils.CONST_5_Q4H
            Utils.CONST_6 -> intervalTxt = Utils.CONST_6_Q5H
            Utils.CONST_7 -> intervalTxt = Utils.CONST_7_Q6H
            Utils.CONST_8 -> intervalTxt = Utils.CONST_8_Q8H
            Utils.CONST_9 -> intervalTxt = Utils.CONST_9_QD
            Utils.CONST_10 -> intervalTxt = Utils.CONST_10_AC
            Utils.CONST_11 -> intervalTxt = Utils.CONST_11_PC
            Utils.CONST_12 -> intervalTxt = Utils.CONST_12_AM
            Utils.CONST_13 -> intervalTxt = Utils.CONST_13_PM
            Utils.CONST_14 -> intervalTxt = Utils.CONST_14_ANTE
            Utils.CONST_15 -> intervalTxt = Utils.CONST_15_H
            Utils.CONST_16 -> intervalTxt = Utils.CONST_16_HS
            Utils.CONST_17 -> intervalTxt = Utils.CONST_17_PRN
            Utils.CONST_18 -> intervalTxt = Utils.CONST_18_STAT
        }
        holder.interval.text = intervalTxt
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val drug: TextView = itemView.txt_presc_drug
        val startDate: TextView = itemView.text_presc_start_date
        val quantity: TextView = itemView.text_presc_quantity
        val dosage: TextView = itemView.text_presc_dosage
        val interval: TextView = itemView.text_presc_interval
    }
}