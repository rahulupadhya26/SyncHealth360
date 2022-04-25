package com.app.synchealth.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.RatingBar
import com.app.synchealth.R
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.SendFeedback
import com.app.synchealth.utils.Utils
import com.facebook.react.bridge.ReactApplicationContext
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_sync_health_provider_feed_back.*
import kotlinx.android.synthetic.main.fragment_sync_health_rate_visit.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SyncHealthRateVisit.newInstance] factory method to
 * create an instance of this fragment.
 */
class SyncHealthRateVisit : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var rateYourVistRating: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sync_health_rate_visit
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHeader().visibility = View.GONE
        getBackButton().visibility = View.INVISIBLE
        getSubTitle().visibility = View.VISIBLE
        getSubTitle().text = getString(R.string.txt_nav_menu_sync_health_rate_visit)

        ratingBar_visit_rate.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { p0, p1, p2 ->
                rateYourVistRating = p1.toString()
            }

        btn_visit_rate.setOnClickListener {
            if (rateYourVistRating != null) {
                if (!edit_txt_visit_rate.getText().toString().trim()
                        .isEmpty()
                ) {
                    sendVisitRate()
                } else {
                    displayToast("Please provide feedback")
                }
            } else {
                displayToast("Please give rating")
            }
        }
    }

    private fun sendVisitRate() {
        val rctAes = RCTAes(ReactApplicationContext(mActivity!!))
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .sendFeedback(
                        SendFeedback(
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString(Utils.apptId),
                            rctAes.encryptString(rateYourVistRating),
                            rctAes.encryptString(
                                edit_txt_visit_rate.getText().toString().trim()
                            ),
                            rctAes.encryptString("VIS")
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        hideProgress()
                        var responseBody = result.string()
                        Log.d("Response Body", responseBody)
                        if (!responseBody.equals("TH102")) {
                            popBackStack()
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
         * @return A new instance of fragment SyncHealthRateVisit.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SyncHealthRateVisit().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_sync_health_rate_visit"
    }
}