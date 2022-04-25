package com.app.synchealth.adapters


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.app.synchealth.fragments.IntroScreensFragment

class ViewPagerAdapter internal constructor(fm: FragmentManager) : FragmentStatePagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
       return IntroScreensFragment.newInstance(position.toString(),"")
    }
    override fun getCount(): Int {
        return 2
    }
}
