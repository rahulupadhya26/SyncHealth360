package com.app.synchealth.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.synchealth.R
import com.app.synchealth.utils.Utils
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.*
import com.app.synchealth.databinding.FragmentLoginBinding
import com.google.android.datatransport.backend.cct.BuildConfig
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var profileDetails: ArrayList<ProfileDetails>? = null
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
        numUpdates = 1
    }
    var locationManager: LocationManager? = null
    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_login
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSubTitle().text = ""
        getBackButton().visibility = View.GONE
        getSubTitle().visibility = View.GONE

        checkLocationPermission()

        binding.cardViewPatientLogin.setOnClickListener {
            binding.cardViewDoctorLogin.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorLightGrey
                )
            )
            binding.cardViewPatientLogin.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorWhite
                )
            )
            binding.layoutDoctorSignIn.visibility = View.GONE
            binding.layoutPatientSignIn.visibility = View.VISIBLE
        }

        binding.cardViewDoctorLogin.setOnClickListener {
            binding.cardViewDoctorLogin.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorWhite
                )
            )
            binding.cardViewPatientLogin.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.colorLightGrey
                )
            )
            binding.layoutPatientSignIn.visibility = View.GONE
            binding.layoutDoctorSignIn.visibility = View.VISIBLE
        }

        binding.imgInfo.setOnClickListener {
            replaceFragment(
                AboutScreenFragment(),
                R.id.layout_home,
                AboutScreenFragment.TAG
            )
        }

        binding.btnPatientLogin.setOnClickListener {
            if (isValidText(binding.editTxtAuthCode)) {
                signInUsingAuthCode()
            } else {
                displayToast("Please enter proper Auth Code")
            }
        }

        binding.btnSignIn.setOnClickListener {
            replaceFragment(
                LoginUsingCredentialsFragment(),
                R.id.layout_home,
                LoginUsingCredentialsFragment.TAG
            )
        }

        binding.btnSignUp.setOnClickListener {
            replaceFragment(
                SignUpTcCodeFragment(),
                R.id.layout_home,
                SignUpTcCodeFragment.TAG
            )
        }

        binding.txtDoctorSignUp.setOnClickListener {

        }

        binding.btnDocSignIn.setOnClickListener {
            if (getText(binding.editTxtDocUsername).isNotEmpty()) {
                if (getText(binding.editTxtDocPass).isNotEmpty()) {
                    doctorSignIn()
                } else {
                    displayToast("Password cannot be blank.")
                }
            } else {
                displayToast("Username cannot be blank.")
            }
        }

        //Check if service is started or not
        /*if (!Utils.isServiceRunning(mActivity!!, SyncHealthService::class.java)) {
            Utils.startBackGroundService(mActivity!!)
        }*/
    }

    private fun signInUsingAuthCode() {
        showProgress()
        //val configUrl: String = JSONObject(syncHealthGetConfigResponse()).getString("url")
        var ipAddress: String = ""
        if (Utils.getIPAddress(true)!!.isNotEmpty()) {
            ipAddress = Utils.getIPAddress(true)!!
        } else {
            ipAddress = Utils.getIPAddress(false)!!
        }
        val rctAes = RCTAes()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .signInUsingAuthCode(
                        SignInUsingAuthCode(
                            rctAes.encryptString(getText(binding.editTxtAuthCode)),
                            rctAes.encryptString(ipAddress),
                            rctAes.encryptString(Build.VERSION.SDK_INT.toString()),
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
                                    updateSyncHealthPatientId(response[1]) //Patient Id
                                    saveUserName(response[2])
                                    updateSyncHealthToken(response[3])   //Token
                                    getProfileDetails()
                                } else {
                                    hideProgress()
                                    displayToast("Incorrect Username/Password.")
                                    popBackStack()
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

    private fun doctorSignIn() {
        showProgress()
        //val configUrl: String = JSONObject(syncHealthGetConfigResponse()).getString("url")
        val rctAes = RCTAes()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .doctorSignIn(
                        DoctorSignIn(
                            rctAes.encryptString(getText(binding.editTxtDocUsername)),
                            rctAes.encryptString(getText(binding.editTxtDocPass))
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            val resp = result.string()
                            if (!resp.contains("101")) {
                                val responseBody = JSONObject(resp)
                                if (responseBody.get("status") == "200") {
                                    updateSyncHealthToken(responseBody.get("token").toString())   //Token
                                    updateProviderId(responseBody.get("provider_id").toString()) //Provider Id
                                    getDoctorDetails()
                                } else {
                                    hideProgress()
                                    displayToast("Incorrect Username/Password.")
                                    popBackStack()
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

    private fun getDoctorDetails() {
        val rctAes = RCTAes()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .providerDetails(
                        ProviderDetails(
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString(getProviderId()),
                            "NrySk6ThVfy4GEc5pLE9KQ=="
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
                                val providerDetails = gson.fromJson(responseBody, DoctorProfileDetails::class.java)
                                val json = gson.toJson(providerDetails)
                                saveProviderDetails(json)
                                updateProfileInfo(providerDetails!!.fname + " " + providerDetails.lname)
                                replaceFragmentNoBackStack(
                                    DoctorDashboard(),
                                    R.id.layout_home,
                                    DoctorDashboard.TAG
                                )
                            } else if (!responseBody.contains("TH100")) {
                                displayToast("Invalid username and password")
                            } else {
                                displayToast("User info not present...")
                            }
                        } catch (e: Exception) {
                            hideProgress()
                            displayToast("Something went wrong.. Please try after sometime")
                            e.printStackTrace()
                        }
                    }, { error ->
                        hideProgress()
                        displayToast("Error ${error.localizedMessage}")
                    })
            )
        }
        handler!!.postDelayed(runnable!!, 1000)
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                getLocationUpdate(location)

            }
        }
    }

    fun getLocationUpdate(location: Location) {
        Utils.latitude = location.latitude
        Utils.longitude = location.longitude
        fusedLocationProvider!!.removeLocationUpdates(locationCallback)
    }

    private fun checkLocationPermission() {
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(mActivity!!)
        locationManager = mActivity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                mActivity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(mActivity!!)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        } else {
            if (!isGPSEnabled()) {
                enableGPS()
            } else {
                fusedLocationProvider?.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                mActivity!!,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                mActivity!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    fun isGPSEnabled(): Boolean {
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            mActivity!!,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        if (isGPSEnabled()) {
                            enableGPS()
                        } else {
                            fusedLocationProvider?.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.getMainLooper()
                            )
                        }
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    AlertDialog.Builder(mActivity!!)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton(
                            "OK"
                        ) { _, _ ->
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                requireActivity().packageName, null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .create()
                        .show()
                }
                return
            }
        }
    }

    fun enableGPS() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(mActivity!!)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(
            builder.build()
        )
        task.addOnSuccessListener {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            if (ContextCompat.checkSelfPermission(
                    mActivity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationProvider?.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    startIntentSenderForResult(
                        exception.resolution.intentSender,
                        REQUEST_CHECK_SETTINGS,
                        null,
                        0,
                        0,
                        0,
                        null
                    );
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    /**
     * Handles the callback from the OAuth sign in flow, executing the post sign in function
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    checkLocationPermission()
                }
                else -> enableGPS()
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
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_login"
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val REQUEST_CHECK_SETTINGS = 15
    }
}
