package com.app.synchealth.fragments

import android.os.Bundle
import android.view.View
import com.app.synchealth.R
import com.app.synchealth.data.UserData
import com.app.synchealth.controller.OnOtpCompletionListener
import kotlinx.android.synthetic.main.fragment_validate_o_t_p.view.*

private const val ARG_PARAM1 = "param1"

class ValidateOTPFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var userData: UserData? = null
    private var otpString:String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userData = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_validate_o_t_p
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.otp_view.setOtpCompletionListener(
            object : OnOtpCompletionListener {
                override fun onOtpCompleted(otp: String?) {
                    otpString = otp
                }

            }
        )

        view.txt_email.text = userData!!.email

        view.btn_enter_otp.setOnClickListener {
            view.layout_recovery_success.visibility = View.GONE
            view.layout_validate_pass_code.visibility = View.VISIBLE
        }

        view.btn_validate.setOnClickListener {
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

        view.txt_resend.setOnClickListener {
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