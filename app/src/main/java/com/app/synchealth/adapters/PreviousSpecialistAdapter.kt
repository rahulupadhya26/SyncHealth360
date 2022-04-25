package com.app.synchealth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.controller.OnPrevSpecialistItemClickListener
import com.app.synchealth.data.PrevSpecialist
import kotlinx.android.synthetic.main.layout_item_doctors_list.view.cardview_layout_doctor
import kotlinx.android.synthetic.main.layout_item_doctors_list.view.txt_doctor_name
import kotlinx.android.synthetic.main.layout_item_prev_specialist.view.*

class PreviousSpecialistAdapter(
    val context: Context,
    val list: ArrayList<PrevSpecialist>,
    private val adapterItemClickListener: OnPrevSpecialistItemClickListener?
) : RecyclerView.Adapter<PreviousSpecialistAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PreviousSpecialistAdapter.ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_prev_specialist, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PreviousSpecialistAdapter.ViewHolder, position: Int) {

        val item = list[position]
        holder.doctorName.text = item.fname + " " + item.lname
        holder.doctorTitle.text = item.title
        holder.doctorLayout.setOnClickListener {
            adapterItemClickListener!!.OnPrevSpecialistItemClickListener(item)
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorName: TextView = itemView.txt_doctor_name
        val doctorTitle: TextView = itemView.txt_doctor_title
        val doctorLayout: CardView = itemView.cardview_layout_doctor
    }
}