package com.app.synchealth.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.app.synchealth.R
import com.app.synchealth.databinding.FragmentSignUpBinding
import com.app.synchealth.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var selectedGender: String? = null
    var selectedMartialStatus: String? = null
    var genderData: Array<String>? = null
    var martialStatusData: Array<String>? = null
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sign_up
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSubTitle().text = "SIGN UP"
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.VISIBLE
        genderData = resources.getStringArray(R.array.gender)
        martialStatusData = resources.getStringArray(R.array.martial_status)

        genderSpinner(view)
        martialStatusSpinner(view)

        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { views, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat)
                binding.txtSignupDob.text = sdf.format(cal.time)
            }

        binding.imgSignupDobCalender.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                mActivity!!, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        binding.txtSignupDob.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                mActivity!!, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        binding.btnSignUpNext.setOnClickListener {
            val msg = "Please fill all the details"
            if (getText(binding.etSignUpFname).isNotEmpty()) {
                if (getText(binding.etSignUpLname).isNotEmpty()) {
                    if (binding.txtSignupDob.text.isNotEmpty()) {
                        if (getText(binding.etSignUpSsn).isNotEmpty()) {
                            if (getText(binding.etSignUpOccupation).isNotEmpty()) {
                                if (getText(binding.etSignUpMotherName).isNotEmpty()) {
                                    if (checkComponents()) {
                                        saveProfileData()
                                    } else {
                                        displayToast(msg)
                                    }
                                } else {
                                    displayToast(msg)
                                }
                            } else {
                                displayToast(msg)
                            }
                        } else {
                            displayToast(msg)
                        }
                    } else {
                        displayToast(msg)
                    }
                } else {
                    displayToast(msg)
                }
            } else {
                displayToast(msg)
            }
        }
    }

    private fun checkComponents(): Boolean {
        var isEmpty = false
        if (getText(binding.etSignUpCellPhone).isNotEmpty()) {
            if (getText(binding.etSignUpHomePhone).isNotEmpty()) {
                if (getText(binding.etSignUpEmail).isNotEmpty()) {
                    if (getText(binding.etSignUpStreet).isNotEmpty()) {
                        if (getText(binding.etSignUpCity).isNotEmpty()) {
                            if (getText(binding.etSignUpState).isNotEmpty()) {
                                if (getText(binding.etSignUpZipCode).isNotEmpty()) {
                                    if (getText(binding.etSignUpCountry).isNotEmpty()) {
                                        if (getText(binding.etSignUpEcName).isNotEmpty()) {
                                            if (getText(binding.etSignUpEcPhoneNumber).isNotEmpty()) {
                                                isEmpty = true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return isEmpty
    }

    private fun saveProfileData() {
        Utils.firstName = getText(binding.etSignUpFname)
        Utils.lastName = getText(binding.etSignUpLname)
        Utils.gender = selectedGender!!
        Utils.dob = binding.txtSignupDob.text.toString()
        Utils.martialStatus = selectedMartialStatus!!
        Utils.ssn = getText(binding.etSignUpSsn)
        Utils.occupation = getText(binding.etSignUpOccupation)
        Utils.motherName = getText(binding.etSignUpMotherName)
        Utils.cellPhone = getText(binding.etSignUpCellPhone)
        Utils.homePhone = getText(binding.etSignUpHomePhone)
        Utils.email = getText(binding.etSignUpEmail)
        Utils.street = getText(binding.etSignUpStreet)
        Utils.city = getText(binding.etSignUpCity)
        Utils.state = getText(binding.etSignUpState)
        Utils.zipcode = getText(binding.etSignUpZipCode)
        Utils.country = getText(binding.etSignUpCountry)
        Utils.ecName = getText(binding.etSignUpEcName)
        Utils.ecPhoneNo = getText(binding.etSignUpEcPhoneNumber)

        replaceFragment(
            SignUpInsurance(),
            R.id.layout_home,
            SignUpInsurance.TAG
        )
    }

    private fun genderSpinner(view: View) {
        val adapter = ArrayAdapter(
            mActivity!!,
            android.R.layout.simple_list_item_1, genderData!!
        )
        binding.spinnerSignupGender.adapter = adapter

        binding.spinnerSignupGender.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                v: View?, position: Int, id: Long
            ) {
                selectedGender = genderData!![position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun martialStatusSpinner(view: View) {
        val martialStatusData = resources.getStringArray(R.array.martial_status)
        val adapter = ArrayAdapter(
            mActivity!!,
            android.R.layout.simple_list_item_1, martialStatusData
        )
        binding.spinnerSignupMartialStatus.adapter = adapter

        binding.spinnerSignupMartialStatus.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                v: View?, position: Int, id: Long
            ) {
                selectedMartialStatus = martialStatusData[position].toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_SignUp_Profile"
    }
}