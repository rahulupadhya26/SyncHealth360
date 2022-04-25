package com.app.synchealth.services

import com.app.synchealth.MainActivity
import com.app.synchealth.data.TokenPostData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class TokenTracker(token: String,activity: MainActivity) {
    private var mCompositeDisposable: CompositeDisposable = CompositeDisposable()
    init {
        try {
            val tokenData = TokenPostData(activity.getUserInfo().id,token)
            mCompositeDisposable.add(
                    activity.getEncryptedRequestInterface()
                            .saveUserToken(tokenData)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            if (result.message.equals("success", ignoreCase = true)) {
                                activity.updateTokenSavedStatus()
                            }
                        }, { _ ->

                        })
            )
        }catch (e:Exception)
        {

        }

    }
}