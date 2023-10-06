package com.app.synchealth.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.data.PrevConsultData
import com.app.synchealth.databinding.LayoutItemPrevConsultBinding
import java.util.ArrayList

class PreviousConsultationAdapter(
    val context: Context,
    val list: ArrayList<PrevConsultData?>
) :
    RecyclerView.Adapter<PreviousConsultationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PreviousConsultationAdapter.ViewHolder {
        val binding = LayoutItemPrevConsultBinding.inflate(
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
            txtPrevConsultDoctorName.text = "Dr. " + item!!.fname + " " + item.lname
            txtPrevConsultDoctorTitle.text = item.physician_type
            txtPrevConsultDoctorAddress.text =
                item.located_street + "," +
                        item.located_city + "," +
                        item.located_state + "," +
                        item.located_zipcode + "," +
                        item.located_country
            txtPrevConsultDate.text = "Date : " + item.pc_eventDate
            txtPrevConsultTime.text = "Time : " + item.pc_startTime
        }
    }

    inner class ViewHolder(val binding: LayoutItemPrevConsultBinding) :
        RecyclerView.ViewHolder(binding.root)
}