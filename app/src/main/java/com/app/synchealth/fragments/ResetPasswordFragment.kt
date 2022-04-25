package com.app.synchealth.fragments

import android.os.Bundle
import android.view.View
import com.app.synchealth.R
import com.app.synchealth.data.UserData
import com.app.synchealth.preference.PrefKeys
import com.app.synchealth.preference.PreferenceHelper
import com.app.synchealth.preference.PreferenceHelper.set
import com.app.synchealth.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_reset_password.view.*

private const val ARG_PARAM1 = "param1"

class ResetPasswordFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var userData: UserData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userData = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_reset_password
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.VISIBLE
        getSubTitle().text = resources.getText(R.string.txt_lbl_reset_password)
        view.btn_submit.setOnClickListener {
            if(isValidText(view.edit_password))
            {
                if(isValidText(view.edit_confirm_password))
                {
                    if(getText(view.edit_password) == getText(view.edit_confirm_password))
                    {
                        userData!!.password = getText(view.edit_password)
                        userData!!.action = Utils.CONST_STATUS_ACTION_CHANGE_PASSWORD
                        showWhiteProgress()
                        mCompositeDisposable.add(
                            getEncryptedRequestInterface()
                                .updateProfile(userData!!)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({
                                        result ->
                                    hideProgress()
                                    if (result.message.equals("Success",ignoreCase =true)) {
                                        try {
                                            val prefs = PreferenceHelper.defaultPrefs(mActivity!!)
                                            prefs[PrefKeys.PREF_Pass] = getText(view.edit_password)
                                            displayToast("Success! Your password changed successfully.")
                                            replaceFragmentNoBackStack(LoginFragment(),R.id.layout_home,LoginFragment.TAG)
                                        }catch (e:Exception){}
                                    }
                                }, {
                                        error ->
                                    hideProgress()
                                    displayToast("Error ${error.localizedMessage}")
                                }))
                    }else
                    {
                        displayToast("Password and confirm password should be same")
                    }

                }else
                {
                    displayToast("Please enter confirm password")
                }

            }else
            {
                displayToast("Please enter password")
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance(userData: UserData) =
            ResetPasswordFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, userData)
                }
            }

        const val TAG = "Screen_Reset_Password"
    }
}
