package com.app.synchealth.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.adapters.VitalsAdapter
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.GetVitals
import com.app.synchealth.data.Vitals
import com.app.synchealth.databinding.FragmentSyncHealthVitalsBinding
import com.app.synchealth.utils.Utils
import com.google.common.reflect.TypeToken
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.lang.reflect.Type

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SyncHealthVitals.newInstance] factory method to
 * create an instance of this fragment.
 */
class SyncHealthVitals : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var vitals: ArrayList<Vitals> = ArrayList()
    private lateinit var binding: FragmentSyncHealthVitalsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sync_health_vitals
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSyncHealthVitalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHeader().visibility = View.GONE
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.VISIBLE
        getSubTitle().text = getString(R.string.txt_nav_menu_sync_health_vitals)
        getVitalInfo()
    }

    private fun getVitalInfo() {
        val rctAes = RCTAes()
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .getVitalInfo(
                        GetVitals(
                            rctAes.encryptString(syncHealthGetPatientId()),
                            rctAes.encryptString(syncHealthGetToken())
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgress()
                            var responseBody = result.string()
                            Log.d("Response Body", responseBody)
                            if (!responseBody.contains("TH102")) {
                                val gson = GsonBuilder().create()
                                val previousSpecialist: Type =
                                    object : TypeToken<ArrayList<Vitals?>?>() {}.getType()
                                vitals = ArrayList()
                                vitals =
                                    gson.fromJson(responseBody, previousSpecialist)
                                binding.recyclerviewVitals.apply {
                                    layoutManager =
                                        LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                                    adapter = VitalsAdapter(
                                        mContext!!,
                                        vitals
                                    )
                                }
                            } else {
                                binding.textNoVitalsInfo.text =
                                    "No vital information for the logged in user."
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
         * @return A new instance of fragment SyncHealthVitals.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SyncHealthVitals().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_sync_health_vitals"
    }
}