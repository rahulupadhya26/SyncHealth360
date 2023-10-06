package com.app.synchealth.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import com.app.synchealth.R
import com.app.synchealth.adapters.CalendarAdapter
import com.app.synchealth.data.CalendarData
import com.app.synchealth.databinding.FragmentAuthCodeBinding
import com.app.synchealth.databinding.FragmentDoctorDashboardBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DoctorDashboard.newInstance] factory method to
 * create an instance of this fragment.
 */
class DoctorDashboard : BaseFragment(), CalendarAdapter.CalendarInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val cal = Calendar.getInstance(Locale.ENGLISH)

    private var calendarAdapter: CalendarAdapter? = null

    private val calendarList = ArrayList<CalendarData>()
    private lateinit var binding: FragmentDoctorDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_doctor_dashboard
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDoctorDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSubTitle().text = ""
        getBackButton().visibility = View.GONE
        getSubTitle().visibility = View.GONE

        calendarAdapter = CalendarAdapter(requireActivity(), this, arrayListOf())

        binding.monthYearPicker.text = sdf.format(cal.time)
        binding.calendarView.setHasFixedSize(true)
        binding.calendarView.adapter = calendarAdapter

        binding.monthYearPicker.setOnClickListener {
            displayDatePicker()
        }

        binding.imgDoctorLogout.setOnClickListener {
            clearCache()
        }

        getDates()
    }

    private fun displayDatePicker() {
        val materialDateBuilder: MaterialDatePicker.Builder<Long> =
            MaterialDatePicker.Builder.datePicker()
        materialDateBuilder.setTitleText("Select Date")

        val materialDatePicker = materialDateBuilder.build()

        materialDatePicker.show(requireActivity().supportFragmentManager, "MATERIAL_DATE_PICKER")

        materialDatePicker.addOnPositiveButtonClickListener {
            try {
                binding.monthYearPicker.text = sdf.format(it)
                cal.time = Date(it)
                getDates()
            } catch (e: ParseException) {
                Log.d("TAG", "displayDatePicker: ${e.message}")
            }
        }
    }

    private fun getDates() {
        val dateList = ArrayList<CalendarData>() // For our Calendar Data Class
        val dates = ArrayList<Date>() // For Date
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        //  dates = 0 < MAX DAYS = 28 (For FEB)
        //  dates = 1 < MAX DAYS = 28 (For FEB)
        //  dates = 2 < MAX DAYS = 28 (For FEB)
        //  .....

        while (dates.size < maxDaysInMonth) {
            dates.add(monthCalendar.time)
            if (cal.time == monthCalendar.time) {
                dateList.add(CalendarData(monthCalendar.time, true))
            } else {
                dateList.add(CalendarData(monthCalendar.time))
            }
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)   // Increment Day By 1
        }
        calendarList.clear()
        calendarList.addAll(dateList)
        calendarAdapter!!.updateList(dateList)
    }

    override fun onSelect(calendarData: CalendarData, position: Int) {
        // You can get Selected date here....
        calendarList.forEachIndexed { index, calendarModel ->
            calendarModel.isSelected = index == position
        }
        calendarAdapter!!.updateList(calendarList)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DoctorDashboard.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DoctorDashboard().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_Doctor_dashboard"
    }
}