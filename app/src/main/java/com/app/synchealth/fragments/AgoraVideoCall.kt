package com.app.synchealth.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.app.synchealth.R
import com.app.synchealth.data.CancelAppointment
import com.app.synchealth.utils.Utils
import com.app.synchealth.crypto.RCTAes
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AgoraVideoCall.newInstance] factory method to
 * create an instance of this fragment.
 */
class AgoraVideoCall : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var navigateFrom: String? = null

    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1

    private var mRtcEngine: RtcEngine? = null

    private var mMuted = false

    private var mLocalContainer: FrameLayout? = null
    private var mLocalVideo: VideoCanvas? = null
    private var mRemoteVideo: VideoCanvas? = null

    private var mCallBtn: ImageView? = null
    private var mMuteBtn: ImageView? = null
    private var mSwitchCameraBtn: ImageView? = null
    private var views: View? = null
    private val cameraMicPermissionRequestCode = 1

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote user joining the channel to get the uid of the user.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            requireActivity().runOnUiThread {
                // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
                setupRemoteVideo(uid)
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            requireActivity().runOnUiThread {
                onRemoteUserLeft(uid)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            navigateFrom = it.getString(ARG_PARAM1)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_agora_video_call
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views = view
        getHeader().visibility = View.GONE
        getBackButton().visibility = View.INVISIBLE
        getSubTitle().visibility = View.VISIBLE
        getSubTitle().text = getString(R.string.txt_nav_menu_video_call)

        initUI()

        view.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                endVideoCall()
                true
            }
            false
        }

        /*
         * Request permissions.
         */
        if (checkPermissionForCameraAndMicrophone()) {
            initializeAndJoinChannel()
        } else {
            requestPermissionForCameraAndMicrophone()
        }
    }

    private fun requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                mActivity!!,
                Manifest.permission.CAMERA
            ) ||
            ActivityCompat.shouldShowRequestPermissionRationale(
                mActivity!!,
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            displayToast(mActivity!!.getString(R.string.permissions_needed))
        } else {
            ActivityCompat.requestPermissions(
                mActivity!!,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                cameraMicPermissionRequestCode
            )
        }
    }

    private fun checkPermissionForCameraAndMicrophone(): Boolean {
        val resultCamera =
            ContextCompat.checkSelfPermission(mActivity!!, Manifest.permission.CAMERA)
        val resultMic =
            ContextCompat.checkSelfPermission(mActivity!!, Manifest.permission.RECORD_AUDIO)

        return resultCamera == PackageManager.PERMISSION_GRANTED &&
                resultMic == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == cameraMicPermissionRequestCode) {
            var cameraAndMicPermissionGranted = true

            for (grantResult in grantResults) {
                cameraAndMicPermissionGranted = cameraAndMicPermissionGranted and
                        (grantResult == PackageManager.PERMISSION_GRANTED)
            }

            if (cameraAndMicPermissionGranted) {
                initializeAndJoinChannel()
            } else {
                displayToast(mActivity!!.getString(R.string.permissions_needed))
            }
        }
    }

    private fun endVideoCall() {
        val builder = AlertDialog.Builder(mActivity!!)
        builder.setTitle("Confirmation")
        builder.setMessage("Do you wish to end the video call?")
        builder.setPositiveButton("Yes") { dialog, which ->
            endCall()
            completedAppointment()
        }
        builder.setNegativeButton("No") { dialog, which ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun completedAppointment() {
        var rctAes = RCTAes()
        showProgress()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .cancelAppointment(
                        CancelAppointment(
                            rctAes.encryptString(syncHealthGetToken()),
                            rctAes.encryptString(Utils.apptTeleToken),
                            rctAes.encryptString(Utils.apptPcId),
                            rctAes.encryptString("3")
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        hideProgress()
                        var responseBody = result.string()
                        Log.d("Response Body", responseBody)
                        if (responseBody.equals("TH200")) {
                            popBackStack()
                            replaceFragment(
                                SyncHealthProviderFeedBack(),
                                R.id.layout_home,
                                SyncHealthProviderFeedBack.TAG
                            )
                        } else {
                            displayToast("Something went wrong. Please try later!")
                        }
                    }, { error ->
                        hideProgress()
                        displayToast("Error ${error.localizedMessage}")
                    })
            )
        }
        handler!!.postDelayed(runnable!!, 1000)
    }

    private fun initUI() {
        mLocalContainer = views!!.findViewById(R.id.local_video_view_container)
        mCallBtn = views!!.findViewById(R.id.btn_call)
        mMuteBtn = views!!.findViewById(R.id.btn_mute)
        mSwitchCameraBtn = views!!.findViewById(R.id.btn_switch_camera)
        mCallBtn!!.setOnClickListener {
            onCallClicked()
        }
        mMuteBtn!!.setOnClickListener {
            onLocalAudioMuteClicked()
        }
        mSwitchCameraBtn!!.setOnClickListener {
            onSwitchCameraClicked()
        }
    }

    private fun setupRemoteVideo(uid: Int) {
        val remoteContainer = views!!.findViewById(R.id.remote_video_view_container) as FrameLayout
        val remoteFrame = RtcEngine.CreateRendererView(mActivity!!)
        remoteFrame.setZOrderMediaOverlay(true)
        remoteContainer.addView(remoteFrame)
        mRtcEngine!!.setupRemoteVideo(VideoCanvas(remoteFrame, VideoCanvas.RENDER_MODE_FIT, uid))
    }

    private fun onRemoteUserLeft(uid: Int) {
        if (mRemoteVideo != null && mRemoteVideo!!.uid == uid) {
            removeFromParent(mRemoteVideo)
            // Destroys remote view
            mRemoteVideo = null
        }
    }

    private fun removeFromParent(canvas: VideoCanvas?): ViewGroup? {
        if (canvas != null) {
            val parent = canvas.view.parent
            if (parent != null) {
                val group = parent as ViewGroup
                group.removeView(canvas.view)
                return group
            }
        }
        return null
    }

    private fun initializeAndJoinChannel() {
        mRtcEngine = RtcEngine.create(mActivity!!, APP_ID, mRtcEventHandler)

        // By default, video is disabled, and you need to call enableVideo to start a video stream.
        mRtcEngine!!.enableVideo()

        // Call CreateRendererView to create a SurfaceView object and add it as a child to the FrameLayout.
        val localFrame = RtcEngine.CreateRendererView(mActivity!!)
        mLocalContainer!!.addView(localFrame)
        // Pass the SurfaceView object to Agora so that it renders the local video.
        mRtcEngine!!.setupLocalVideo(VideoCanvas(localFrame, VideoCanvas.RENDER_MODE_FIT, 0))

        // Join the channel with a token.
        mRtcEngine!!.joinChannel(Utils.apptTeleToken, CHANNEL + Utils.apptPcId, "", 0)
    }

    override fun onDestroy() {
        super.onDestroy()

        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
    }

    private fun leaveChannel() {
        if (mRtcEngine != null) {
            mRtcEngine!!.leaveChannel()
        }
    }

    fun onLocalAudioMuteClicked() {
        mMuted = !mMuted
        // Stops/Resumes sending the local audio stream.
        mRtcEngine!!.muteLocalAudioStream(mMuted)
        val res = if (mMuted) R.drawable.btn_mute else R.drawable.btn_unmute
        mMuteBtn!!.setImageResource(res)
    }

    fun onSwitchCameraClicked() {
        // Switches between front and rear cameras.
        mRtcEngine!!.switchCamera()
    }

    fun onCallClicked() {
        endVideoCall()
    }

    private fun endCall() {
        removeFromParent(mLocalVideo)
        mLocalVideo = null
        removeFromParent(mRemoteVideo)
        mRemoteVideo = null
        leaveChannel()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AgoraVideoCall.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            AgoraVideoCall().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }

        const val TAG = "Screen_sync_Agora_video_call"
        const val APP_ID: String = "63c5dfb4327f464499ca9c7f0ca868b1"
        const val TOKEN: String =
            "00663c5dfb4327f464499ca9c7f0ca868b1IABIOq53ePciYEV5jzDcUAPVHuXOjvczOtMp6SKPFy3XCE8JtkYAAAAAIgCvvwAArAlUYQQAAQAAAAAAAwAAAAAAAgAAAAAABAAAAAAA"
        const val CHANNEL: String = "IntelligenEHR-"
    }
}