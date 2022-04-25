package com.app.synchealth.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.adapters.PrescriptionAdapter
import com.app.synchealth.adapters.VitalsAdapter
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.GetPrescription
import com.app.synchealth.data.GetVitals
import com.app.synchealth.data.Prescription
import com.app.synchealth.data.Vitals
import com.app.synchealth.utils.Utils
import com.facebook.react.bridge.ReactApplicationContext
import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_prescription.*
import kotlinx.android.synthetic.main.fragment_sync_health_vitals.*
import java.lang.Exception
import java.lang.reflect.Type

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PrescriptionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PrescriptionFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var prescription: Prescription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_prescription
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHeader().visibility = View.GONE
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.VISIBLE
        getSubTitle().text = getString(R.string.txt_nav_menu_sync_health_prescription)
        getPrescriptionInfo()
    }

    private fun getPrescriptionInfo() {
        val rctAes = RCTAes(ReactApplicationContext(mActivity!!))
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .getPrescriptionInfo(
                        GetPrescription(
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString(syncHealthGetPatientId()),
                            rctAes.encryptString("0")
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
                                prescription =
                                    gson.fromJson(responseBody, Prescription::class.java)
                                val prescriptionList: ArrayList<Prescription> = ArrayList()
                                prescriptionList.add(prescription)
                                recyclerview_prescription.apply {
                                    layoutManager =
                                        LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                                    adapter = PrescriptionAdapter(
                                        mContext!!,
                                        prescriptionList
                                    )
                                }
                            } else {
                                text_no_presc_info.text =
                                    "No prescription information for the logged in user."
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
         * @return A new instance of fragment PerscriptionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PrescriptionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_sync_health_prescription"
    }
}