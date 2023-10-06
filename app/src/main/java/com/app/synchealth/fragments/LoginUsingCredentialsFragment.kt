package com.app.synchealth.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.app.synchealth.BuildConfig
import com.app.synchealth.R
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.ProfileData
import com.app.synchealth.data.ProfileDetails
import com.app.synchealth.data.SyncHealthLogin
import com.app.synchealth.databinding.FragmentAuthCodeBinding
import com.app.synchealth.databinding.FragmentLoginUsingCredentialsBinding
import com.app.synchealth.utils.Utils
import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.reflect.Type

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginUsingCredentialsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginUsingCredentialsFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var profileDetails: ArrayList<ProfileDetails>? = null
    private lateinit var binding: FragmentLoginUsingCredentialsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_login_using_credentials
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginUsingCredentialsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSubTitle().text = ""
        getBackButton().visibility = View.GONE
        getSubTitle().visibility = View.GONE

        binding.editTextUsername.setText(getUserName())



        binding.btnContinue.setOnClickListener {
            if (isValidText(binding.editTextUsername)) {
                if (isValidText(binding.editTextPassword)) {
                    hideKeyboard(binding.btnContinue)
                    showProgress()
                    signInUsingAuthCode()
                } else {
                    displayToast("Please enter password")
                }
            } else {
                displayToast("Please enter valid email")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun signInUsingAuthCode() {
        //val configUrl: String = JSONObject(syncHealthGetConfigResponse()).getString("url")
        var ipAddress: String = ""
        if (Utils.getIPAddress(true)!!.isNotEmpty()) {
            ipAddress = Utils.getIPAddress(true)!!
        } else {
            ipAddress = Utils.getIPAddress(false)!!
        }
        if (binding.cbRememberUserName.isChecked) {
            saveUserName(getText(binding.editTextUsername))
        }
        val rctAes = RCTAes()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .syncHealthLogin(
                        SyncHealthLogin(
                            rctAes.encryptString(getText(binding.editTextUsername)),
                            rctAes.encryptString(getText(binding.editTextPassword)),
                            rctAes.encryptString(ipAddress),
                            rctAes.encryptString(Build.VERSION.BASE_OS),
                            rctAes.encryptString(Build.DEVICE),
                            rctAes.encryptString(Utils.latitude.toBigDecimal().toPlainString()),
                            rctAes.encryptString(Utils.longitude.toBigDecimal().toPlainString()),
                            rctAes.encryptString(BuildConfig.VERSION_NAME)
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            val resp = result.string()
                            if (!resp.contains("101")) {
                                val responseBody = rctAes.decryptString(resp)
                                Log.d("Response Body", responseBody)
                                val response = responseBody.split(":").toTypedArray()
                                val status =
                                    Integer.parseInt(if (response[0].isEmpty()) "0" else response[0])
                                if (status == 200) {
                                    updateSyncHealthPatientId(response[1])   //Patient Id
                                    updateSyncHealthToken(response[2])   //Token
                                    getProfileDetails()
                                } else {
                                    hideProgress()
                                    displayToast("Incorrect Username/Password.")
                                }
                            } else {
                                hideProgress()
                                displayToast("Something went wrong.. Please try after sometime.. ERR-101")
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
        handler.postDelayed(runnable!!, 1000)
    }

    private fun getProfileDetails() {
        val rctAes = RCTAes()
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
                            val responseBody = result.string()
                            Log.d("Response Body", responseBody)
                            if (!responseBody.contains("TH102")) {
                                val gson = GsonBuilder().create()
                                profileDetails = ArrayList()
                                val profileData: Type =
                                    object : TypeToken<ArrayList<ProfileDetails?>?>() {}.type
                                profileDetails = gson.fromJson(responseBody, profileData)
                                updateUserInfo(profileDetails!![0], "")
                                updateProfileInfo(profileDetails!![0].fname + " " + profileDetails!![0].lname)
                                replaceFragmentNoBackStack(
                                    SyncHealthDashboard(),
                                    R.id.layout_home,
                                    SyncHealthDashboard.TAG
                                )
                            } else if (!responseBody.contains("TH100")) {
                                displayToast("Invalid username and password")
                            } else {
                                displayToast("User info not present...")
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
         * @return A new instance of fragment LoginUsingCredentialsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginUsingCredentialsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_login_using_credentials"
    }
}