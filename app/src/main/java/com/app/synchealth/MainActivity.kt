package com.app.synchealth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.loader.content.CursorLoader
import com.app.synchealth.data.UserData
import com.app.synchealth.preference.PrefKeys
import com.app.synchealth.preference.PreferenceHelper
import com.app.synchealth.utils.NSFWDetector
import com.app.synchealth.utils.Utils
import com.app.synchealth.controller.IController
import com.app.synchealth.fragments.LoginFragment
import com.app.synchealth.fragments.SplashFragment
import com.app.synchealth.fragments.SyncHealthDashboard
import com.app.synchealth.preference.PreferenceHelper.get
import com.app.synchealth.preference.PreferenceHelper.set
import com.app.synchealth.services.RestartBroadcastReceiver
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_header.*
import kotlinx.android.synthetic.main.layout_header_drawer.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseActivity(), IController {

    private val permissionRequestCode: Int = 1001
    private val requestImageCapture: Int = 1002
    private val requestGalleryImage = 1003
    private val requestFileUpload = 1004
    private val requestVideoUpload = 1005
    private val permissionCode: Int = 1000
    private var mCurrentPhotoPath: String? = ""
    private var bitmapList: ArrayList<String> = ArrayList()
    private var preference: SharedPreferences? = null
    private var selectedOption: Int? = -1
    private var imageView: ImageView? = null

    private var navigationView: BottomNavigationView? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    var progressAlive = false
    var pDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontScale(resources.configuration, 1.0f)
        setContentView(R.layout.activity_main)
        preference = PreferenceHelper.defaultPrefs(this)
        if (!checkPermission()) {
            requestPermission(permissionCode)
        }
        replaceFragmentNoBackStack(SplashFragment(), R.id.layout_home, SplashFragment.TAG)
        getBackButton().setOnClickListener {
            popBackStack()
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseApp.initializeApp(this)
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.app_name))
        ic_menu.setOnClickListener { handleClick(it) }
        ic_search.setOnClickListener { handleClick(it) }
        search.setOnClickListener { handleClick(it) }
        close.setOnClickListener { handleClick(it) }
        ic_notification.setOnClickListener { handleClick(it) }
        nav_view.getHeaderView(0).drawer_img_back.setOnClickListener { _view -> handleClick(_view) }
        nav_view.setNavigationItemSelectedListener { item: MenuItem ->
            drawer_layout.closeDrawer(GravityCompat.START)
            handleClick(item.itemId)
            false
        }
        searchText.setOnEditorActionListener { _, actionId, _ ->
            when (actionId and EditorInfo.IME_MASK_ACTION) {
                EditorInfo.IME_ACTION_DONE -> {
                    //onSearchClick()
                }
            }
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, RestartBroadcastReceiver::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    private fun adjustFontScale(
        configuration: Configuration,
        scale: Float
    ) {
        configuration.fontScale = scale
        val metrics: DisplayMetrics = resources.displayMetrics
        val wm: WindowManager =
            this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        configuration.fontScale = 1.0f
        baseContext.resources.updateConfiguration(configuration, metrics)
    }

    private fun checkPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) ==
                PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission(id: Int) {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACTIVITY_RECOGNITION,
            ),
            id
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionRequestCode) {

            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                takePicture()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            return
        } else if (requestCode == requestFileUpload) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                choosePhotoFromGallery()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == requestVideoUpload) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                uploadVideo()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        } else {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile()

        val uri: Uri = FileProvider.getUriForFile(
            this,
            "com.app.synchealth",
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, requestImageCapture)
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(galleryIntent, requestGalleryImage)
    }

    override fun uploadVideo() {

        if (checkPermission()) {
            val videoPickIntent = Intent(Intent.ACTION_PICK)
            videoPickIntent.type = "video/*"
            startActivityForResult(
                Intent.createChooser(videoPickIntent, "Please pick a video"),
                requestVideoUpload
            )
        } else requestPermission(requestVideoUpload)

    }

    @Throws(IOException::class)
    private fun createFile(): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = absolutePath
        }
    }

    private fun handleClick(view: View) {
        when (view.id) {
            R.id.drawer_img_back -> {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            R.id.ic_menu -> {
                drawer_layout.openDrawer(GravityCompat.START)
            }
            R.id.ic_search -> {
                hideKeyboard(searchText)
                layout_search.visibility = View.VISIBLE
            }
            R.id.search -> {
                //onSearchClick()
            }
            R.id.close -> {
                closeSearch()
            }
//            R.id.ic_cart, R.id.txt_cart_count -> {
//                openCartList()
//            }
//            R.id.ic_favourites ->{
//                replaceFragment(FavouritesFragment(),R.id.layout_home,FavouritesFragment.TAG)
//            }
            /*R.id.ic_notification -> {
                replaceFragment(
                    NotificationsFragment(),
                    R.id.layout_home,
                    NotificationsFragment.TAG
                )
            }*/
        }
    }

    /*private fun onSearchClick() {
        hideKeyboard(searchText)
        val searchKey = searchText.text.toString().trim()
        if (searchKey.isNotEmpty())
            replaceFragment(
                ContentPagerFragment.newInstance(0, searchKey),
                R.id.layout_home,
                ContentPagerFragment.TAG
            )
        else closeSearch()
    }*/

    private fun closeSearch() {
        hideKeyboard(searchText)
        layout_search.visibility = View.GONE
        searchText.setText("")
    }

    private fun handleClick(itemId: Int) {
        when (itemId) {
            R.id.nav_logout -> {
                sendEventLog("", Utils.NAV_LOGOUT)
                displayConfirmPopup()
            }
            R.id.nav_sync -> {
                sendEventLog("", Utils.NAV_SYNC_HEALTH)
                replaceFragment(
                    SyncHealthDashboard(),
                    R.id.layout_home,
                    SyncHealthDashboard.TAG
                )
            }
        }
    }

    private fun displayConfirmPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("Would you like to exit the App?")
        builder.setPositiveButton(android.R.string.yes) { dialog, _ ->
            dialog.dismiss()
            clearCache()
        }
        builder.setNegativeButton(android.R.string.no) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    @SuppressLint("WrongViewCast")
    override fun getHeader(): View {
        return findViewById(R.id.layout_header)
    }

    @SuppressLint("WrongViewCast")
    override fun getBackButton(): View {
        return findViewById(R.id.ico_action_back)
    }

    @SuppressLint("WrongViewCast")
    override fun getSubTitle(): TextView {
        return findViewById(R.id.txt_sub_title)
    }

    @Suppress("DEPRECATION")
    override fun showProgress() {
        /*layout_progress.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            progress.indeterminateDrawable.colorFilter =
                BlendModeColorFilter(getColor(R.color.colorPrimary), BlendMode.SRC_ATOP)
        } else {
            progress.indeterminateDrawable.setColorFilter(
                getColor(R.color.colorPrimary),
                android.graphics.PorterDuff.Mode.MULTIPLY
            )
        }
        layout_home.setBackgroundResource(R.drawable.bg_box_white_top_corners)*/
        if (progressAlive) {
            pDialog!!.cancel()
            progressAlive = false
        }
        pDialog = ProgressDialog(this)
        pDialog!!.setMessage("Please wait...")
        if ("Please wait".contains("Please wait")) pDialog!!.setCanceledOnTouchOutside(false)
        progressAlive = true
        pDialog!!.show()
    }

    @Suppress("DEPRECATION")
    fun showPlainProgress() {
        layout_progress.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            progress.indeterminateDrawable.colorFilter =
                BlendModeColorFilter(getColor(R.color.colorPrimary), BlendMode.SRC_ATOP)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                progress.indeterminateDrawable.setColorFilter(
                    getColor(R.color.colorPrimary),
                    android.graphics.PorterDuff.Mode.MULTIPLY
                )
            }
        }
    }

    @Suppress("DEPRECATION")
    fun showWhiteProgress() {
        layout_progress.visibility = View.VISIBLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            progress.indeterminateDrawable.colorFilter =
                BlendModeColorFilter(getColor(R.color.colorWhite), BlendMode.SRC_ATOP)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                progress.indeterminateDrawable.setColorFilter(
                    getColor(R.color.colorWhite),
                    android.graphics.PorterDuff.Mode.MULTIPLY
                )
            }
        }
    }

    override fun hideProgress() {
        /*layout_home.setBackgroundColor(Color.TRANSPARENT)
        layout_progress.visibility = View.GONE*/
        if (progressAlive) {
            pDialog!!.dismiss()
            pDialog!!.cancel()
            progressAlive = false
        }
    }

    override fun captureImage() {
        if (checkPermission()) takePicture() else requestPermission(permissionRequestCode!!)
    }

    fun captureImage(imageView: ImageView) {
        this.imageView = imageView
        showPictureDialog()
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems =
            arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            dialog.dismiss()
            when (which) {
                0 -> {
                    selectedOption = 0
                    uploadPicture()
                }
                1 -> {
                    selectedOption = 1
                    captureImage()
                }
            }
        }
        pictureDialog.show()
    }

    override fun uploadPicture() {
        if (checkPermission()) choosePhotoFromGallery() else requestPermission(requestFileUpload)
    }

    override fun clearTempFormData() {
        this.bitmapList.clear()
    }

    override fun getBitmapList(): ArrayList<String> {
        return bitmapList
    }

    override fun setBottomMenu(id: Int) {
        getBottomNavigation()!!.selectedItemId = id
    }

    fun updateBitmapList(bitmapList: ArrayList<String>) {
        this.bitmapList = bitmapList
    }

    fun getProfileImageView(): ImageView? {
        return imageView
    }

    fun resetProfileImageView() {
        imageView = null
    }

    override fun getPhotoPath(): String {
        return mCurrentPhotoPath!!
    }

    override fun getTokenSavedStatus(): Boolean {
        return preference!![PrefKeys.PREF_TokenSavedStatus]!!
    }

    override fun syncHealthGetConfigResponse(): String {
        return preference!![PrefKeys.PREF_Sync_Health_Config_Response]!!
    }

    override fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun isLoggedIn(): Boolean {
        return preference!![PrefKeys.PREF_IS_LOGIN, false]!!
    }

    override fun syncHealthGetPatientId(): String {
        return preference!![PrefKeys.PREF_Sync_Health_Patient_Id, ""]!!
    }

    override fun syncHealthGetToken(): String {
        return preference!![PrefKeys.PREF_Sync_Health_Token, ""]!!
    }

    override fun profileInfo(): String {
        return preference!![PrefKeys.PREF_Sync_Health_Profile_Info, ""]!!
    }

    override fun sendEventLog(key: String, value: String) {
        try {
            val bundle = Bundle()
            bundle.putString(key, value)
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun detectImage(bitmap: Bitmap, confidenceThreshold: Float): Boolean {
        var isVulgar = false
        NSFWDetector.isNSFW(bitmap, confidenceThreshold) { isNSFW, label, confidence, image ->
            if (label == Utils.LABEL_SFW) {
                isVulgar = false
                //Toast.makeText(this, "NSFW with confidence: $confidence", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", label)
            } else if (label == Utils.LABEL_NSFW) {
                isVulgar = true
                Log.d("MainActivity", label)
                //Toast.makeText(this, "SFW with confidence: $confidence", Toast.LENGTH_SHORT).show()
            } else {
                isVulgar = false
            }
        }
        return isVulgar
    }

    override fun getUserInfo(): UserData {
        return UserData(
            preference!![PrefKeys.PREF_ID, ""]!!,
            preference!![PrefKeys.PREF_Name, ""]!!,
            preference!![PrefKeys.PREF_Mobile, ""]!!,
            preference!![PrefKeys.PREF_Hno, ""]!!,
            preference!![PrefKeys.PREF_City, ""]!!,
            preference!![PrefKeys.PREF_State, ""]!!,
            preference!![PrefKeys.PREF_Pincode, ""]!!,
            preference!![PrefKeys.PREF_Email, ""]!!,
            preference!![PrefKeys.PREF_Landmark, ""]!!,
            FavoritesCount = preference!![PrefKeys.PREF_FavoriteCount, 0]!!,
            userProfilePhoto = preference!![PrefKeys.PREF_Pic, ""]!!,
            lastname = preference!![PrefKeys.PREF_Last_Name, ""]!!,
            isExpert = preference!![PrefKeys.PREF_isExpert, false]!!,
            isUser = preference!![PrefKeys.PREF_isUser, false]!!,
            isSponsor = preference!![PrefKeys.PREF_isSponsor, false]!!,
            pass = preference!![PrefKeys.PREF_Pass, ""]!!
        )
    }

    override fun clearCache() {
        preference!![PrefKeys.PREF_ID] = ""
        preference!![PrefKeys.PREF_IS_LOGIN] = false
        preference!![PrefKeys.PREF_TokenSavedStatus] = false
        preference!![PrefKeys.PREF_Sync_Health_Patient_Id] = ""
        preference!![PrefKeys.PREF_Sync_Health_Token] = ""
        preference!![PrefKeys.PREF_Sync_Health_Profile_Info] = ""
        getHeader().visibility = View.GONE
        swipeSliderEnable(false)
        setBottomNavigation(null)
        replaceFragmentNoBackStack(LoginFragment(), R.id.layout_home, LoginFragment.TAG)
    }

    fun setUserName() {
        if (getUserInfo().id.isNotEmpty()) {
            val name = getUserInfo().name + " " + getUserInfo().lastname
            nav_view.getHeaderView(0).name_profile.text = name
            nav_view.getHeaderView(0).email_profile.text = getUserInfo().email
            Glide.with(this).load(getUserInfo().userProfilePhoto)
                .apply(RequestOptions().optionalCircleCrop())
                .into(nav_view.getHeaderView(0).img_profile)
            /*nav_view.getHeaderView(0).img_profile.setOnClickListener {
                replaceFragment(
                    ImageViewerFragment.newInstance(getUserInfo().userProfilePhoto),
                    R.id.layout_home,
                    ImageViewerFragment.TAG
                )
                drawer_layout.closeDrawer(GravityCompat.START)
            }*/

        }
    }

    override fun swipeSliderEnable(flag: Boolean) {
        if (flag)
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED)
        else
            drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    private fun loadBitmap(fileName: String) {
        NSFWDetector.isNSFW(
            BitmapFactory.decodeFile(fileName),
            Utils.NSFW_CONFIDENCE_THRESHOLD.toFloat()
        ) { isNSFW, label, confidence, image ->
            if (label == Utils.LABEL_SFW) {
                Toast.makeText(this, "This is an obscene image", Toast.LENGTH_SHORT).show()
            } else if (label == Utils.LABEL_NSFW) {
                if (imageView != null) {
                    Glide.with(this)
                        .load(File(fileName)).apply(RequestOptions().optionalCircleCrop())
                        .into(imageView!!)
                } else {
                    bitmapList.add(fileName)
                }
                //Toast.makeText(this, "SFW with confidence: $confidence", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error while loading image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String {
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val loader =
                CursorLoader(this, contentUri, proj, null, null, null)
            val cursor = loader.loadInBackground()
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val result = cursor.getString(columnIndex)
            cursor.close()
            return result
        } catch (e: Exception) {
        }
        return ""
    }

    fun updateTokenSavedStatus() {
        val prefs = PreferenceHelper.defaultPrefs(this)
        prefs[PrefKeys.PREF_TokenSavedStatus] = true
    }


    fun setBottomNavigation(navigationView: BottomNavigationView?) {
        this.navigationView = navigationView
    }

    fun getBottomNavigation(): BottomNavigationView? {
        return navigationView
    }

}