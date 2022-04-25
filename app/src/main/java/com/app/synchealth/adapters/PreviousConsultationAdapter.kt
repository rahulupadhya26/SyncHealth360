package com.app.synchealth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.data.PrevConsultData
import kotlinx.android.synthetic.main.layout_item_prev_consult.view.*
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
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_prev_consult, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PreviousConsultationAdapter.ViewHolder, position: Int) {
        val item = list[position]
        holder.doctorName.text = "Dr. " + item!!.fname + " " + item!!.lname
        holder.physicianType.text = item!!.physician_type
        holder.address.text =
            item!!.located_street + "," + item!!.located_city + "," + item!!.located_state + "," + item!!.located_zipcode + "," + item!!.located_country
        holder.apptDate.text = "Date : " + item!!.pc_eventDate
        holder.apptTime.text = "Time : " + item!!.pc_startTime

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorName: TextView = itemView.txt_prev_consult_doctor_name
        val physicianType: TextView = itemView.txt_prev_consult_doctor_title
        val address: TextView = itemView.txt_prev_consult_doctor_address
        val apptDate: TextView = itemView.txt_prev_consult_date
        val apptTime: TextView = itemView.txt_prev_consult_time
    }
}