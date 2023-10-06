package com.app.synchealth.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.data.CalendarData
import com.app.synchealth.databinding.LayoutItemAppointmentListBinding
import com.app.synchealth.databinding.RowCalendarDateBinding


/*
*   Note :-
*
*   RecyclerView List Adapter not working Here
*
*   It's show weird animation when submit list.
*
*   So, Old RecyclerView Adapter is used....
*
* */


class CalendarAdapter(
    private val context: Context,
    private val calendarInterface: CalendarInterface,
    private val list: ArrayList<CalendarData>
) :
    RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowCalendarDateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val item = list[position]
            if (item.isSelected) {
                tvCalendarDay.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorWhite
                    )
                )
                tvCalendarDate.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorWhite
                    )
                )
                cardCalendar.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
            } else {
                tvCalendarDay.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorBlack
                    )
                )
                tvCalendarDate.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorBlack
                    )
                )
                cardCalendar.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorWhite
                    )
                )
            }

            tvCalendarDay.text = item.calendarDay
            tvCalendarDate.text = item.calendarDate
            cardCalendar.setOnClickListener {
                calendarInterface.onSelect(item, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(calendarList: ArrayList<CalendarData>) {
        list.clear()
        list.addAll(calendarList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: RowCalendarDateBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface CalendarInterface {
        fun onSelect(calendarData: CalendarData, position: Int)
    }

}
