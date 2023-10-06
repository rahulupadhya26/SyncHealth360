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
import com.app.synchealth.R
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.*
import com.app.synchealth.databinding.FragmentAuthCodeBinding
import com.app.synchealth.databinding.FragmentSyncHealthProfileBinding
import com.app.synchealth.utils.Utils
import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
    private lateinit var binding: FragmentSyncHealthProfileBinding

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSyncHealthProfileBinding.inflate(inflater, container, false)
        return binding.root
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

        binding.imgEdit.setOnClickListener {
            enableProfileDetails()
        }

        binding.btnProfileSaveBtn.setOnClickListener {
            disableProfileDetails()
            updateProfileDetails()
        }

        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { views, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat)
                binding.txtProfileDob.text = sdf.format(cal.time)
            }

        binding.txtProfileDob.setOnClickListener {
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
        binding.editTextFname.isEnabled = false
        binding.editTextLname.isEnabled = false
        binding.spinnerGender.isEnabled = false
        binding.spinnerGender.isClickable = false
        binding.txtProfileDob.isEnabled = false
        binding.txtProfileDob.isClickable = false
        binding.spinnerMartialStatus.isEnabled = false
        binding.spinnerMartialStatus.isClickable = false
        binding.editTextSsn.isEnabled = false
        binding.editMotherName.isEnabled = false
        binding.editOccupation.isEnabled = false
        binding.editEmergencyContact.isEnabled = false
        binding.editMobilePhone.isEnabled = false
        binding.editContactEmail.isEnabled = false
        binding.editEcPhoneNumber.isEnabled = false
        binding.editEcName.isEnabled = false
        binding.editProfileStreet.isEnabled = false
        binding.editProfileCity.isEnabled = false
        binding.editProfileState.isEnabled = false
        binding.editProfileZipCode.isEnabled = false
        binding.editProfileCountry.isEnabled = false
        binding.editInsuranceProvider.isEnabled = false
        binding.editInsurancePlanName.isEnabled = false
        binding.editInsuranceSubscriber.isEnabled = false
        binding.editInsurancePolicyNo.isEnabled = false
        binding.editInsuranceGroupNo.isEnabled = false
        binding.btnProfileSaveBtn.visibility = View.GONE
        binding.imgEdit.visibility = View.VISIBLE
        binding.imgProfileDobCalender.visibility = View.GONE
    }

    private fun enableProfileDetails() {
        binding.editTextFname.isEnabled = true
        binding.editTextLname.isEnabled = true
        binding.spinnerGender.isEnabled = true
        binding.spinnerGender.isClickable = true
        binding.txtProfileDob.isEnabled = true
        binding.txtProfileDob.isClickable = true
        binding.spinnerMartialStatus.isEnabled = true
        binding.spinnerMartialStatus.isClickable = true
        binding.editTextSsn.isEnabled = true
        binding.editMotherName.isEnabled = true
        binding.editOccupation.isEnabled = true
        binding.editEmergencyContact.isEnabled = true
        binding.editMobilePhone.isEnabled = true
        binding.editContactEmail.isEnabled = true
        binding.editEcPhoneNumber.isEnabled = true
        binding.editEcName.isEnabled = true
        binding.editProfileStreet.isEnabled = true
        binding.editProfileCity.isEnabled = true
        binding.editProfileState.isEnabled = true
        binding.editProfileZipCode.isEnabled = true
        binding.editProfileCountry.isEnabled = true
        binding.editInsuranceProvider.isEnabled = true
        binding.editInsurancePlanName.isEnabled = true
        binding.editInsuranceSubscriber.isEnabled = true
        binding.editInsurancePolicyNo.isEnabled = true
        binding.editInsuranceGroupNo.isEnabled = true
        binding.btnProfileSaveBtn.visibility = View.VISIBLE
        binding.imgEdit.visibility = View.GONE
        binding.imgProfileDobCalender.visibility = View.VISIBLE
    }

    private fun updateProfileData(profileData: ProfileDetails) {
        binding.editTextFname.setText(profileData.fname)
        binding.editTextLname.setText(profileData.lname)
        binding.spinnerGender.setSelection(genderData!!.indexOf(profileData.sex))
        binding.txtProfileDob.text = profileData.DOB
        binding.spinnerMartialStatus.setSelection(martialStatusData!!.indexOf(profileData.status))
        binding.editTextSsn.setText(profileData.ss)
        binding.editMotherName.setText(profileData.mothersname)
        binding.editOccupation.setText(profileData.occupation)
        binding.editEmergencyContact.setText(profileData.phone_contact)
        binding.editMobilePhone.setText(profileData.phone_cell)
        binding.editContactEmail.setText(profileData.email)
        binding.editEcPhoneNumber.setText(profileData.EC_Phone_number)
        binding.editEcName.setText(profileData.ec_Name)
        binding.editProfileStreet.setText(profileData.street)
        binding.editProfileCity.setText(profileData.city)
        binding.editProfileState.setText(profileData.state)
        binding.editProfileZipCode.setText(profileData.postal_code)
        binding.editProfileCountry.setText(profileData.country_code)
    }

    private fun updateInsuranceData(insuranceData: InsuranceDetails) {
        binding.editInsuranceProvider.setText(insuranceData.provider)
        binding.editInsurancePlanName.setText(insuranceData.plan_name)
        binding.editInsuranceSubscriber.setText(insuranceData.subscriber_employer)
        binding.editInsurancePolicyNo.setText(insuranceData.policy_number)
        binding.editInsuranceGroupNo.setText(insuranceData.group_number)
    }

    private fun genderSpinner() {
        val adapter = ArrayAdapter(
            mActivity!!,
            android.R.layout.simple_list_item_1, genderData!!
        )
        binding.spinnerGender.adapter = adapter

        binding.spinnerGender.onItemSelectedListener = object :
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
        binding.spinnerMartialStatus.adapter = adapter

        binding.spinnerMartialStatus.onItemSelectedListener = object :
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
        val rctAes = RCTAes()
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
        val rctAes = RCTAes()
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
        val rctAes = RCTAes()
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .updateProfileData(
                        UpdateProfileData(
                            rctAes.encryptString(syncHealthGetPatientId()),
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString(binding.editTextFname.text.toString()),
                            rctAes.encryptString(binding.editTextLname.text.toString()),
                            rctAes.encryptString(selectedGender),
                            rctAes.encryptString(binding.txtProfileDob.text.toString()),
                            rctAes.encryptString(selectedMartialStatus),
                            rctAes.encryptString(binding.editTextSsn.text.toString()),
                            rctAes.encryptString(binding.editMotherName.text.toString()),
                            rctAes.encryptString(binding.editOccupation.text.toString()),
                            rctAes.encryptString(binding.editEmergencyContact.text.toString()),
                            rctAes.encryptString(binding.editMobilePhone.text.toString()),
                            rctAes.encryptString(binding.editContactEmail.text.toString()),
                            rctAes.encryptString(binding.editProfileStreet.text.toString()),
                            rctAes.encryptString(binding.editProfileCity.text.toString()),
                            rctAes.encryptString(binding.editProfileState.text.toString()),
                            rctAes.encryptString(binding.editProfileZipCode.text.toString()),
                            rctAes.encryptString(binding.editProfileCountry.text.toString()),
                            rctAes.encryptString(binding.editInsuranceProvider.text.toString()),
                            rctAes.encryptString(binding.editInsurancePlanName.text.toString()),
                            rctAes.encryptString(binding.editInsuranceSubscriber.text.toString()),
                            rctAes.encryptString(binding.editInsurancePolicyNo.text.toString()),
                            rctAes.encryptString(binding.editInsuranceGroupNo.text.toString()),
                            rctAes.encryptString(binding.editEcName.text.toString()),
                            rctAes.encryptString(binding.editEcPhoneNumber.text.toString()),
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