package com.app.synchealth.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.app.synchealth.R
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.*
import com.app.synchealth.utils.Utils
import com.facebook.react.bridge.ReactApplicationContext
import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_sync_health_consent_letter.view.*
import kotlinx.android.synthetic.main.fragment_sync_health_dashboard.*
import kotlinx.android.synthetic.main.fragment_sync_health_profile.*
import java.lang.Exception
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SyncHealthProfile.newInstance] factory method to
 * create an instance of this fragment.
 */
class SyncHealthProfile : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var profileDetails: ArrayList<ProfileDetails>? = null
    var insuranceDetails: ArrayList<InsuranceDetails>? = null
    var selectedGender: String? = null
    var selectedMartialStatus: String? = null
    var genderData: Array<String>? = null
    var martialStatusData: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sync_health_profile
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHeader().visibility = View.GONE
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.VISIBLE
        getSubTitle().text = getString(R.string.txt_nav_menu_sync_health_profile)
        genderData = resources.getStringArray(R.array.gender)
        martialStatusData = resources.getStringArray(R.array.martial_status)
        disableProfileDetails()
        genderSpinner()
        martialStatusSpinner()

        img_edit.setOnClickListener {
            enableProfileDetails()
        }

        btn_profile_save_btn.setOnClickListener {
            disableProfileDetails()
            updateProfileDetails()
        }

        var cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { views, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat)
                txt_profile_dob.text = sdf.format(cal.time)
            }

        txt_profile_dob.setOnClickListener {
            var datePickerDialog = DatePickerDialog(
                mActivity!!, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        getProfileDetails()
    }

    private fun disableProfileDetails() {
        edit_text_fname.isEnabled = false
        edit_text_lname.isEnabled = false
        spinner_gender.isEnabled = false
        spinner_gender.isClickable = false
        txt_profile_dob.isEnabled = false
        txt_profile_dob.isClickable = false
        spinner_martial_status.isEnabled = false
        spinner_martial_status.isClickable = false
        edit_text_ssn.isEnabled = false
        edit_mother_name.isEnabled = false
        edit_occupation.isEnabled = false
        edit_emergency_contact.isEnabled = false
        edit_mobile_phone.isEnabled = false
        edit_contact_email.isEnabled = false
        edit_ec_phone_number.isEnabled = false
        edit_ec_name.isEnabled = false
        edit_profile_street.isEnabled = false
        edit_profile_city.isEnabled = false
        edit_profile_state.isEnabled = false
        edit_profile_zip_code.isEnabled = false
        edit_profile_country.isEnabled = false
        edit_insurance_provider.isEnabled = false
        edit_insurance_plan_name.isEnabled = false
        edit_insurance_subscriber.isEnabled = false
        edit_insurance_policy_no.isEnabled = false
        edit_insurance_group_no.isEnabled = false
        btn_profile_save_btn.visibility = View.GONE
        img_edit.visibility = View.VISIBLE
        img_profile_dob_calender.visibility = View.GONE
    }

    private fun enableProfileDetails() {
        edit_text_fname.isEnabled = true
        edit_text_lname.isEnabled = true
        spinner_gender.isEnabled = true
        spinner_gender.isClickable = true
        txt_profile_dob.isEnabled = true
        txt_profile_dob.isClickable = true
        spinner_martial_status.isEnabled = true
        spinner_martial_status.isClickable = true
        edit_text_ssn.isEnabled = true
        edit_mother_name.isEnabled = true
        edit_occupation.isEnabled = true
        edit_emergency_contact.isEnabled = true
        edit_mobile_phone.isEnabled = true
        edit_contact_email.isEnabled = true
        edit_ec_phone_number.isEnabled = true
        edit_ec_name.isEnabled = true
        edit_profile_street.isEnabled = true
        edit_profile_city.isEnabled = true
        edit_profile_state.isEnabled = true
        edit_profile_zip_code.isEnabled = true
        edit_profile_country.isEnabled = true
        edit_insurance_provider.isEnabled = true
        edit_insurance_plan_name.isEnabled = true
        edit_insurance_subscriber.isEnabled = true
        edit_insurance_policy_no.isEnabled = true
        edit_insurance_group_no.isEnabled = true
        btn_profile_save_btn.visibility = View.VISIBLE
        img_edit.visibility = View.GONE
        img_profile_dob_calender.visibility = View.VISIBLE
    }

    private fun updateProfileData(profileData: ProfileDetails) {
        //val rctAes = RCTAes(ReactApplicationContext(mActivity!!))
        edit_text_fname.setText(profileData.fname)
        edit_text_lname.setText(profileData.lname)
        spinner_gender.setSelection(genderData!!.indexOf(profileData.sex))
        txt_profile_dob.text = profileData.DOB
        spinner_martial_status.setSelection(martialStatusData!!.indexOf(profileData.status))
        edit_text_ssn.setText(profileData.ss)
        edit_mother_name.setText(profileData.mothersname)
        edit_occupation.setText(profileData.occupation)
        edit_emergency_contact.setText(profileData.phone_contact)
        edit_mobile_phone.setText(profileData.phone_cell)
        edit_contact_email.setText(profileData.email)
        edit_ec_phone_number.setText(profileData.EC_Phone_number)
        edit_ec_name.setText(profileData.ec_Name)
        edit_profile_street.setText(profileData.street)
        edit_profile_city.setText(profileData.city)
        edit_profile_state.setText(profileData.state)
        edit_profile_zip_code.setText(profileData.postal_code)
        edit_profile_country.setText(profileData.country_code)
    }

    private fun updateInsuranceData(insuranceData: InsuranceDetails) {
        edit_insurance_provider.setText(insuranceData.provider)
        edit_insurance_plan_name.setText(insuranceData.plan_name)
        edit_insurance_subscriber.setText(insuranceData.subscriber_employer)
        edit_insurance_policy_no.setText(insuranceData.policy_number)
        edit_insurance_group_no.setText(insuranceData.group_number)
    }

    private fun genderSpinner() {
        val adapter = ArrayAdapter(
            mActivity!!,
            android.R.layout.simple_list_item_1, genderData!!
        )
        spinner_gender.adapter = adapter

        spinner_gender.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                v: View, position: Int, id: Long
            ) {
                selectedGender = genderData!![position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun martialStatusSpinner() {
        val martialStatusData = resources.getStringArray(R.array.martial_status)
        val adapter = ArrayAdapter(
            mActivity!!,
            android.R.layout.simple_list_item_1, martialStatusData
        )
        spinner_martial_status.adapter = adapter

        spinner_martial_status.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                v: View, position: Int, id: Long
            ) {
                selectedMartialStatus = martialStatusData.get(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun getProfileDetails() {
        val rctAes = RCTAes(ReactApplicationContext(mActivity!!))
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .getProfileData(
                        ProfileData(
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString(syncHealthGetPatientId())
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgress()
                            var responseBody = result.string()
                            Log.d("Response Body", responseBody)
                            if (!responseBody.equals("TH102")) {
                                val gson = GsonBuilder().create()
                                profileDetails = ArrayList()
                                val profileData: Type =
                                    object : TypeToken<ArrayList<ProfileDetails?>?>() {}.getType()
                                profileDetails = gson.fromJson(responseBody, profileData)
                                updateProfileData(profileDetails!!.get(0))
                                getInsuranceDetails()
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

    private fun getInsuranceDetails() {
        val rctAes = RCTAes(ReactApplicationContext(mActivity!!))
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .getInsuranceData(
                        InsuranceData(
                            rctAes.encryptString(syncHealthGetPatientId()),
                            rctAes.encryptString(syncHealthGetToken()),
                            "uqiRSvkJqt48cJ1+c6TSoA=="
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgress()
                            var responseBody = result.string()
                            Log.d("Response Body", responseBody)
                            if (responseBody!=null && responseBody.isNotEmpty() && !responseBody.contains("TH102")) {
                                val gson = GsonBuilder().create()
                                insuranceDetails = ArrayList()
                                val profileData: Type =
                                    object : TypeToken<ArrayList<InsuranceDetails?>?>() {}.getType()
                                insuranceDetails = gson.fromJson(responseBody, profileData)
                                updateInsuranceData(insuranceDetails!![0])
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

    private fun updateProfileDetails() {
        val rctAes = RCTAes(ReactApplicationContext(mActivity!!))
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .updateProfileData(
                        UpdateProfileData(
                            rctAes.encryptString(syncHealthGetPatientId()),
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString(edit_text_fname.text.toString()),
                            rctAes.encryptString(edit_text_lname.text.toString()),
                            rctAes.encryptString(selectedGender),
                            rctAes.encryptString(txt_profile_dob.text.toString()),
                            rctAes.encryptString(selectedMartialStatus),
                            rctAes.encryptString(edit_text_ssn.text.toString()),
                            rctAes.encryptString(edit_mother_name.text.toString()),
                            rctAes.encryptString(edit_occupation.text.toString()),
                            rctAes.encryptString(edit_emergency_contact.text.toString()),
                            rctAes.encryptString(edit_mobile_phone.text.toString()),
                            rctAes.encryptString(edit_contact_email.text.toString()),
                            rctAes.encryptString(edit_profile_street.text.toString()),
                            rctAes.encryptString(edit_profile_city.text.toString()),
                            rctAes.encryptString(edit_profile_state.text.toString()),
                            rctAes.encryptString(edit_profile_zip_code.text.toString()),
                            rctAes.encryptString(edit_profile_country.text.toString()),
                            rctAes.encryptString(edit_insurance_provider.text.toString()),
                            rctAes.encryptString(edit_insurance_plan_name.text.toString()),
                            rctAes.encryptString(edit_insurance_subscriber.text.toString()),
                            rctAes.encryptString(edit_insurance_policy_no.text.toString()),
                            rctAes.encryptString(edit_insurance_group_no.text.toString()),
                            rctAes.encryptString(edit_ec_name.text.toString()),
                            rctAes.encryptString(edit_ec_phone_number.text.toString()),
                            rctAes.encryptString(insuranceDetails!![0].subscriber_relationship)
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgress()
                            var responseBody = result.string()
                            Log.d("Response Body", responseBody)
                            if (!responseBody.equals("TH102")) {
                                getProfileDetails()
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
         * @return A new instance of fragment SyncHealthProfile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SyncHealthProfile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_sync_health_profile"
    }
}