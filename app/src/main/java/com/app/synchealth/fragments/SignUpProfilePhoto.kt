package com.app.synchealth.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import com.app.synchealth.R
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.Register
import com.app.synchealth.databinding.FragmentAuthCodeBinding
import com.app.synchealth.databinding.FragmentSignUpProfilePhotoBinding
import com.app.synchealth.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpProfilePhoto.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpProfilePhoto : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentSignUpProfilePhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sign_up_profile_photo
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpProfilePhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSubTitle().text = "LOGIN DETAILS"
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.VISIBLE

        binding.imgProfilePhoto.setOnClickListener {
            captureImage(binding.imgProfilePhoto)
        }

        binding.tvAddProfilePic.setOnClickListener {
            captureImage(binding.imgProfilePhoto)
        }

        binding.btnSignUpSubmit.setOnClickListener {
            if (getText(binding.etSignUpPassword).isNotEmpty()) {
                if (getText(binding.etSignUpConfirmPassword).isNotEmpty()) {
                    if (getText(binding.etSignUpPassword) == getText(binding.etSignUpConfirmPassword)) {
                        sendSignUpDetails()
                    }
                }
            }
        }
    }

    private fun sendSignUpDetails() {
        val rctAes = RCTAes()
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .register(
                        Register(
                            rctAes.encryptString(Utils.firstName),
                            rctAes.encryptString(Utils.lastName),
                            rctAes.encryptString(Utils.gender),
                            rctAes.encryptString(Utils.dob),
                            rctAes.encryptString(Utils.martialStatus),
                            rctAes.encryptString(Utils.ssn),
                            rctAes.encryptString(Utils.motherName),
                            rctAes.encryptString(Utils.occupation),
                            rctAes.encryptString(Utils.cellPhone),
                            rctAes.encryptString(Utils.homePhone),
                            rctAes.encryptString(Utils.email),
                            rctAes.encryptString(Utils.street),
                            rctAes.encryptString(Utils.city),
                            rctAes.encryptString(Utils.state),
                            rctAes.encryptString(Utils.zipcode),
                            rctAes.encryptString(Utils.country),
                            rctAes.encryptString(Utils.insuranceName),
                            rctAes.encryptString(Utils.planName),
                            rctAes.encryptString(Utils.subscriber),
                            rctAes.encryptString(Utils.policyNo),
                            rctAes.encryptString(Utils.groupNo),
                            rctAes.encryptString(getText(binding.etSignUpUsername)),
                            rctAes.encryptString(getText(binding.etSignUpPassword)),
                            rctAes.encryptString(Utils.tccode),
                            rctAes.encryptString(Utils.ecName),
                            rctAes.encryptString(Utils.ecPhoneNo),
                            "1gO0iNmHRlfw1B8sphZkgqRJXNBZhyF4vtkxJSVd1T4="
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgress()
                            val responseBody = result.string()
                            Log.d("Response Body", responseBody)
                            if (!responseBody.contains("TH500")) {
                                if (!responseBody.contains("TH600")) {
                                    val respStr = rctAes.decryptString(
                                        responseBody.substring(
                                            responseBody.lastIndexOf(" ") + 1
                                        )
                                    )
                                    val respStrList = respStr.split(":")
                                    val respCode = respStrList[0]
                                    if (respCode == "200") {
                                        updateSyncHealthPatientId(respStrList[1])
                                        updateSyncHealthToken(respStrList[2])
                                        Utils.authCode = respStrList[3]
                                        displayToast("Message has been sent...")
                                        replaceFragment(
                                            AuthCodeFragment(),
                                            R.id.layout_home,
                                            AuthCodeFragment.TAG
                                        )
                                    } else {
                                        displayToast("Something went wrong.. Please try after sometime")
                                    }
                                } else {
                                    displayToast("Email Id already exist.")
                                }
                            } else {
                                displayToast("Username already exist.")
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
         * @return A new instance of fragment SignUpProfilePhoto.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpProfilePhoto().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_SignUp_Profile_photo"
    }
}