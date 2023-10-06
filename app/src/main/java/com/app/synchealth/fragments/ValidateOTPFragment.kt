package com.app.synchealth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.synchealth.R
import com.app.synchealth.data.UserData
import com.app.synchealth.controller.OnOtpCompletionListener
import com.app.synchealth.databinding.FragmentValidateOTPBinding

private const val ARG_PARAM1 = "param1"

class ValidateOTPFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var userData: UserData? = null
    private var otpString:String? = ""
    private lateinit var binding: FragmentValidateOTPBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userData = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_validate_o_t_p
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentValidateOTPBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.otpView.setOtpCompletionListener(
            object : OnOtpCompletionListener {
                override fun onOtpCompleted(otp: String?) {
                    otpString = otp
                }

            }
        )

        binding.txtEmail.text = userData!!.email

        binding.btnEnterOtp.setOnClickListener {
            binding.layoutRecoverySuccess.visibility = View.GONE
            binding.layoutValidatePassCode.visibility = View.VISIBLE
        }

        binding.btnValidate.setOnClickListener {
            if(otpString!!.length == 4)
            {
                if (otpString == userData!!.otp)
                replaceFragment(ResetPasswordFragment.newInstance(userData!!),R.id.layout_home,ResetPasswordFragment.TAG)
                else
                    displayToast("Wrong pass code entered.")
            }else
            {
                displayToast("Please enter correct Pass code")
            }
        }

        binding.txtResend.setOnClickListener {
            popBackStack()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(userData: UserData) =
            ValidateOTPFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, userData)
                }
            }
        const val TAG = "Screen_validateOTP"
    }
}