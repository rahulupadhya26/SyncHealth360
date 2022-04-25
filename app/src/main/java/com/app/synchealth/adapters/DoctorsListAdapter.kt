package com.app.synchealth.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.controller.OnDoctorItemClickListener
import com.app.synchealth.data.DoctorsList
import kotlinx.android.synthetic.main.layout_item_doctors_list.view.*

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
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_doctors_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DoctorsListAdapter.ViewHolder, position: Int) {
        val item = list[position]
        holder.doctorName.text = item.fname + " " + item.lname
        holder.doctorExp.text = "Year of exp : " + item.yrs_of_experience
        holder.doctorQualification.text = "Qualification : " + item.educational_qualification
        holder.doctorRating.text = "Rating : " + item.rating
        holder.doctorFees.text = "Fees : $" + item.consultation_charge
        holder.doctorLayout.setOnClickListener {
            row_index = position;
            notifyDataSetChanged()
            adapterItemClickListener!!.OnDoctorItemClickListener(item)
        }
        if (row_index == position) {
            holder.doctorLayout.setCardBackgroundColor(context.getColor(R.color.colorLightGrey))
        } else {
            holder.doctorLayout.setCardBackgroundColor(context.getColor(R.color.colorWhite))
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorImage: ImageView = itemView.img_doctor_pic
        val doctorName: TextView = itemView.txt_doctor_name
        val doctorExp: TextView = itemView.txt_doctor_exp
        val doctorQualification: TextView = itemView.txt_doctor_qualification
        val doctorRating: TextView = itemView.txt_doctor_rating
        val doctorFees: TextView = itemView.txt_doctor_fees
        val doctorLayout: CardView = itemView.cardview_layout_doctor

    }
}