package com.app.synchealth.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.adapters.DoctorsListAdapter
import com.app.synchealth.controller.OnDoctorItemClickListener
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.*
import com.app.synchealth.utils.Utils
import com.facebook.react.bridge.ReactApplicationContext
import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_sync_health_consult.view.*
import java.lang.Exception
import java.lang.reflect.Type

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SyncHealthConsult.newInstance] factory method to
 * create an instance of this fragment.
 */
class SyncHealthConsult : BaseFragment(), OnDoctorItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var views: View
    private lateinit var multiAutoCompleteTextViewPhysicianList: MultiAutoCompleteTextView
    var physiciansSortedList: List<CommonsList> = listOf()
    var physicianArray: ArrayList<String> = ArrayList()
    var physicians: ArrayList<DoctorsList> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sync_health_consult
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views = view
        getHeader().visibility = View.GONE
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.VISIBLE
        getSubTitle().text = getString(R.string.txt_nav_menu_sync_health_consult)
        selectPhysician()
        getPhysicianList()
        views.btn_previous_specialist_txt.setOnClickListener {
            replaceFragment(
                SyncHealthPrevSpecialist(),
                R.id.layout_home,
                SyncHealthPrevSpecialist.TAG
            )
        }
        views.btn_consult_next_step.setOnClickListener {
            if (!getText(multiAutoCompleteTextViewPhysicianList).isEmpty()) {
                if (!Utils.providerId.isEmpty()) {
                    replaceFragment(SyncHealthTimeSlot(), R.id.layout_home, SyncHealthTimeSlot.TAG)
                } else {
                    displayToast("Please select provider")
                }
            } else {
                displayToast("Please select physician")
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun selectPhysician() {
        multiAutoCompleteTextViewPhysicianList =
            views.findViewById(R.id.multi_auto_complete_text_physician)
        multiAutoCompleteTextViewPhysicianList.setPadding(15, 15, 15, 15)
        @Suppress("DEPRECATION")
        multiAutoCompleteTextViewPhysicianList.setTextColor(getResources().getColor(R.color.colorBlack))

        multiAutoCompleteTextViewPhysicianList.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        multiAutoCompleteTextViewPhysicianList.setOnItemClickListener { parent, v, position, id ->
            //get doctors list
            getDoctorsList(getPhysicianType())
        }

        multiAutoCompleteTextViewPhysicianList.setOnTouchListener(View.OnTouchListener { v, event ->
            if (physicianArray.size > 0) {
                var physicianText = multiAutoCompleteTextViewPhysicianList.getText().toString()
                if (!physicianText.isEmpty()) {
                    var physicianList =
                        physicianText.substring(0, physicianText.length - 2).split(",")
                            .toTypedArray()
                    if (physicianList.isNotEmpty()) {
                        var tempPhysicianList: ArrayList<String> = ArrayList()
                        tempPhysicianList.addAll(physicianArray)
                        for (physician in physicianList) {
                            tempPhysicianList.remove(physician.trim())
                        }
                        setPhysicianList(tempPhysicianList)
                    }
                } else {
                    physicians = ArrayList()
                    updatePhysiciansList(physicians)
                }
                multiAutoCompleteTextViewPhysicianList.showDropDown()
            }
            false
        })
    }

    private fun getPhysicianType(): String {
        val physician = multiAutoCompleteTextViewPhysicianList.text.toString()
        val physicianList = physician.substring(0, physician.length - 2).split(",")
            .toTypedArray()
        var optionType = ""
        for (physician in physicianList) {
            if (physicianArray.contains(physician.trim())) {
                optionType += physiciansSortedList[physicianArray.indexOf(physician.trim())].option_id + ", "
            }
        }
        return optionType.substring(0, optionType.length - 2).trim()
    }

    private fun setPhysicianList(physicianArray: ArrayList<String>) {
        // Create a new data adapter object.
        val arrayAdapter = ArrayAdapter(
            mActivity!!,
            android.R.layout.simple_spinner_dropdown_item,
            physicianArray
        )
        // Connect the data source with AutoCompleteTextView through adapter.
        multiAutoCompleteTextViewPhysicianList.setAdapter(arrayAdapter)
    }

    private fun getPhysicianList() {
        val rctAes = RCTAes(ReactApplicationContext(mActivity!!))
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .physicianList(
                        PhysicianList(
                            rctAes.encryptString(syncHealthGetToken())
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgress()
                            var responseBody = result.string()
                            Log.d("Response Body", responseBody)
                            if (responseBody != "TH207") {
                                val gson = GsonBuilder().create()
                                val physicians =
                                    gson.fromJson(responseBody, Array<CommonsList>::class.java)
                                        .toList()
                                physiciansSortedList = listOf()
                                physiciansSortedList =
                                    physicians.sortedWith(
                                        compareBy(
                                            CommonsList::seq,
                                            CommonsList::seq
                                        )
                                    )
                                physicianArray = ArrayList()
                                for (physician in physiciansSortedList) {
                                    physicianArray.add(physician.title)
                                }
                                setPhysicianList(physicianArray)
                            } else {
                                clearCache()
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

    private fun getDoctorsList(selectedPhysician: String) {
        val rctAes = RCTAes(ReactApplicationContext(mActivity!!))
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .doctorsList(
                        ProviderList(
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString(selectedPhysician.filter { !it.isWhitespace() })
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgress()
                            var responseBody = result.string()
                            Log.d("Response Body", responseBody)
                            if (!responseBody.equals("TH207") && !responseBody.contains("TH102")) {
                                val gson = GsonBuilder().create()
                                val doctorList: Type =
                                    object : TypeToken<ArrayList<DoctorsList?>?>() {}.getType()
                                physicians = ArrayList()
                                physicians =
                                    gson.fromJson(responseBody, doctorList);
                                updatePhysiciansList(physicians)
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

    private fun updatePhysiciansList(physicians: ArrayList<DoctorsList>) {
        views.recycler_view_doctor_list.apply {
            layoutManager = LinearLayoutManager(
                mActivity!!,
                RecyclerView.VERTICAL,
                false
            )
            adapter = DoctorsListAdapter(
                mActivity!!,
                physicians, this@SyncHealthConsult
            )
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SyncHealthConsult.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SyncHealthConsult().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_sync_health_consult"
    }

    override fun OnDoctorItemClickListener(doctorList: DoctorsList) {
        Utils.providerId = doctorList.id
        var physician = multiAutoCompleteTextViewPhysicianList.getText().toString().trim()
        var physicianList = physician.substring(0, physician.length - 1)
        Utils.apptProviderType = physicianList
        Utils.providerType = doctorList.physician_type
    }
}