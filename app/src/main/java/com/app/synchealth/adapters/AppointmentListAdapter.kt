package com.app.synchealth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.controller.OnAppointmentItemClickListener
import com.app.synchealth.data.AppointmentList
import com.app.synchealth.databinding.LayoutItemAppointmentListBinding
import com.app.synchealth.utils.Utils


class AppointmentListAdapter(
    val context: Context,
    val list: List<AppointmentList>,
    private val adapterItemClickListener: OnAppointmentItemClickListener?
) :
    RecyclerView.Adapter<AppointmentListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentListAdapter.ViewHolder {
        val binding = LayoutItemAppointmentListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        /*val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_appointment_list, parent, false)*/
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val item = list[position]
            txtPcTitle.text = item.provider_type
            txtFname.text = "Dr. " + item.fname + " " + item.lname
            txtDateTime.text = item.pc_eventDate + "\n" + item.pc_startTime
            layoutChat.setOnClickListener {
                adapterItemClickListener!!.OnAppointmentItemClickListener(
                    Utils.EVENT_APPOINTMENT_CHAT,
                    item
                )
            }
            layoutEmail.setOnClickListener {
                adapterItemClickListener!!.OnAppointmentItemClickListener(
                    Utils.EVENT_APPOINTMENT_EMAIL,
                    item
                )
            }
            btnStartConsult.setOnClickListener {
                adapterItemClickListener!!.OnAppointmentItemClickListener(
                    Utils.EVENT_START_APPOINTMENT,
                    item
                )
            }
            btnCancelConsult.setOnClickListener {
                adapterItemClickListener!!.OnAppointmentItemClickListener(
                    Utils.EVENT_CANCEL_APPOINTMENT,
                    item
                )
            }
        }
    }

    inner class ViewHolder(val binding: LayoutItemAppointmentListBinding) :
        RecyclerView.ViewHolder(binding.root)
}