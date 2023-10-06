package com.app.synchealth.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.adapters.FinalReviewAdapter
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.CreateAppointment
import com.app.synchealth.data.PaymentStatus
import com.app.synchealth.databinding.FragmentAuthCodeBinding
import com.app.synchealth.databinding.FragmentSyncHealthFinalReviewBinding
import com.app.synchealth.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SyncHealthFinalReview.newInstance] factory method to
 * create an instance of this fragment.
 */
class SyncHealthFinalReview : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentSyncHealthFinalReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sync_health_final_review
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSyncHealthFinalReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHeader().visibility = View.GONE
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.VISIBLE
        getSubTitle().text = getString(R.string.txt_nav_menu_sync_health_final_review)

        val finalReviewList = ArrayList<String>()
        finalReviewList.add("I need consultation for - Myself")
        finalReviewList.add(
            "My location is - " + Utils.selectedStreet + "," +
                    Utils.selectedCity + "," +
                    Utils.selectedState + "," +
                    Utils.selectedPostalCode + "," +
                    Utils.selectedCountry
        )
        finalReviewList.add("Phone# - " + Utils.selectedPhoneNo)
        finalReviewList.add("Would like to talk to - " + Utils.apptProviderType)
        finalReviewList.add("Mode of communications - " + Utils.selectedCommunicationMode)
        finalReviewList.add("Tentative date of appointment - " + Utils.aptScheduleDate)
        finalReviewList.add("Appointment duration would be for - 30 mins")
        finalReviewList.add("Tentative time of appointment - " + Utils.aptScheduleTime)
        //finalReviewList.add("Chosen pharmacy - " + Utils.pharmacyVal)
        finalReviewList.add("Details - " + Utils.details)
        finalReviewList.add("Symptoms - " + Utils.selectedSymptoms)
        finalReviewList.add("Medication - " + Utils.medication)
        finalReviewList.add("Allergies - " + Utils.allergies)

        binding.recyclerViewFinalReview.apply {
            layoutManager = LinearLayoutManager(
                mActivity!!,
                RecyclerView.VERTICAL,
                false
            )
            adapter = FinalReviewAdapter(
                mActivity!!,
                finalReviewList
            )
        }

        binding.btnAppointmentSubmit.setOnClickListener {
            if (binding.checkboxTermsConditions.isChecked) {
                callCreateAppointment()
            } else {
                val builder = AlertDialog.Builder(mActivity!!)
                builder.setTitle("Message")
                builder.setMessage("Please select terms and conditions for further procedure")
                builder.setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                }
                builder.setCancelable(false)
                builder.show()
            }
        }
    }

    fun showConfirmationConsentLetter() {
        val builder = AlertDialog.Builder(mActivity!!)
        builder.setTitle("Confirmation")
        builder.setMessage("Do you wish to sign the Consent Letter?")
        builder.setPositiveButton("I AGREE") { dialog, which ->
            replaceFragment(
                SyncHealthConsentLetter(),
                R.id.layout_home,
                SyncHealthConsentLetter.TAG
            )
        }
        builder.setNegativeButton("I DISAGREE") { dialog, which ->
            replaceFragment(
                CreateAppointCongratulations(),
                R.id.layout_home,
                CreateAppointCongratulations.TAG
            )
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun callCreateAppointment() {
        var rctAes = RCTAes()
        var communicationMode = ""
        when (Utils.selectedCommunicationMode) {
            "Phone call" -> communicationMode = "1"
            "Video call" -> communicationMode = "0"
            "Office visit" -> communicationMode = "2"
        }
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .createAppointmentApi(
                        CreateAppointment(
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString(syncHealthGetPatientId()),
                            rctAes.encryptString(
                                Utils.selectedStreet + "," +
                                        Utils.selectedCity + "," +
                                        Utils.selectedState + "," +
                                        Utils.selectedPostalCode + "," +
                                        Utils.selectedCountry
                            ),
                            rctAes.encryptString(""),
                            rctAes.encryptString(Utils.details),
                            rctAes.encryptString(Utils.aptScheduleDate),
                            "xTBZgiD2CdawgTnW808E+Q==",
                            rctAes.encryptString(Utils.aptScheduleTime),
                            rctAes.encryptString(Utils.aptEndTime),
                            rctAes.encryptString(communicationMode),
                            rctAes.encryptString(Utils.providerType),
                            rctAes.encryptString(Utils.selectedStreet),
                            rctAes.encryptString(Utils.providerId),
                            rctAes.encryptString(Utils.selectedCity),
                            rctAes.encryptString(Utils.selectedState),
                            rctAes.encryptString(Utils.selectedPostalCode),
                            rctAes.encryptString(Utils.selectedCountry),
                            rctAes.encryptString(Utils.pharmacyVal),
                            rctAes.encryptString(Utils.selectedPhoneNo),
                            "awGoFbx9C+qH5g3jy5LcKQ==",
                            rctAes.encryptString("N/A"),
                            "QqpVInoCnTvsFFaZIKbvqQ==",
                            rctAes.encryptString(
                                Utils.selectedSymptoms.split(",").toList()
                                    .joinToString(separator = ",").replace(", ", ",")),
                            rctAes.encryptString(
                                Utils.allergies.split(",").toList().joinToString(separator = ",")
                                    .replace(", ", ",")
                            ),
                            rctAes.encryptString(Utils.medication)
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgress()
                            val responseBody = result.string()
                            Log.d("Response Body", responseBody)
                            if (responseBody.contains("TH200")) {
                                val response = JSONObject(responseBody)
                                if (response.getString("status").equals("TH200")) {
                                    //updatePaymentStatus()
                                    showConfirmationConsentLetter()
                                } else {
                                    displayToast("Please fill proper information $responseBody")
                                }
                            } else {
                                displayToast("Please fill proper information $responseBody")
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

    private fun updatePaymentStatus() {
        var rctAes = RCTAes()
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + "/apis/api/stripePay/")
                    .updatePaymentStatus(
                        PaymentStatus(
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString(syncHealthGetPatientId()),
                            "hrXCIkW9EbMXK8WniB7IXnWWoq4cTUMbgw5dPby+fwo=",
                            "vsp9MB4bHedYfElcXngwHPgyOAB/XYuc8FrLDSmDtT4=",
                            "P80TgiEn5Ud2DtQ2bOsBHfmElK5zREIazd4+dioYs04=",
                            "pEq6sa4yTo1kit5+Go+bGw==",
                            "null",
                            "ITJDcrIqpfMyrxvjQWWh2w==",
                            "wI3UWqCrJbKUXuj0dXpH/Q==",
                            "OETeee0qkTQ9yd4ocqf0J4kbfggIpwUfPDN/3DWth2Q=",
                            "+boRCkGCeuwQUMKoUpNjuQ==",
                            "null",
                            "MDOVsNF7TybrCpoqDufzUQ==",
                            "null",
                            "xkqS3FKGv9bavihz3gFqaA==",
                            "Wh65MYnVv01iSj5iiRAAxw=="
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        hideProgress()
                        val responseBody = result.string()
                        Log.d("Response Body", responseBody)
                        if (responseBody.contains("TH200")) {
                            showConfirmationConsentLetter()
                        } else {
                            displayToast("Please Enter Proper Information")
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
         * @return A new instance of fragment SyncHealthFinalReview.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SyncHealthFinalReview().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_sync_health_final_review"
    }
}