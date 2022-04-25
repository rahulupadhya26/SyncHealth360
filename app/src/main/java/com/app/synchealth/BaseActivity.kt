package com.app.synchealth

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.app.synchealth.controller.IFragment
import com.app.wecare.crypto.DecryptionImpl
import com.app.wecare.crypto.EncryptionImpl
import com.app.synchealth.interceptor.CommonInterceptor
import com.app.wecare.interceptor.DecryptionInterceptor
import com.app.wecare.interceptor.EncryptionInterceptor
import com.app.synchealth.services.RequestInterface
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


open class BaseActivity : AppCompatActivity(), IFragment {

    companion object {
        //const val baseURL: String = "https://discovertx.csardent.com/API/"
        const val baseURL: String = "https://crm.psyclarity.csardent.com/API/"
        //const val baseURL: String = "https://democrm.csardent.com/API/"
    }

    override fun addFragment(fragment: Fragment, frameId: Int, fragmentName: String) {
        supportFragmentManager.beginTransaction().add(frameId, fragment, fragmentName)
            .commitAllowingStateLoss()
    }

    override fun replaceFragment(fragment: Fragment, frameId: Int, fragmentName: String) {
        supportFragmentManager.beginTransaction().addToBackStack(fragmentName)
            .replace(frameId, fragment, fragmentName).commitAllowingStateLoss()
    }

    override fun replaceFragmentNoBackStack(
        fragment: Fragment,
        frameId: Int,
        fragmentName: String
    ) {
        clearBackStack()
        supportFragmentManager.beginTransaction().replace(frameId, fragment, fragmentName)
            .commitAllowingStateLoss()
    }

    override fun popBackStack() {
        supportFragmentManager.popBackStack()
    }

    fun getFragmentTag(): String {
        return supportFragmentManager.findFragmentById(R.id.layout_home)!!.tag!!
    }

    fun getCurrentFragment(): Fragment {
        return supportFragmentManager.findFragmentById(R.id.layout_home)!!
    }

    private fun clearBackStack() {
        try {
            supportFragmentManager.popBackStackImmediate(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        } catch (e: Exception) {
        }
    }

    fun getEncryptedRequestInterface(): RequestInterface {

        val httpClient = getHttpClient()

        //Encryption Interceptor
        val encryptionInterceptor = EncryptionInterceptor(EncryptionImpl())
        //Decryption Interceptor
        val decryptionInterceptor = DecryptionInterceptor(DecryptionImpl())

        httpClient.addInterceptor(encryptionInterceptor)
        httpClient.addInterceptor(decryptionInterceptor)


        return Retrofit.Builder()
            .baseUrl(baseURL).client(httpClient.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RequestInterface::class.java)
    }


    fun getRequestInterface(): RequestInterface {
        return Retrofit.Builder()
            .baseUrl(baseURL).client(getHttpClient().build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RequestInterface::class.java)
    }

    private fun getHttpClient(): OkHttpClient.Builder {
        val httpClient = OkHttpClient.Builder()

        httpClient.connectTimeout(180, TimeUnit.SECONDS)
        httpClient.callTimeout(4, TimeUnit.MINUTES)
        httpClient.readTimeout(180, TimeUnit.SECONDS)
        httpClient.writeTimeout(1000, TimeUnit.SECONDS)

        val logging = HttpLoggingInterceptor()
        // set your desired log level
        logging.level = HttpLoggingInterceptor.Level.BODY

        // add logging as last interceptor
        httpClient.addInterceptor(logging)  // <-- this is the important line!
        return httpClient
    }

    fun getSyncHealthRequestInterface(configUrl:String): RequestInterface {

        val httpClient = getHttpClient()
        //Decryption Interceptor
        val commonInterceptor = CommonInterceptor()
        httpClient.addInterceptor(commonInterceptor)

        return Retrofit.Builder()
            .baseUrl(configUrl).client(httpClient.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(RequestInterface::class.java)
    }
}
