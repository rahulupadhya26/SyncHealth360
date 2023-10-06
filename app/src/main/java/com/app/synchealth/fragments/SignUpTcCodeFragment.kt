package com.app.synchealth.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.synchealth.R
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.ConfigData
import com.app.synchealth.data.TcCode
import com.app.synchealth.databinding.FragmentAuthCodeBinding
import com.app.synchealth.databinding.FragmentSignUpTcCodeBinding
import com.app.synchealth.utils.Utils
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpTcCodeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpTcCodeFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentSignUpTcCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sign_up_tc_code
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpTcCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSubTitle().text = ""
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.GONE

        binding.btnTcCodeNextStep.setOnClickListener {
            if (getText(binding.editTxtTcCode).isNotEmpty()) {
                getAuth()
            } else {
                displayToast("Please enter the TC Code")
            }
        }
    }

    private fun getAuth() {
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_MASTER_URL)
                    .getConfig(TcCode(RCTAes().encryptString(getText(binding.editTxtTcCode))))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        hideProgress()
                        val responseBody: String = result.string()
                        Log.i("Info", responseBody)
                        Utils.tccode = getText(binding.editTxtTcCode)
                        val gson = GsonBuilder().create()
                        val configData = gson.fromJson(responseBody, ConfigData::class.java)
                        val json = gson.toJson(configData)
                        saveConfigData(json)
                        replaceFragment(
                            SignUpFragment(),
                            R.id.layout_home,
                            SignUpFragment.TAG
                        )
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
         * @return A new instance of fragment SignUpTcCodeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpTcCodeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_Tc_code"
    }
}