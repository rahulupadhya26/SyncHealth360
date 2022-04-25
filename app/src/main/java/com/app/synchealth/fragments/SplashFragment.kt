package com.app.synchealth.fragments


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.app.synchealth.R
import com.app.synchealth.crypto.MCrypt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun getLayout(): Int {
        return R.layout.fragment_splash
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHeader().visibility = View.GONE
        Handler().postDelayed({
            if (isLoggedIn()) {
                replaceFragment(
                    SyncHealthDashboard(),
                    R.id.layout_home,
                    SyncHealthDashboard.TAG
                )
            } else {
                replaceFragmentNoBackStack(
                    IntroScreenPagerFragment(),
                    R.id.layout_home,
                    IntroScreenPagerFragment.TAG
                )
            }
        }, 5000)
        Log.i(
            "decrypted",
            MCrypt.decrypt("Nkn04sFXJnQbejglFOj99TxljqCUeGTVM0zvHfVVTQpZgrjiUAqGeOvJuhVDCYiCsP0YFduiWy3u84yzT/A/xlFwfKcT8/Pna982cbXsqwQSkm55gdLAt+g2Y/4ioaVqwIA3H2V05U6y8JXG3vuDc2Kll7aMTudC4A1HHZ+hEUTQQ1LDvjJBlQ1z5AmQlP/zGBHJPJAdtQoXDtYR++5PhjSrGdiL+8Gw428uASRMMmmvlfYgsyt0UUGarhQ2x1QkxIAUhOgsbXZkF68GkniFGrIZWe0a8/F4/8NYuLpRM/fQUrn/QOgjd3pDVkn4oWZ0JdnKYSSs0JbA4WHyUDYDL8SVCbFMx5htQaWvkgdV30dHzvWdSQRe2JWIo1JopjM99NufzHkYjdH78lmv47zXtA==:ODY3Njg4N2M0OTFhZTJmNA==")!!
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SplashFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SplashFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_Splash"
    }
}
