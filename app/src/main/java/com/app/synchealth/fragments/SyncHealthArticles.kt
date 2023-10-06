package com.app.synchealth.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.synchealth.R
import com.app.synchealth.adapters.ArticlesAdapter
import com.app.synchealth.adapters.SuggestedVideoAdapter
import com.app.synchealth.controller.OnSuggestedVideoClickListener
import com.app.synchealth.crypto.RCTAes
import com.app.synchealth.data.Articles
import com.app.synchealth.data.GetArticles
import com.app.synchealth.utils.ExpoPlayerUtils
import com.app.synchealth.utils.Utils
import com.app.synchealth.data.*
import com.app.synchealth.databinding.FragmentAuthCodeBinding
import com.app.synchealth.databinding.FragmentSyncHealthArticlesBinding
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SyncHealthArticles.newInstance] factory method to
 * create an instance of this fragment.
 */
class SyncHealthArticles : BaseFragment(), OnSuggestedVideoClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var suggestedVideos: ArrayList<String> = ArrayList()
    private var expoPlayerUtils: ExpoPlayerUtils? = null
    private lateinit var binding: FragmentSyncHealthArticlesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_sync_health_articles
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSyncHealthArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHeader().visibility = View.GONE
        getBackButton().visibility = View.VISIBLE
        getSubTitle().visibility = View.VISIBLE
        getSubTitle().text = getString(R.string.txt_nav_menu_sync_health_articles)
        showProgress()
        getArticles()
        binding.txtVideoplayerClose.setOnClickListener {
            try {
                if (expoPlayerUtils != null) {
                    expoPlayerUtils!!.releasePlayer()
                }
                getSubTitle().visibility = View.VISIBLE
                binding.layoutArticlesList.visibility = View.VISIBLE
                getBackButton().visibility = View.VISIBLE
                binding.layoutVideoplayer.visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getArticles() {
        val rctAes = RCTAes()
        runnable = Runnable {
            mCompositeDisposable.add(
                getSyncHealthRequestInterface(Utils.SYNC_HEALTH_BASE_URL + Utils.SYNC_HEALTH_URL_PART)
                    .getArticles(
                        GetArticles(
                            rctAes.encryptString(syncHealthGetToken())
                        )
                    )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        try {
                            hideProgress()
                            val responseBody = result.string()
                            Log.d("Response Body", responseBody)
                            if (!responseBody.contains("TH102")) {
                                val gson = GsonBuilder().create()
                                val articlesList =
                                    gson.fromJson(responseBody, Array<Articles>::class.java)
                                        .toList()
                                binding.recyclerviewArticles.apply {
                                    layoutManager =
                                        LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                                    adapter = ArticlesAdapter(
                                        mContext!!,
                                        articlesList
                                    )
                                }
                                suggestedVideos = ArrayList()
                                for (articles in articlesList) {
                                    suggestedVideos.add(articles.link)
                                }
                                if (suggestedVideos.isNotEmpty()) {
                                    binding.titleSuggestVideo.visibility = View.VISIBLE
                                    binding.recyclerViewSuggestedVideos.apply {
                                        layoutManager =
                                            LinearLayoutManager(
                                                mContext,
                                                RecyclerView.HORIZONTAL,
                                                false
                                            )
                                        adapter = SuggestedVideoAdapter(
                                            mContext!!,
                                            suggestedVideos, this@SyncHealthArticles
                                        )
                                    }
                                }
                            } else {
                                binding.textNoArticlesInfo.visibility = View.VISIBLE
                                binding.textNoArticlesInfo.text =
                                    "No record found."
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            hideProgress()
                            binding.textNoArticlesInfo.visibility = View.VISIBLE
                            binding.textNoArticlesInfo.text =
                                "No record found."
                            displayToast("Something went wrong.. Please try after sometime")
                        }
                    }, { error ->
                        hideProgress()
                        binding.textNoArticlesInfo.visibility = View.VISIBLE
                        binding.textNoArticlesInfo.text =
                            "No record found."
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
         * @return A new instance of fragment SyncHealthArticles.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SyncHealthArticles().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val TAG = "Screen_sync_health_articles"
    }

    override fun onPause() {
        super.onPause()
        getHeader().visibility = View.GONE
        try {
            if (expoPlayerUtils != null) {
                expoPlayerUtils!!.releasePlayer()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            if (expoPlayerUtils != null) {
                expoPlayerUtils!!.releasePlayer()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun OnSuggestedVideoClickListener(video: String) {
        expoPlayerUtils = ExpoPlayerUtils()
        getSubTitle().visibility = View.GONE
        binding.layoutArticlesList.visibility = View.GONE
        getBackButton().visibility = View.GONE
        binding.layoutVideoplayer.visibility = View.VISIBLE
        expoPlayerUtils!!.initializePlayer(mContext!!, binding.videoPlayer, video)
    }
}