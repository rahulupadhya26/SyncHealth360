package com.app.synchealth.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.controller.OnDoctorItemClickListener
import com.app.synchealth.data.DoctorsList
import com.app.synchealth.databinding.LayoutItemDoctorsListBinding

class DoctorsListAdapter(
    val context: Context,
    val list: List<DoctorsList>,
    private val adapterItemClickListener: OnDoctorItemClickListener?
) :
    RecyclerView.Adapter<DoctorsListAdapter.ViewHolder>() {
    var row_index: Int = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DoctorsListAdapter.ViewHolder {
        val binding = LayoutItemDoctorsListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val item = list[position]
            txtDoctorName.text = item.fname + " " + item.lname
            txtDoctorExp.text = "Year of exp : " + item.yrs_of_experience
            txtDoctorQualification.text = "Qualification : " + item.educational_qualification
            txtDoctorRating.text = "Rating : " + item.rating
            txtDoctorFees.text = "Fees : $" + item.consultation_charge
            cardviewLayoutDoctor.setOnClickListener {
                row_index = position;
                notifyDataSetChanged()
                adapterItemClickListener!!.OnDoctorItemClickListener(item)
            }
            if (row_index == position) {
                cardviewLayoutDoctor.setCardBackgroundColor(context.getColor(R.color.colorLightGrey))
            } else {
                cardviewLayoutDoctor.setCardBackgroundColor(context.getColor(R.color.colorWhite))
            }
        }
    }

    inner class ViewHolder(val binding: LayoutItemDoctorsListBinding) : RecyclerView.ViewHolder(binding.root)
}