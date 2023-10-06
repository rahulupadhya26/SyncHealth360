package com.app.synchealth.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.data.Vitals
import com.app.synchealth.databinding.LayoutItemVitalsBinding

class VitalsAdapter(val context: Context, val list: ArrayList<Vitals>) :
    RecyclerView.Adapter<VitalsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VitalsAdapter.ViewHolder {
        val binding = LayoutItemVitalsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val item = list[position]
            textVitalHeight.text = item.height
            textVitalTempMethod.text = if (item.temp_method == null) "NA" else item.temp_method
            textVitalRespi.text = item.respiration
            textVitalWeight.text = item.weight
            textVitalTemp.text = item.temperature
            textVitalPulse.text = item.pulse
            textVitalBmi.text = item.BMI
            txtRecentVitalDateTime.text = "Most recent vitals from : " + item.date + " per min"
        }
    }

    inner class ViewHolder(val binding: LayoutItemVitalsBinding) :
        RecyclerView.ViewHolder(binding.root)
}