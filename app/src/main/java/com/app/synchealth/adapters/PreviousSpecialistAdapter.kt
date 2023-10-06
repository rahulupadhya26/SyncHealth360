package com.app.synchealth.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.controller.OnPrevSpecialistItemClickListener
import com.app.synchealth.data.PrevSpecialist
import com.app.synchealth.databinding.LayoutItemPrevSpecialistBinding

class PreviousSpecialistAdapter(
    val context: Context,
    val list: ArrayList<PrevSpecialist>,
    private val adapterItemClickListener: OnPrevSpecialistItemClickListener?
) : RecyclerView.Adapter<PreviousSpecialistAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PreviousSpecialistAdapter.ViewHolder {
        val binding = LayoutItemPrevSpecialistBinding.inflate(
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
        val item = list[position]
        holder.binding.txtDoctorName.text = item.fname + " " + item.lname
        holder.binding.txtDoctorTitle.text = item.title
        holder.binding.cardviewLayoutDoctor.setOnClickListener {
            adapterItemClickListener!!.OnPrevSpecialistItemClickListener(item)
        }
    }

    inner class ViewHolder(val binding: LayoutItemPrevSpecialistBinding) : RecyclerView.ViewHolder(binding.root)
}