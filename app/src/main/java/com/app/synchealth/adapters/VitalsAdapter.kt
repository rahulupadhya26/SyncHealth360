package com.app.synchealth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.data.Vitals
import kotlinx.android.synthetic.main.layout_item_vitals.view.*

class VitalsAdapter(val context: Context, val list: ArrayList<Vitals>) :
    RecyclerView.Adapter<VitalsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VitalsAdapter.ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_vitals, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VitalsAdapter.ViewHolder, position: Int) {
        val item = list[position]
        holder.vitalHeight.text = item.height
        holder.tempMethod.text = if (item.temp_method == null) "NA" else item.temp_method
        holder.respiration.text = item.respiration
        holder.weight.text = item.weight
        holder.temperature.text = item.temperature
        holder.pulse.text = item.pulse
        holder.bmi.text = item.BMI
        holder.mostRecentVital.text = "Most recent vitals from : " + item.date + " per min"
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vitalHeight: TextView = itemView.text_vital_height
        val tempMethod: TextView = itemView.text_vital_temp_method
        val respiration: TextView = itemView.text_vital_respi
        val weight: TextView = itemView.text_vital_weight
        val temperature: TextView = itemView.text_vital_temp
        val pulse: TextView = itemView.text_vital_pulse
        val bmi: TextView = itemView.text_vital_bmi
        val mostRecentVital: TextView = itemView.txt_recent_vital_date_time
    }
}