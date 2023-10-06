package com.app.synchealth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.graphics.drawable.toBitmap
import com.app.synchealth.R
import com.app.synchealth.databinding.FragmentAuthCodeBinding
import com.app.synchealth.databinding.FragmentSignUpBinding
import com.app.synchealth.databinding.FragmentSignUpInsuranceBinding
import com.app.synchealth.utils.Utils

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpInsurance.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpInsurance : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var selectedInsuranceName: String? = null
    var insuranceNameData: Array<String>? = null
    private lateinit var binding: FragmentSignUpInsuranceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sign_up_insurance
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpInsuranceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSubTitle().text = "INSURANCE DETAILS"
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.VISIBLE
        insuranceNameData = resources.getStringArray(R.array.insuranceProviderName)

        insuranceProviderNameSpinner()

        binding.layoutAttachCard.visibility = View.VISIBLE
        binding.imgAttachCard.visibility = View.GONE

        binding.cvAttachCard.setOnClickListener {
            captureImage(binding.imgAttachCard)
            binding.imgAttachCard.visibility = View.VISIBLE
            binding.layoutAttachCard.visibility = View.GONE
        }

        binding.btnSignUpNext.setOnClickListener {
            Utils.insuranceName = selectedInsuranceName!!
            Utils.planName = getText(binding.etSignUpInsurancePlanName)
            Utils.policyNo = getText(binding.etSignUpInsurancePolicyNumber)
            Utils.subscriber = getText(binding.etSignUpSubscriber)
            Utils.groupNo = getText(binding.etSignUpGroupNumber)
            if(binding.imgAttachCard.drawable != null) {
                val bitmap = binding.imgAttachCard.drawable.toBitmap()
                Utils.insurancePhoto = "data:image/jpg;base64," + Utils.convert(bitmap)
            }

            replaceFragment(
                SignUpProfilePhoto(),
                R.id.layout_home,
                SignUpProfilePhoto.TAG
            )
        }
    }

    private fun insuranceProviderNameSpinner() {
        val adapter = ArrayAdapter(
            mActivity!!,
            android.R.layout.simple_list_item_1, insuranceNameData!!
        )
        binding.spinnerSignUpInsuranceProvider.adapter = adapter

        binding.spinnerSignUpInsuranceProvider.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                v: View?, position: Int, id: Long
            ) {
                selectedInsuranceName = insuranceNameData!![position]
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
         * @return A new instance of fragment SignUpInsurance.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpInsurance().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        const val TAG = "Screen_SignUp_Insurance"
    }
}