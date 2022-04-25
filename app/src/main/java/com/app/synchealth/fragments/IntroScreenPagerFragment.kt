package com.app.synchealth.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.app.synchealth.R
import com.app.synchealth.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_intro_screen_pager.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IntroScreenPagerFragment.newInstance] factory method to
 * create an instance of this fragment. */
class IntroScreenPagerFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var position: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_intro_screen_pager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ViewPagerAdapter(
            mActivity!!.supportFragmentManager
        )
        view.view_pager.offscreenPageLimit = 1
        view.view_pager.adapter = adapter
        view.indicator.setViewPager(view.view_pager)
        view.view_pager.currentItem = position!!
        view.view_pager.isSaveFromParentEnabled = false
        view.view_pager.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(pos: Int) {
                position = pos
                if (position == 0)
                    view.btn_continue.text = "Get Started"
                else
                    view.btn_continue.text = "Finish"
            }

        })

        view.btn_continue.setOnClickListener {
            if (position == 1) {
                replaceFragmentNoBackStack(LoginFragment(), R.id.layout_home, LoginFragment.TAG)
            } else {
                view.view_pager.currentItem = position!! + 1
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IntroScreenPagerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IntroScreenPagerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_Intro"
    }
}
