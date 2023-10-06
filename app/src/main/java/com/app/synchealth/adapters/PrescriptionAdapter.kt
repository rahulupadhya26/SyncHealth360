package com.app.synchealth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.data.Prescription
import com.app.synchealth.databinding.LayoutItemPrescriptionBinding
import com.app.synchealth.utils.Utils

class PrescriptionAdapter(val context: Context, val list: ArrayList<Prescription>) :
    RecyclerView.Adapter<PrescriptionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PrescriptionAdapter.ViewHolder {
        val binding = LayoutItemPrescriptionBinding.inflate(
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
            txtPrescDrug.text = item.drug
            textPrescStartDate.text = item.start_date
            textPrescQuantity.text = item.quantity
            textPrescDosage.text = item.dosage
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
            textPrescInterval.text = intervalTxt
        }
    }

    inner class ViewHolder(val binding: LayoutItemPrescriptionBinding) :
        RecyclerView.ViewHolder(binding.root)
}