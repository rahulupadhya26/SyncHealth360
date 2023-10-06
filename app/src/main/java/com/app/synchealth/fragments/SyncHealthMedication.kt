package com.app.synchealth.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.app.synchealth.R
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.CommonList
import com.app.synchealth.data.CommonsList
import com.app.synchealth.data.UploadImage
import com.app.synchealth.databinding.FragmentAuthCodeBinding
import com.app.synchealth.databinding.FragmentSyncHealthMedicationBinding
import com.app.synchealth.utils.Utils
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.lang.Exception


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SyncHealthMedication.newInstance] factory method to
 * create an instance of this fragment.
 */
class SyncHealthMedication : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var multiAutoCompleteTextViewSelectAllergies: MultiAutoCompleteTextView
    var allergiesArray: ArrayList<String> = ArrayList()
    private lateinit var binding: FragmentSyncHealthMedicationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sync_health_medication
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSyncHealthMedicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHeader().visibility = View.GONE
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.VISIBLE
        getSubTitle().text = getString(R.string.txt_nav_menu_sync_health_medication)
        selectAllergies()
        getAllergiesList()
        binding.btnAddPicture.setOnClickListener {
            captureImage(binding.imgCapturePhotoForReview)
        }
        binding.btnCurrentMedication.setOnClickListener {
            var allergyText = multiAutoCompleteTextViewSelectAllergies.getText().toString()
            if (!allergyText.isEmpty()) {
                if (!binding.editTxtCurrentMedication.text.toString().isEmpty()) {
                    if (!binding.editTxtMedicationExplaination.text.toString().isEmpty()) {
                        if (binding.imgCapturePhotoForReview.drawable != null) {
                            val bitmap = binding.imgCapturePhotoForReview.drawable.toBitmap()
                            uploadMedicationLetter(bitmap)
                        } else {
                            navigateToNextScreen()
                        }
                    } else {
                        binding.editTxtMedicationExplaination.error = "Please enter detail"
                    }
                } else {
                    binding.editTxtCurrentMedication.error = "Please enter current medication"
                }
            } else {
                displayToast("Please select allergies, if any")
            }
        }
    }

    private fun setAllergyList(allergyArray: ArrayList<String>) {
        // Create a new data adapter object.
        val arrayAdapter = ArrayAdapter(
            mActivity!!,
            android.R.layout.simple_spinner_dropdown_item,
            allergyArray
        )
        // Connect the data source with AutoCompleteTextView through adapter.
        multiAutoCompleteTextViewSelectAllergies.setAdapter(arrayAdapter)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun selectAllergies() {
        multiAutoCompleteTextViewSelectAllergies = binding.multiAutoCompleteTextAllergies
        multiAutoCompleteTextViewSelectAllergies.setPadding(15, 15, 15, 15)
        @Suppress("DEPRECATION")
        multiAutoCompleteTextViewSelectAllergies.setTextColor(getResources().getColor(R.color.colorBlack))
        // Get the string array from strings.xml file.
        //val symptomsArray = resources.getStringArray(R.array.symptoms_array)
        multiAutoCompleteTextViewSelectAllergies.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        multiAutoCompleteTextViewSelectAllergies.setOnTouchListener(View.OnTouchListener { v, event ->
            if (allergiesArray.size > 0) {
                var allergyText = multiAutoCompleteTextViewSelectAllergies.getText().toString()
                if (!allergyText.isEmpty()) {
                    var allergiesList =
                        allergyText.substring(0, allergyText.length - 2).split(",").toTypedArray()
                    if (allergiesList.size >= 1) {
                        var tempAllergiesList: ArrayList<String> = ArrayList()
                        tempAllergiesList.addAll(allergiesArray)
                        for (symptom in allergiesList) {
                            tempAllergiesList.remove(symptom.trim())
                        }
                        setAllergyList(tempAllergiesList)
                    }
                }
                multiAutoCompleteTextViewSelectAllergies.showDropDown()
            }
            false
        })
    }

    private fun getAllergiesList() {
        var rctAes = RCTAes()
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .commonList(
                        CommonList(
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString(Utils.CONST_ALLERGY_LIST)
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgress()
                            var responseBody = result.string()
                            Log.d("Response Body", responseBody)
                            if (!responseBody.equals("TH207")) {
                                val gson = GsonBuilder().create()
                                val allergies =
                                    gson.fromJson(responseBody, Array<CommonsList>::class.java)
                                        .toList()
                                val allergySortedList =
                                    allergies.sortedWith(
                                        compareBy(
                                            CommonsList::seq,
                                            CommonsList::seq
                                        )
                                    )
                                allergiesArray = ArrayList()
                                for (symptom in allergySortedList) {
                                    allergiesArray.add(symptom.title)
                                }
                                setAllergyList(allergiesArray)
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

    private fun convert(bitmap: Bitmap): String? {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    private fun navigateToNextScreen() {
        var allergyText =
            multiAutoCompleteTextViewSelectAllergies.getText().toString()
        Utils.allergies = allergyText.substring(0, allergyText.length - 2).trim()
        Utils.medication = binding.editTxtCurrentMedication.text.toString().trim()
        Utils.details = binding.editTxtMedicationExplaination.text.toString().trim()
        replaceFragment(
            SyncHealthFinalReview(),
            R.id.layout_home,
            SyncHealthFinalReview.TAG
        )
    }

    private fun uploadMedicationLetter(bitmap: Bitmap) {
        var rctAes = RCTAes()
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .uploadImage(
                        UploadImage(
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString("data:image/jpg;base64," + convert(bitmap)),
                            "fmYlnPeg3uyPtlS3HKcANg==",
                            rctAes.encryptString(syncHealthGetPatientId()),
                            rctAes.encryptString(syncHealthGetPatientId() + "_" + ".jpg"),
                            rctAes.encryptString("Information")
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgress()
                            var responseBody = result.string()
                            Log.d("Response Body", responseBody.drop(2))
                            if (responseBody.contains("TH200")) {
                                navigateToNextScreen()
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
         * @return A new instance of fragment SyncHealthMedication.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SyncHealthMedication().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_sync_health_medication"
    }
}