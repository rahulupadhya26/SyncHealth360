package com.app.synchealth.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.app.synchealth.R
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.TimeSlot
import com.app.synchealth.databinding.FragmentSyncHealthTimeSlotBinding
import com.app.synchealth.utils.Utils
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SyncHealthTimeSlot.newInstance] factory method to
 * create an instance of this fragment.
 */
class SyncHealthTimeSlot : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var selectedTimeSlot: String? = null
    private lateinit var binding: FragmentSyncHealthTimeSlotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sync_health_time_slot
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSyncHealthTimeSlotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHeader().visibility = View.GONE
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.VISIBLE
        getSubTitle().text = getString(R.string.txt_nav_menu_sync_health_schedule)
        binding.btnAvailableSlotsTxt.setOnClickListener {
            if (binding.txtAppointmentDate.text.isNotEmpty()) {
                getTimeSlotList(view)
            } else {
                displayToast("Please select date")
            }
        }
        binding.btnTimeSlot.setOnClickListener {
            if (binding.txtAppointmentDate.text.isNotEmpty()) {
                if (selectedTimeSlot != null) {
                    Utils.aptScheduleDate = binding.txtAppointmentDate.text.toString()
                    Utils.aptScheduleTime = selectedTimeSlot!!.split("-")[0]
                    Utils.pharmacyVal = "Apollo"
                    replaceFragment(
                        SyncHealthMedication(),
                        R.id.layout_home,
                        SyncHealthMedication.TAG
                    )
                } else {
                    displayToast("Please select time slot to continue")
                }
            } else {
                displayToast("Please select date")
            }
        }
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { views, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat)
                binding.txtAppointmentDate.text = sdf.format(cal.time)
                binding.scheduledDateTxt.text = "Scheduled date : " + sdf.format(cal.time)
            }
        binding.txtAppointmentDate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                mActivity!!, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }
    }

    fun timeSlotsSpinner(view: View, timeSlots: Array<Any>) {
        binding.layoutSpinner.visibility = View.VISIBLE
        // access the spinner
        val spinner = view.findViewById<Spinner>(R.id.spinner_appointment_time)
        if (spinner != null) {
            val adapter = ArrayAdapter(
                mActivity!!,
                android.R.layout.simple_list_item_1, timeSlots
            )
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    v: View, position: Int, id: Long
                ) {
                    selectedTimeSlot = timeSlots[position].toString()
                    binding.timeSlotTxt.text = "Time slot selected : " + timeSlots.get(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
    }

    private fun getTimeSlotList(view: View) {
        val rctAes = RCTAes()
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .getTimeSlot(
                        TimeSlot(
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString(Utils.providerType),
                            rctAes.encryptString(binding.txtAppointmentDate.text.toString()),
                            rctAes.encryptString(Utils.providerId)
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgress()
                            val responseBody = result.string()
                            Log.d("Response Body", responseBody)
                            if (responseBody != "TH204" && responseBody != "TH201") {
                                var map: Map<String, Any> = HashMap()
                                map = Gson().fromJson(responseBody, map.javaClass)
                                val timeSlots: Array<Any> = map.toSortedMap().values.toTypedArray()
                                timeSlotsSpinner(view, timeSlots)
                            } else {
                                displayToast("No slots available $responseBody")
                            }
                        } catch (e: Exception) {
                            hideProgress()
                            displayToast("Something went wrong.. Please try after sometime")
                        }
                    }, { error ->
                        hideProgress()
                        displayToast("Error ${error.localizedMessage}")
                    })
            )
        }
        handler!!.postDelayed(runnable!!, 1000)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SyncHealthTimeSlot.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SyncHealthTimeSlot().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_sync_health_consult_time_slot"
    }
}