package com.app.synchealth.fragments

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.adapters.AppointmentListAdapter
import com.app.synchealth.controller.OnAppointmentItemClickListener
import com.app.synchealth.data.*
import com.app.synchealth.utils.Utils
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.databinding.FragmentAuthCodeBinding
import com.app.synchealth.databinding.FragmentSyncHealthDashboardBinding
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
 * Use the [SyncHealthDashboard.newInstance] factory method to
 * create an instance of this fragment.
 */
class SyncHealthDashboard : BaseFragment(), OnAppointmentItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var views: View
    var profileDetails: ArrayList<ProfileDetails>? = null
    var prevSpecialist: ArrayList<PrevSpecialist> = ArrayList()
    var prevSpecData: PrevSpecialist? = null
    private lateinit var binding: FragmentSyncHealthDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sync_health_dashboard
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSyncHealthDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views = view
        getHeader().visibility = View.VISIBLE
        getBackButton().visibility = View.INVISIBLE
        getSubTitle().visibility = View.VISIBLE
        getSubTitle().text = getString(R.string.txt_nav_menu_sync_health)

        //Check if service is started or not
        /*if (!Utils.isServiceRunning(mActivity!!, SyncHealthService::class.java)) {
            Utils.startBackGroundService(mActivity!!)
        }*/

        binding.txtProfileName.text = "Good Morning, " + profileInfo()
        mActivity!!.setUserName()
        binding.fabSyncHealthCreateAppointmentBtn.setOnClickListener {
            replaceFragment(
                SyncHealthCreateAppointBasicInfo(),
                R.id.layout_home,
                SyncHealthCreateAppointBasicInfo.TAG
            )
        }

        binding.btnViewProfile.setOnClickListener {
            replaceFragment(
                SyncHealthProfile(),
                R.id.layout_home,
                SyncHealthProfile.TAG
            )
        }

        binding.layoutArticles.setOnClickListener {
            replaceFragment(
                SyncHealthArticles(),
                R.id.layout_home,
                SyncHealthArticles.TAG
            )
        }

        binding.layoutPharmacy.setOnClickListener {
            displayToast("Screen under construction")
        }

        binding.layoutVitals.setOnClickListener {
            replaceFragment(
                SyncHealthVitals(),
                R.id.layout_home,
                SyncHealthVitals.TAG
            )
        }

        binding.layoutPerscription.setOnClickListener {
            replaceFragment(
                PrescriptionFragment(),
                R.id.layout_home,
                PrescriptionFragment.TAG
            )
        }

        binding.layoutHealthinfo.setOnClickListener {
            replaceFragment(
                SyncHealthInfo.newInstance(true),
                R.id.layout_home,
                SyncHealthInfo.TAG
            )
        }

        binding.btnPrevConsultViewAll.setOnClickListener {
            replaceFragment(
                SyncHealthPrevConsult(),
                R.id.layout_home,
                SyncHealthPrevConsult.TAG
            )
        }

        binding.btnQuickBookNow.setOnClickListener {
            if (prevSpecData != null) {
                Utils.providerId = prevSpecData!!.provider_id
                Utils.providerType = prevSpecData!!.option_id
                Utils.apptProviderType = prevSpecData!!.title
                replaceFragment(
                    SyncHealthCreateAppointBasicInfo.newInstance(Utils.QUICK_BOOK),
                    R.id.layout_home,
                    SyncHealthCreateAppointBasicInfo.TAG
                )
            } else {
                displayToast("Data not found!!")
            }
        }

        showProgress()
        getAppointmentList()
    }

    /*private fun callGetConfigApi() {
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(syncHealthGetConfigURL)
                    .getConfig(GetConfig(MCrypt.encrypt(getUserInfo().email)!!))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val responseBody: String = result.string()
                        updateSyncHealthConfigResponse(responseBody)
                        callLoginApi()
                    }, { error ->
                        hideProgress()
                        displayToast("Error ${error.localizedMessage}")
                    })
            )
        }
        handler!!.postDelayed(runnable!!, 1000)
    }*/

    private fun getAppointmentList() {
        val rctAes = RCTAes()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .appointmentList(
                        SyncHealthAppointmentList(
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString(syncHealthGetPatientId()),
                            rctAes.encryptString("N/A")
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            val responseBody = result.string()
                            Log.d("Response Body", responseBody)
                            if (checkErrorCode(responseBody)) {
                                binding.cardviewNoAppointment.visibility = View.GONE
                                binding.recyclerViewAppointmentList.visibility = View.VISIBLE
                                val gson = GsonBuilder().create()
                                val appointmentList =
                                    gson.fromJson(responseBody, Array<AppointmentList>::class.java)
                                        .toList()
                                binding.recyclerViewAppointmentList.apply {
                                    layoutManager =
                                        LinearLayoutManager(
                                            mActivity!!,
                                            RecyclerView.HORIZONTAL,
                                            false
                                        )
                                    adapter = AppointmentListAdapter(
                                        mActivity!!,
                                        appointmentList, this@SyncHealthDashboard
                                    )
                                }
                            } else {
                                binding.recyclerViewAppointmentList.visibility = View.GONE
                                binding.cardviewNoAppointment.visibility = View.VISIBLE
                            }
                            getPrevSpecialist()
                        } catch (e: Exception) {
                            hideProgress()
                            binding.cardviewNoAppointment.visibility = View.VISIBLE
                            displayToast("Something went wrong.. Please try again later")
                        }
                    }, { error ->
                        hideProgress()
                        binding.cardviewNoAppointment.visibility = View.VISIBLE
                        displayToast("Error ${error.localizedMessage}")
                    })
            )
        }
        handler!!.postDelayed(runnable!!, 1000)
    }

    @SuppressLint("SetTextI18n")
    private fun getPrevSpecialist() {
        var rctAes = RCTAes()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .prevSpecialist(
                        DoctorsReq(
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
                            if (checkErrorCode(responseBody)) {
                                val gson = GsonBuilder().create()
                                val previousSpecialist: Type =
                                    object : TypeToken<ArrayList<PrevSpecialist?>?>() {}.type
                                prevSpecialist = ArrayList()
                                prevSpecialist =
                                    gson.fromJson(responseBody, previousSpecialist)
                                prevSpecData = prevSpecialist[0]
                                binding.txtQuickPhysician.text = prevSpecData!!.title
                                binding.textQuickDoctorName.text =
                                    "Dr. " + prevSpecData!!.fname + " " + prevSpecData!!.lname
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

    private fun cancelAppointment(appointmentList: AppointmentList) {
        var rctAes = RCTAes()
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .cancelAppointment(
                        CancelAppointment(
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString(appointmentList.tele_token),
                            rctAes.encryptString(appointmentList.pc_eid),
                            rctAes.encryptString("4")
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            var responseBody = result.string()
                            Log.d("Response Body", responseBody)
                            if (responseBody.contains("TH200")) {
                                displayToast("Appointment cancelled successfully.")
                                getAppointmentList()
                            } else {
                                displayToast("Something went wrong. Please try later!")
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
         * @return A new instance of fragment SyncHealthDashboard.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SyncHealthDashboard().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_sync_health_dashboard"
        const val syncHealthGetConfigURL: String = "https://masterehr.csardent.com/accounts/"
    }

    override fun OnAppointmentItemClickListener(
        event: Int,
        appointmentList: AppointmentList
    ) {
        when (event) {

            Utils.EVENT_START_APPOINTMENT -> {
                Utils.apptId = appointmentList.appt_id
                Utils.apptTeleToken = appointmentList.tele_token
                Utils.apptPcId = appointmentList.pc_eid
                if (appointmentList.consent.contains("pending")) {
                    replaceFragment(
                        SyncHealthConsentLetter.newInstance(Utils.NAVIGATE_FROM_DASHBOARD),
                        R.id.layout_home,
                        SyncHealthConsentLetter.TAG
                    )
                } else {
                    replaceFragment(
                        AgoraVideoCall(),
                        R.id.layout_home,
                        AgoraVideoCall.TAG
                    )
                }
            }
            Utils.EVENT_CANCEL_APPOINTMENT -> {
                cancelAppointment(appointmentList)
            }
            Utils.EVENT_APPOINTMENT_CHAT -> {
                displayToast("Screen under construction")
                /*replaceFragment(
                    SyncHealthChats.newInstance(appointmentList),
                    R.id.layout_home,
                    SyncHealthChats.TAG
                )*/
            }
            Utils.EVENT_APPOINTMENT_EMAIL -> {
                sendEmail()
            }
        }
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Consultation - " + profileInfo())
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                mActivity!!,
                "There is no email client installed.", Toast.LENGTH_SHORT
            ).show()
        }
    }


}