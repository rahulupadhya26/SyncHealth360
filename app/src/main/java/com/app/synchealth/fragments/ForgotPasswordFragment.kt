package com.app.synchealth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.synchealth.R
import com.app.synchealth.data.LoginUser
import com.app.synchealth.databinding.FragmentAuthCodeBinding
import com.app.synchealth.databinding.FragmentForgotPasswordBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ForgotPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForgotPasswordFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_forgot_password
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.VISIBLE
        getSubTitle().text = resources.getText(R.string.txt_lbl_header_forgot_password)

        binding.btnReset.setOnClickListener {
            if(isValidEmail(binding.editEmail))
            {
                showWhiteProgress()
                mCompositeDisposable.add(
                    getEncryptedRequestInterface()
                        .forgotPassword(LoginUser(getText(binding.editEmail), ""))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                                result ->
                            hideProgress()
                            if (result.message.equals("Success",ignoreCase =true)) {
                                try {
                                replaceFragment(ValidateOTPFragment.newInstance(result.userData!!),R.id.layout_home,ValidateOTPFragment.TAG)
                                }catch (e:Exception){}
                            }else
                            {
                                displayToast("Please enter valid email.")
                            }
                        }, {
                                error ->
                            hideProgress()
                            displayToast("Error ${error.localizedMessage}")
                        }))
            }else
            {
                displayToast("Please enter valid email.")
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
         * @return A new instance of fragment ForgotPasswordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ForgotPasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        const val TAG = "Screen_Forgot_Password"
    }
}
