package com.app.synchealth.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.synchealth.R
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.UploadImage
import com.app.synchealth.databinding.FragmentAuthCodeBinding
import com.app.synchealth.databinding.FragmentSyncHealthConsentLetterBinding
import com.app.synchealth.utils.SignatureView
import com.app.synchealth.utils.Utils
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
 * Use the [SyncHealthConsentLetter.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class SyncHealthConsentLetter : BaseFragment(), SignatureView.OnSignedListener {
    // TODO: Rename and change types of parameters
    private var navigateFrom: String? = null
    var bSigned: Boolean = false
    private lateinit var binding: FragmentSyncHealthConsentLetterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            navigateFrom = it.getString(ARG_PARAM1)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sync_health_consent_letter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSyncHealthConsentLetterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHeader().visibility = View.GONE
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.VISIBLE
        getSubTitle().text = getString(R.string.txt_nav_menu_sync_health_consent_letter)
        binding.layoutConsentLetter.visibility = View.VISIBLE
        binding.layoutScreenshot.visibility = View.GONE
        binding.txtConsentAllergies.text = "Allergies : " + Utils.allergies
        binding.txtConsentSymptoms.text = "Symptoms : " + Utils.selectedSymptoms
        binding.txtConsentCurrMed.text = "Current Medications : " + Utils.medication
        binding.txtScreenshotClose.setOnClickListener {
            if (navigateFrom.equals(Utils.NAVIGATE_FROM_DASHBOARD)) {
                popBackStack()
                replaceFragment(
                    AgoraVideoCall.newInstance(Utils.NAVIGATE_FROM_DASHBOARD),
                    R.id.layout_home,
                    AgoraVideoCall.TAG
                )
            } else {
                replaceFragment(
                    CreateAppointCongratulations(),
                    R.id.layout_home,
                    CreateAppointCongratulations.TAG
                )
            }
        }
        binding.signatureView.setOnSignedListener(this)
        binding.signatureView.clear()
        binding.layoutConsentLetter.setOnClickListener {
            if (bSigned) {
                val screenshot: Bitmap = takeScreenshotOfRootView(binding.layoutConsentLetter)
                binding.imgScreenshot.setImageBitmap(screenshot)
                binding.layoutConsentLetter.visibility = View.GONE
                binding.layoutScreenshot.visibility = View.VISIBLE
                uploadConsentLetter(screenshot)
            } else {
                displayToast("Please sign the consent letter")
            }
        }

        binding.btnClear.setOnClickListener {
            binding.signatureView.clear()
        }
    }

    fun convert(bitmap: Bitmap): String? {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    private fun uploadConsentLetter(bitmap: Bitmap) {
        var rctAes = RCTAes()
        binding.txtScreenshotClose.visibility = View.GONE
        binding.txtFormUpload.text = "Uploading details to server"
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
                            rctAes.encryptString("Consent")
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
                                binding.txtScreenshotClose.visibility = View.VISIBLE
                                binding.txtFormUpload.text = "Form uploaded successfully"
                            } else {
                                binding.txtScreenshotClose.visibility = View.VISIBLE
                                binding.txtFormUpload.text = "Failed to upload form"
                            }
                        } catch (e: Exception) {
                            hideProgress()
                            displayToast("Something went wrong.. Please try after sometime")
                        }
                    }, { error ->
                        hideProgress()
                        binding.txtScreenshotClose.visibility = View.VISIBLE
                        binding.txtFormUpload.text = "Failed to upload form"
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
         * @return A new instance of fragment SyncHealthConsentLetter.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            SyncHealthConsentLetter().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }

        private fun takeScreenshot(view: View): Bitmap {
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache(true)
            val b = Bitmap.createBitmap(view.drawingCache)
            view.buildDrawingCache(false)
            return b
        }

        fun takeScreenshotOfRootView(v: View): Bitmap {
            return takeScreenshot(v.rootView)
        }

        const val TAG = "Screen_sync_health_consent_letter"
    }

    override fun onStartSigning() {
        bSigned = true
    }

    override fun onSigned() {
        bSigned = true
    }

    override fun onClear() {
        bSigned = false
    }
}