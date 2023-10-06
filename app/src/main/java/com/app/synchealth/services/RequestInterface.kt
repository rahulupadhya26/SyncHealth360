package com.app.synchealth.services

import com.app.synchealth.data.*
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*


interface RequestInterface {

    @POST("forgot-password.php")
    fun forgotPassword(@Body user: LoginUser): Single<SimpleResponse>

    @POST("update-profile.php")
    fun updateProfile(@Body user: UserData) : Single<SimpleResponse>

    @POST("post-notification.php")
    fun saveUserToken(@Body token: TokenPostData): Single<SimpleResponse>

    @POST("mobile_login.php")
    fun syncHealthLogin(@Body login: SyncHealthLogin) : Single<ResponseBody>

    @POST("get_multi_appt_status.php")
    fun appointmentList(@Body appointmentList: SyncHealthAppointmentList) : Single<ResponseBody>

    @POST("set_appt_status.php")
    fun cancelAppointment(@Body cancelAppointment: CancelAppointment) : Single<ResponseBody>

    @POST("get_list_by_type.php")
    fun commonList(@Body commonList: CommonList) : Single<ResponseBody>

    @POST("get_providertype.php")
    fun physicianList(@Body physicianList: PhysicianList) : Single<ResponseBody>

    @POST("get_provider_type_details.php")
    fun doctorsList(@Body doctorsList: ProviderList) : Single<ResponseBody>

    @POST("get_appointmentproviderCheck.php")
    fun prevSpecialist(@Body prevSpecialist: DoctorsReq) : Single<ResponseBody>

    @POST("get_provider_time_slot.php")
    fun getTimeSlot(@Body getTimeSlot: TimeSlot) : Single<ResponseBody>

    @POST("tele_api_new_json.php")
    fun createAppointmentApi(@Body createAppointmentApi: CreateAppointment) : Single<ResponseBody>

    @POST("insert_update_payment.php")
    fun updatePaymentStatus(@Body updatePaymentStatus: PaymentStatus) : Single<ResponseBody>

    @POST("upload_image.php")
    fun uploadImage(@Body uploadImage: UploadImage) : Single<ResponseBody>

    @POST("getpatientdata.php")
    fun getProfileData(@Body getProfileData: ProfileData) : Single<ResponseBody>

    @POST("getinsurancedata.php")
    fun getInsuranceData(@Body getInsuranceData: InsuranceData) : Single<ResponseBody>

    @POST("updatepatient.php")
    fun updateProfileData(@Body updateProfileData: UpdateProfileData) : Single<ResponseBody>

    @POST("get_appointmentlist.php")
    fun getPrevConsulationData(@Body getPrevConsulationData: PrevConsult) : Single<ResponseBody>

    @POST("get_vitals.php")
    fun getVitalInfo(@Body getVitalInfo: GetVitals) : Single<ResponseBody>

    @POST("prescription.php")
    fun getPrescriptionInfo(@Body getPrescriptionInfo: GetPrescription) : Single<ResponseBody>

    @POST("set_ratings.php")
    fun sendFeedback(@Body sendFeedback: SendFeedback) : Single<ResponseBody>

    @POST("tl_get_articles.php")
    fun getArticles(@Body getArticles: GetArticles) : Single<ResponseBody>

    @POST("getchat_update.php")
    fun getChats(@Body getChats: GetChats) : Single<ResponseBody>

    @POST("setchat.php")
    fun sendChat(@Body sendChat: SendChat) : Single<ResponseBody>

    @POST("getconfig")
    fun getConfig(@Body tcCode: TcCode) : Single<ResponseBody>

    @POST("register.php")
    fun register(@Body sendSignUpData: Register) : Single<ResponseBody>

    @POST("signin.php")
    fun signInUsingAuthCode(@Body signIn: SignInUsingAuthCode) : Single<ResponseBody>

    @POST("signin_prov.php")
    fun doctorSignIn(@Body signIn: DoctorSignIn) : Single<ResponseBody>

    @POST("get_provider.php")
    fun providerDetails(@Body signIn: ProviderDetails) : Single<ResponseBody>
}