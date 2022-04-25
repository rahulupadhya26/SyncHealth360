package com.app.synchealth.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.synchealth.R
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.CommonList
import com.app.synchealth.data.CommonsList
import com.app.synchealth.utils.Utils
import com.facebook.react.bridge.ReactApplicationContext
import com.google.android.datatransport.backend.cct.BuildConfig
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_sync_health_create_appoint_basic_info.*
import kotlinx.android.synthetic.main.fragment_sync_health_create_appoint_basic_info.view.*
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SyncHealthCreateAppointBasicInfo.newInstance] factory method to
 * create an instance of this fragment.
 */
class SyncHealthCreateAppointBasicInfo : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var quickBook: String? = null
    private lateinit var multiAutoCompleteTextView: MultiAutoCompleteTextView
    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        maxWaitTime = 60
        numUpdates = 1
    }
    var locationManager: LocationManager? = null
    lateinit var views: View
    var symptomsArray: ArrayList<String> = ArrayList()
    var communicationType: String? = null
    var symptomsSortedList: List<CommonsList> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            quickBook = it.getString(ARG_PARAM1)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sync_health_create_appoint_basic_info
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views = view
        getHeader().visibility = View.GONE
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.VISIBLE
        getSubTitle().text = getString(R.string.txt_nav_menu_sync_health_create_appoint_basic_info)
        //Bind and select symptoms from list
        selectSymptoms(view)
        //Get Symptoms List
        getSymptomsList()

        btn_locate_me.setOnClickListener {
            checkLocationPermission()
        }

        btn_basic_info_next_step.setOnClickListener {
            if (getText(multiAutoCompleteTextView).isNotEmpty() &&
                getText(views.edit_txt_street).isNotEmpty() &&
                getText(views.edit_txt_city).isNotEmpty() &&
                getText(views.edit_txt_state).isNotEmpty() &&
                getText(views.edit_txt_postal_code).isNotEmpty() &&
                getText(views.edit_txt_country).isNotEmpty() &&
                getText(views.edit_txt_phone_number).isNotEmpty() && communicationType != null
            ) {
                val symptoms = multiAutoCompleteTextView.text.toString()
                Utils.selectedSymptoms = symptoms.substring(0, symptoms.length - 2).trim()
                Utils.selectedStreet = getText(views.edit_txt_street).trim()
                Utils.selectedCity = getText(views.edit_txt_city).trim()
                Utils.selectedState = getText(views.edit_txt_state).trim()
                Utils.selectedPostalCode = getText(views.edit_txt_postal_code).trim()
                Utils.selectedCountry = getText(views.edit_txt_country).trim()
                Utils.selectedPhoneNo = getText(views.edit_txt_phone_number).trim()
                Utils.selectedCommunicationMode = communicationType as String

                if (quickBook.equals(Utils.QUICK_BOOK)) {
                    replaceFragment(
                        SyncHealthTimeSlot(),
                        R.id.layout_home,
                        SyncHealthTimeSlot.TAG
                    )
                } else {
                    replaceFragment(
                        SyncHealthConsult(),
                        R.id.layout_home,
                        SyncHealthConsult.TAG
                    )
                }
            } else {
                displayToast("Please fill all the details")
            }
        }

        txt_phone_call.setOnClickListener {
            txt_phone_call.setBackgroundResource(R.drawable.bg_box_blue)
            txt_phone_call.setTextColor(mActivity!!.getColor(R.color.colorWhite))
            txt_video_call.setBackgroundResource(R.drawable.bg_box_grey)
            txt_office_visit.setBackgroundResource(R.drawable.bg_box_grey)
            txt_video_call.setTextColor(mActivity!!.getColor(R.color.colorPrimary))
            txt_office_visit.setTextColor(mActivity!!.getColor(R.color.colorPrimary))
            communicationType = "Phone call"
        }

        txt_video_call.setOnClickListener {
            txt_video_call.setBackgroundResource(R.drawable.bg_box_blue)
            txt_video_call.setTextColor(mActivity!!.getColor(R.color.colorWhite))
            txt_phone_call.setBackgroundResource(R.drawable.bg_box_grey)
            txt_office_visit.setBackgroundResource(R.drawable.bg_box_grey)
            txt_phone_call.setTextColor(mActivity!!.getColor(R.color.colorPrimary))
            txt_office_visit.setTextColor(mActivity!!.getColor(R.color.colorPrimary))
            communicationType = "Video call"
        }

        txt_office_visit.setOnClickListener {
            txt_office_visit.setBackgroundResource(R.drawable.bg_box_blue)
            txt_office_visit.setTextColor(mActivity!!.getColor(R.color.colorWhite))
            txt_video_call.setBackgroundResource(R.drawable.bg_box_grey)
            txt_phone_call.setBackgroundResource(R.drawable.bg_box_grey)
            txt_video_call.setTextColor(mActivity!!.getColor(R.color.colorPrimary))
            txt_phone_call.setTextColor(mActivity!!.getColor(R.color.colorPrimary))
            communicationType = "Office visit"
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    fun selectSymptoms(view: View) {
        multiAutoCompleteTextView = view.findViewById(R.id.multi_auto_complete_text_symptoms)
        multiAutoCompleteTextView.setPadding(15, 15, 15, 15)
        @Suppress("DEPRECATION")
        multiAutoCompleteTextView.setTextColor(resources.getColor(R.color.colorBlack))
        // Get the string array from strings.xml file.
        //val symptomsArray = resources.getStringArray(R.array.symptoms_array)
        multiAutoCompleteTextView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        multiAutoCompleteTextView.setOnTouchListener(OnTouchListener { v, event ->
            if (symptomsArray.size > 0) {
                val symptomsText = multiAutoCompleteTextView.text.toString()
                if (symptomsText.isNotEmpty()) {
                    val symptomsList =
                        symptomsText.substring(0, symptomsText.length - 2).split(",").toTypedArray()
                    if (symptomsList.isNotEmpty()) {
                        val tempSymptomsList: ArrayList<String> = ArrayList()
                        tempSymptomsList.addAll(symptomsArray)
                        for (symptom in symptomsList) {
                            tempSymptomsList.remove(symptom.trim())
                        }
                        setSymptomsList(tempSymptomsList)
                    }
                }
                multiAutoCompleteTextView.showDropDown()
            }
            false
        })
    }

    private fun setSymptomsList(symptomsArray: ArrayList<String>) {
        // Create a new data adapter object.
        val arrayAdapter = ArrayAdapter(
            mActivity!!,
            android.R.layout.simple_spinner_dropdown_item,
            symptomsArray
        )
        // Connect the data source with AutoCompleteTextView through adapter.
        multiAutoCompleteTextView.setAdapter(arrayAdapter)
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                updateAddressUI(location)

            }
        }
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
                                BuildConfig.APPLICATION_ID, null
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
        task.addOnSuccessListener { locationSettingsResponse ->
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
                    exception.startResolutionForResult(
                        mActivity!!,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    fun updateAddressUI(location: Location) {
        var geocoder = Geocoder(mActivity!!, Locale.getDefault())

        var addressList = geocoder.getFromLocation(
            location.latitude,
            location.longitude,
            1
        ) as ArrayList<Address>
        //tv_add.text = addressList.get(0).getAddressLine(0)
        Log.d("Last Location", "$addressList")
        if (addressList.get(0).subLocality != null && !addressList.get(0).subLocality.isEmpty()) {
            views.edit_txt_street.setText(addressList.get(0).subLocality)
        } else if (addressList.get(0).thoroughfare != null && !addressList.get(0).thoroughfare.isEmpty()) {
            views.edit_txt_street.setText(addressList.get(0).thoroughfare)
        } else if (addressList.get(0).subAdminArea != null && !addressList.get(0).subAdminArea.isEmpty()) {
            views.edit_txt_street.setText(addressList.get(0).subAdminArea)
        }
        views.edit_txt_city.setText(addressList.get(0).locality)
        views.edit_txt_state.setText(addressList.get(0).adminArea)
        views.edit_txt_postal_code.setText(addressList.get(0).postalCode)
        views.edit_txt_country.setText(addressList.get(0).countryName)
        fusedLocationProvider!!.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (fusedLocationProvider != null && locationCallback != null) {
            fusedLocationProvider!!.removeLocationUpdates(locationCallback)
        }
    }

    private fun getSymptomsList() {
        var rctAes = RCTAes(ReactApplicationContext(mActivity!!))
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .commonList(
                        CommonList(
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString(Utils.CONST_SYMPTOMS_LIST)
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgress()
                            var responseBody = result.string()
                            Log.d("Response Body", responseBody)
                            if (responseBody != "TH207") {
                                val gson = GsonBuilder().create()
                                val symptoms =
                                    gson.fromJson(responseBody, Array<CommonsList>::class.java)
                                        .toList()
                                symptomsSortedList =
                                    symptoms.sortedWith(
                                        compareBy(
                                            CommonsList::seq,
                                            CommonsList::seq
                                        )
                                    )
                                symptomsArray = ArrayList()
                                for (symptom in symptomsSortedList) {
                                    symptomsArray.add(symptom.title)
                                }
                                setSymptomsList(symptomsArray)
                            } else {
                                clearCache()
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
         * @return A new instance of fragment SyncHealthCreateAppointBasicInfo.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            SyncHealthCreateAppointBasicInfo().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }

        const val TAG = "Screen_sync_health_create_appoint_basic_info"
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val REQUEST_CHECK_SETTINGS = 15
    }
}