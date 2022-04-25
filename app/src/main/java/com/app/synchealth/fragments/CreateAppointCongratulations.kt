package com.app.synchealth.fragments

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import com.app.synchealth.R
import com.app.synchealth.utils.Utils
import kotlinx.android.synthetic.main.fragment_create_appoint_congratulations.*
import kotlinx.android.synthetic.main.fragment_create_appoint_congratulations.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateAppointCongratulations.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateAppointCongratulations : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_create_appoint_congratulations
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHeader().visibility = View.GONE
        getBackButton().visibility = View.GONE
        getSubTitle().visibility = View.GONE
        text_final_fname.text = Utils.patientName
        text_appointment_date_time.text = Utils.aptScheduleDate + " " + Utils.aptScheduleTime
        view.btn_go_to_dashboard.setOnClickListener {
            for (i in 0 until mActivity!!.supportFragmentManager.backStackEntryCount) {
                if (mActivity!!.getCurrentFragment() !is SyncHealthDashboard) {
                    popBackStack()
                } else {
                    break
                }
            }
        }

        view.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                redirectToHome()
                replaceFragmentNoBackStack(
                    SyncHealthDashboard(),
                    R.id.layout_home,
                    SyncHealthDashboard.TAG
                )
                true
            }
            false
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateAppointCongratulations.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateAppointCongratulations().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_sync_health_congratulations"
    }
}