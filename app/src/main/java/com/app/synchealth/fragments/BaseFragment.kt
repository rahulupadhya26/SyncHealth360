package com.app.synchealth.fragments


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.app.synchealth.MainActivity
import com.app.synchealth.R
import com.app.synchealth.controller.*
import com.app.synchealth.data.*
import com.app.synchealth.preference.PrefKeys
import com.app.synchealth.preference.PreferenceHelper
import com.app.synchealth.preference.PreferenceHelper.get
import com.app.synchealth.preference.PreferenceHelper.set
import com.app.synchealth.services.RequestInterface
import com.app.synchealth.utils.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import io.reactivex.disposables.CompositeDisposable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*


abstract class BaseFragment : Fragment(), IFragment, IController {
    // TODO: Rename and change types of parameters
    private var root: View? = null
    var mActivity: MainActivity? = null
    protected var mContext: Context? = null
    private var fragment: BaseFragment? = null
    var mCompositeDisposable: CompositeDisposable = CompositeDisposable()
    protected abstract fun getLayout(): Int
    private var createPostDialog: BottomSheetDialog? = null
    protected val handler: Handler = Handler()
    protected var runnable: Runnable? = null
    private var prefs: SharedPreferences? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        this.mActivity = mContext as MainActivity
        fragment = this
        prefs = PreferenceHelper.defaultPrefs(mActivity!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (root == null)
            root = inflater.inflate(getLayout(), container, false)
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        return root
    }


    override fun addFragment(fragment: Fragment, frameId: Int, fragmentName: String) {
        mActivity!!.addFragment(fragment, frameId, fragmentName)
    }

    override fun replaceFragment(fragment: Fragment, frameId: Int, fragmentName: String) {
        mActivity!!.replaceFragment(fragment, frameId, fragmentName)
    }

    @Synchronized
    fun getEncryptedRequestInterface(): RequestInterface {
        return mActivity!!.getEncryptedRequestInterface()
    }

    @Synchronized
    fun getSyncHealthRequestInterface(syncHealthUrl: String): RequestInterface {
        return mActivity!!.getSyncHealthRequestInterface(syncHealthUrl)
    }

    @Synchronized
    fun getRequestInterface(): RequestInterface {
        return mActivity!!.getRequestInterface()
    }

    override fun replaceFragmentNoBackStack(
        fragment: Fragment,
        frameId: Int,
        fragmentName: String
    ) {
        mActivity!!.replaceFragmentNoBackStack(fragment, frameId, fragmentName)
    }

    override fun popBackStack() {
        mActivity!!.popBackStack()
    }

    override fun hideKeyboard(view: View) {
        mActivity!!.hideKeyboard(view)
    }

    override fun getHeader(): View {
        return mActivity!!.getHeader()
    }

    override fun getBackButton(): View {
        return mActivity!!.getBackButton()
    }

    override fun getSubTitle(): TextView {
        return mActivity!!.getSubTitle()
    }

    override fun getTokenSavedStatus(): Boolean {
        return mActivity!!.getTokenSavedStatus()
    }

    override fun syncHealthGetConfigResponse(): String {
        return mActivity!!.syncHealthGetConfigResponse()
    }

    fun displayToast(msg: String) {
        Toast.makeText(mContext!!, msg, Toast.LENGTH_SHORT).show()
    }

    //Get editText string
    protected fun getText(editText: EditText): String {
        return editText.text.toString().trim()
    }

    //Set the error to editText
    protected fun setEditTextError(editText: EditText, errMsg: String) {
        editText.error = errMsg
        editText.requestFocus()
    }

    //Check the text is valid
    protected fun isValidText(editText: EditText): Boolean {
        val words = editText.text.toString().lowercase().split("\\s+".toRegex())
        val containsBadWords = words.firstOrNull { it in Utils.NSFW_WORDS } != null
        return editText.text.toString().trim().isNotEmpty() && !containsBadWords
    }

    //Check the Mail Id is valid
    protected fun isValidEmail(editText: EditText): Boolean {
        return !TextUtils.isEmpty(getText(editText)) && Patterns.EMAIL_ADDRESS.matcher(
            getText(
                editText
            )
        ).matches()
    }

    //Get User Information
    override fun getUserInfo(): UserData {
        return mActivity!!.getUserInfo()
    }

    protected fun updateUserInfo(user: ProfileDetails, pass: String) {
        val prefs = PreferenceHelper.defaultPrefs(mActivity!!)
        prefs[PrefKeys.PREF_IS_LOGIN] = true
        prefs[PrefKeys.PREF_ID] = user.id
        prefs[PrefKeys.PREF_Name] = user.fname
        prefs[PrefKeys.PREF_Last_Name] = user.lname
        prefs[PrefKeys.PREF_Email] = user.email
        prefs[PrefKeys.PREF_Hno] = user.phone_home
        prefs[PrefKeys.PREF_Mobile] = user.phone_cell
        prefs[PrefKeys.PREF_City] = user.city
        prefs[PrefKeys.PREF_State] = user.state
        prefs[PrefKeys.PREF_Pincode] = user.postal_code
        prefs[PrefKeys.PREF_Landmark] = user.street
        prefs[PrefKeys.PREF_Pass] = pass
    }


    protected fun updateUserPhoto(photo: String) {
        val prefs = PreferenceHelper.defaultPrefs(mActivity!!)
        prefs[PrefKeys.PREF_Pic] = photo
        mActivity!!.setUserName()
    }

    override fun showProgress() {
        mActivity!!.showProgress()
    }

    fun showPlainProgress() {
        mActivity!!.showPlainProgress()
    }

    fun showWhiteProgress() {
        mActivity!!.showWhiteProgress()
    }

    override fun hideProgress() {
        mActivity!!.hideProgress()
    }

    fun getBitmapListMultiParts(): List<MultipartBody.Part> {
        val parts = ArrayList<MultipartBody.Part>()
        var i = 0
        for (item in getBitmapList()) {
            try {
                parts.add(prepareImagePart("bitmapImage$i", item))
                i += 1
            } catch (e: Exception) {
                Log.i("exception", e.message!!)
            }

        }
        return parts
    }

    override fun getPhotoPath(): String {
        return mActivity!!.getPhotoPath()
    }

    fun resetProfileImageView() {
        mActivity!!.resetProfileImageView()
    }

    fun getMultiParts(): List<MultipartBody.Part> {
        val parts = ArrayList<MultipartBody.Part>()
        if (getPhotoPath().isNotEmpty())
            parts.add(prepareImagePart("bitmapImage0", getPhotoPath()))
        return parts
    }

    fun getBitmapMultiParts(bitmap: Bitmap): List<MultipartBody.Part> {
        val parts = ArrayList<MultipartBody.Part>()
        parts.add(prepareBitmapImagePart("bitmapImage0", bitmap))
        return parts
    }

    fun redirectToHome() {
        setBottomMenu(R.id.navigation_home)
        popBackStack()
    }

    private fun prepareImagePart(name: String, filePath: String): MultipartBody.Part {
        val file = File(filePath)
        val requestBody = RequestBody.create(
            "image/*".toMediaTypeOrNull(),
            imageToBitmap(BitmapFactory.decodeFile(file.path))
        )
        return MultipartBody.Part.createFormData(name, file.name, requestBody)
    }

    private fun prepareBitmapImagePart(name: String, bitmap: Bitmap): MultipartBody.Part {
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageToBitmap(bitmap))
        return MultipartBody.Part.createFormData(
            name,
            System.currentTimeMillis().toString() + ".jpg",
            requestBody
        )
    }

    fun getVideoMultiPart(filePath: String): MultipartBody.Part {
        val file = File(filePath)
        val requestBody = file.asRequestBody("*/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("video", file.name, requestBody)
    }

    private fun imageToBitmap(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        return stream.toByteArray()
    }

    override fun captureImage() {
        mActivity!!.captureImage()
    }


    fun onSingleChoiceClick(selected: String, bitmap: Bitmap) {
        if (selected.equals("Feed")) {
            /*FeedOps.post(
                Feed(
                    id = getUserInfo().id,
                    description = "",
                    entryType = Utils.CONST_FEED
                ), this, bitmap, true
            )*/
        } else {
            val dir = File(Environment.getExternalStorageDirectory(), "DiscoverTx")
            if (!dir.exists()) {
                dir.mkdir()
            }
            val imgFile = File(dir, "Badge.png")
            val fos: FileOutputStream
            try {
                fos = FileOutputStream(imgFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()
                Log.e("ImageSave", "Saveimage")
            } catch (e: FileNotFoundException) {
                Log.e("GREC", e.message, e)
            } catch (e: IOException) {
                Log.e("GREC", e.message, e)
            }
            val file = Uri.fromFile(imgFile)
            val shareIntent = Intent(Intent.ACTION_SEND)

            shareIntent.type = "image/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, file)
            shareIntent.putExtra(Intent.EXTRA_TITLE, "I have earn badge")
            if (selected.equals("Facebook")) {
                shareIntent.setPackage("com.facebook.katana")
            } else {
                shareIntent.setPackage("com.instagram.android")
            }
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(shareIntent)

        }
    }

    protected fun captureImage(imageView: ImageView) {
        mActivity!!.captureImage(imageView)
    }

    override fun uploadPicture() {
        mActivity!!.uploadPicture()
    }

    override fun uploadVideo() {
        mActivity!!.uploadVideo()
    }

    override fun clearTempFormData() {
        mActivity!!.clearTempFormData()
    }

    override fun getBitmapList(): ArrayList<String> {
        return mActivity!!.getBitmapList()
    }

    override fun swipeSliderEnable(flag: Boolean) {
        mActivity!!.swipeSliderEnable(flag)
    }

    fun setBottomNavigation(navigationView: BottomNavigationView?) {
        mActivity!!.setBottomNavigation(navigationView)
    }

    fun getBottomNavigation(): BottomNavigationView? {
        return mActivity!!.getBottomNavigation()
    }

    override fun setBottomMenu(id: Int) {
        mActivity!!.setBottomMenu(id)
    }

    override fun sendEventLog(key: String, value: String) {
        mActivity!!.sendEventLog(getUserInfo().name, value)
    }

    override fun detectImage(image: Bitmap, confiThreshold: Float): Boolean {
        return mActivity!!.detectImage(image, confiThreshold)
    }

    override fun onPause() {
        super.onPause()
        if (runnable != null) {
            handler.removeCallbacks(runnable!!)
        }
        if (mCompositeDisposable.size() >= 1) {
            mCompositeDisposable.clear()
        }
        hideProgress()
    }

    override fun isLoggedIn(): Boolean {
        return mActivity!!.isLoggedIn()
    }

    protected fun updateSyncHealthConfigResponse(response: String) {
        val prefs = PreferenceHelper.defaultPrefs(mActivity!!)
        prefs[PrefKeys.PREF_Sync_Health_Config_Response] = response
    }

    protected fun updateSyncHealthPatientId(response: String) {
        val prefs = PreferenceHelper.defaultPrefs(mActivity!!)
        prefs[PrefKeys.PREF_Sync_Health_Patient_Id] = response
    }

    override fun syncHealthGetPatientId(): String {
        return mActivity!!.syncHealthGetPatientId()
    }

    protected fun updateSyncHealthToken(response: String) {
        val prefs = PreferenceHelper.defaultPrefs(mActivity!!)
        prefs[PrefKeys.PREF_Sync_Health_Token] = response
    }

    override fun syncHealthGetToken(): String {
        return mActivity!!.syncHealthGetToken()
    }

    protected fun updateProfileInfo(response: String) {
        val prefs = PreferenceHelper.defaultPrefs(mActivity!!)
        prefs[PrefKeys.PREF_Sync_Health_Profile_Info] = response
    }

    override fun profileInfo(): String {
        return mActivity!!.profileInfo()
    }

    protected fun updateProviderId(response: String) {
        prefs!![PrefKeys.PREF_ProviderId] = response
    }

    fun getProviderId(): String {
        return prefs!![PrefKeys.PREF_ProviderId, ""]!!
    }

    protected fun saveProviderDetails(response: String) {
        prefs!![PrefKeys.PREF_ProviderDetails] = response
    }

    fun getProviderDetails(): DoctorProfileDetails {
        val providerData = prefs!![PrefKeys.PREF_ProviderDetails, ""]!!
        val gson = GsonBuilder().create()
        return gson.fromJson(providerData, DoctorProfileDetails::class.java)
    }

    override fun displayIntroScreen(): Boolean {
        return mActivity!!.displayIntroScreen()
    }

    protected fun updateDisplayIntroScreen(response: Boolean) {
        val prefs = PreferenceHelper.defaultPrefs(mActivity!!)
        prefs[PrefKeys.PREF_Intro_screen] = response
    }

    protected fun getConfigData(): ConfigData {
        val prefs = PreferenceHelper.defaultPrefs(mActivity!!)
        val configData = prefs[PrefKeys.PREF_Config_Data, ""]!!
        val gson = GsonBuilder().create()
        return gson.fromJson(configData, ConfigData::class.java)
    }

    protected fun saveConfigData(response: String) {
        val prefs = PreferenceHelper.defaultPrefs(mActivity!!)
        prefs[PrefKeys.PREF_Config_Data] = response
    }

    protected fun getUserName(): String {
        val prefs = PreferenceHelper.defaultPrefs(mActivity!!)
        return prefs[PrefKeys.PREF_Username, ""]!!
    }

    protected fun saveUserName(response: String) {
        val prefs = PreferenceHelper.defaultPrefs(mActivity!!)
        prefs[PrefKeys.PREF_Username] = response
    }

    override fun clearCache() {
        mActivity!!.clearCache()
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun getImageUri(inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(mActivity!!.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun checkErrorCode(response: String): Boolean {
        var isValidResp = true
        when {
            response.contains(Utils.CONST_NO_RECORDS) -> {
                isValidResp = false
                displayToast("No records found")
            }
            response.contains(Utils.CONST_SQL_ERROR) -> {
                isValidResp = false
                displayToast("Something went wrong. Please try again.. 1005")
            }
            response.contains(Utils.CONST_SUCCESS) -> {
                isValidResp = true
            }
            response.contains(Utils.CONST_FAIL) -> {
                isValidResp = false
                displayToast("Something went wrong. Please try again..  201")
            }
            response.contains(Utils.CONST_INVALID_TOKEN) -> {
                isValidResp = false
                displayToast("Something went wrong. Please try again..  207")
                clearCache()
            }
            response.contains(Utils.CONST_INVALID_REQ) -> {
                isValidResp = false
                displayToast("Something went wrong. Please try again..  205")
            }
            response.contains(Utils.CONST_INVALID_INPUTS) -> {
                isValidResp = false
                displayToast("Something went wrong. Please try again..  204")
            }
            response.contains(Utils.CONST_USR_PASS_INVALID) -> {
                isValidResp = false
                displayToast("Invalid username or password")
            }
            response.contains(Utils.CONST_PASS_NOT_MATCH) -> {
                isValidResp = false
                displayToast("Password does not match")
            }
            response.contains(Utils.CONST_USER_NOT_FOUND) -> {
                isValidResp = false
                displayToast("User not found")
            }
        }
        return isValidResp
    }
}
