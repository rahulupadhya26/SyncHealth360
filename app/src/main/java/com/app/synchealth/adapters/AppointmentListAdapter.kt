package com.app.synchealth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.controller.OnAppointmentItemClickListener
import com.app.synchealth.data.AppointmentList
import com.app.synchealth.utils.Utils
import kotlinx.android.synthetic.main.layout_item_appointment_list.view.*


class AppointmentListAdapter(
    val context: Context,
    val list: List<AppointmentList>, private val adapterItemClickListener: OnAppointmentItemClickListener?
) :
    RecyclerView.Adapter<AppointmentListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentListAdapter.ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_appointment_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AppointmentListAdapter.ViewHolder, position: Int) {

        val item = list[position]
        holder.pcTitle.text = item.provider_type
        holder.doctorName.text = "Dr. " + item.fname + " " + item.lname
        holder.appointmentDateTime.text = item.pc_eventDate + "\n" + item.pc_startTime
        holder.appointmentChat.setOnClickListener {
            adapterItemClickListener!!.OnAppointmentItemClickListener(Utils.EVENT_APPOINTMENT_CHAT, item)
        }
        holder.appointmentEmail.setOnClickListener {
            adapterItemClickListener!!.OnAppointmentItemClickListener(Utils.EVENT_APPOINTMENT_EMAIL, item)
        }
        holder.startConsult.setOnClickListener {
            adapterItemClickListener!!.OnAppointmentItemClickListener(Utils.EVENT_START_APPOINTMENT, item)
        }
        holder.cancelConsult.setOnClickListener {
            adapterItemClickListener!!.OnAppointmentItemClickListener(Utils.EVENT_CANCEL_APPOINTMENT, item)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pcTitle: TextView = itemView.txt_pc_title
        val doctorName: TextView = itemView.txt_fname
        val appointmentDateTime: TextView = itemView.txt_date_time
        val appointmentChat: LinearLayout = itemView.layout_chat
        val appointmentEmail: LinearLayout = itemView.layout_email
        val startConsult: Button = itemView.btn_start_consult
        val cancelConsult: Button = itemView.btn_cancel_consult
    }
}