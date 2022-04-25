package com.app.synchealth.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class TokenPostData(val id: String, val token: String)

data class LoginUser(val email: String, val password: String)

data class Notification(
    val id: String,
    val entryType: String,
    val entryId: String,
    val name: String,
    val description: String,
    val dateEntered: String,
    val userId: String,
    val userProfilePhoto: String,
    val readStatus: Boolean
)

@Parcelize
data class UserData(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val hno: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val email: String = "",
    val landmark: String = "",
    var addressId: String = "",
    val auth: String = "",
    val FavoritesCount: Int = 0,
    val alert: String = "",
    var userid: String = "",
    var userProfilePhoto: String = "",
    val lastname: String = "",
    val statusOption: String = "",
    val statusDate: String = "",
    val mood: String = "",
    val grattitude: String = "",
    val description: String = "",
    var action: String = "",
    val otp: String = "",
    var password: String = "",
    var isUser: Boolean = false,
    var isExpert: Boolean = false,
    var isSponsor: Boolean = false,
    val pass: String = "",
    var dateEntered: String = "",
    val discussionId: String = ""
) : Parcelable

data class SyncHealthLogin(
    val TLH01: String,
    val TLH02: String,
    val TLH03: String,
    val TLH04: String,
    val TLH05: String,
    val TLH06: String,
    val TLH07: String,
    val TLH08: String
)

data class SyncHealthAppointmentList(
    val TL101: String,
    val TL102: String,
    val TL103: String
)

data class CommonList(
    val PA101: String,
    val PA102: String
)

data class PhysicianList(
    val PA101: String
)

data class ProviderList(
    val PA101: String,
    val PA102: String
)

data class DoctorsReq(
    val PA101: String,
    val PA103: String
)

data class TimeSlot(
    val THL101: String,
    val THL102: String,
    val THL103: String,
    val THL104: String
)

data class UploadImage(
    val SH36000: String,
    val SH36001: String,
    val SH36002: String,
    val SH36003: String,
    val SH36004: String,
    val SH36005: String
)

data class ProfileData(
    val PA101: String,
    val PA102: String
)

data class InsuranceData(
    val THL101: String,
    val THL102: String,
    val THL103: String
)

data class UpdateProfileData(
    val THL121: String,
    val THL100: String,
    val THL101: String,
    val THL1011: String,
    val THL102: String,
    val THL103: String,
    val THL104: String,
    val THL105: String,
    val THL106: String,
    val THL107: String,
    val THL108: String,
    val THL109: String,
    val THL110: String,
    val THL111: String,
    val THL112: String,
    val THL113: String,
    val THL114: String,
    val THL115: String,
    val THL116: String,
    val THL117: String,
    val THL118: String,
    val THL119: String,
    val THL120: String,
    val THL122: String,
    val THL123: String,
    val p_subscriber_relationship: String
)

data class PrevConsult(
    val PA101: String,
    val PA102: String,
    val PA103: String
)

data class GetVitals(
    val THL101: String,
    val THL102: String
)

data class GetPrescription(
    val PA100: String,
    val PA101: String,
    val PA102: String
)

data class GetArticles(
    val PA101: String
)

data class GetChats(
    val PA101: String,
    val PA102: String,
    val PA103: String,
    val PA104: String
)

data class SendChat(
    val THL102: String,
    val THL103: String,
    val THL104: String,
    val THL106: String,
    val THL107: String,
    val THL108: String
)

data class SendFeedback(
    val THL101: String,
    val THL102: String,
    val THL103: String,
    val THL104: String,
    val THL105: String
)

data class CancelAppointment(
    val TLH01: String,
    val TLH02: String,
    val TLH03: String,
    val TLH04: String
)

data class CreateAppointment(
    val TLH01: String,
    val PA102: String,
    val PA103: String,
    val PA104: String,
    val PA105: String,
    val AP101: String,
    val AP102: String,
    val AP103: String,
    val AP104: String,
    val AP105: String,
    val PV101: String,
    val PA106: String,
    val PV102: String,
    val PA107: String,
    val PA108: String,
    val PA109: String,
    val PA110: String,
    val PH101: String,
    val PH102: String,
    val PH103: String,
    val PM101: String,
    val PM103: String,
    val SY104: String,
    val AL105: String,
    val ME106: String
)

data class PaymentStatus(
    val PA101: String,
    val PA102: String,
    val PA103: String,
    val PA104: String,
    val PA105: String,
    val PA106: String,
    val PA107: String,
    val PA108: String,
    val PA109: String,
    val PA110: String,
    val PA111: String,
    val PA112: String,
    val PA113: String,
    val PA115: String,
    val PA116: String,
    val PA117: String
)

data class AppointmentList(
    val consultation_charge: String,
    val service_charge: String,
    val synchealth_fee: String,
    val payment_gateway_charge: String,
    val gst: String,
    val total: String,
    val tele_token: String,
    val appt_id: String,
    val pc_eid: String,
    val meeting_mode: String,
    val provider_id: String,
    val provider_type: String,
    val located_street: String,
    val located_city: String,
    val located_state: String,
    val located_zipcode: String,
    val pc_title: String,
    val pc_eventDate: String,
    val pc_startTime: String,
    val consent: String,
    val pc_endTime: String,
    val pc_duration: String,
    val pc_apptstatus: String,
    val fname: String,
    val lname: String,
    val phonecell: String,
    val physician_type: String,
    val email: String
) : Serializable

data class CommonsList(
    val list_id: String,
    val option_id: String,
    val title: String,
    val seq: String,
    val is_default: String,
    val option_value: String,
    val mapping: String,
    val notes: String,
    val codes: String,
    val toggle_setting_1: String,
    val toggle_setting_2: String,
    val activity: String,
    val subtype: String,
    val edit_options: String,
    val timestamp: String
)

data class PrevConsultData(
    val pid: String,
    val pc_eid: String,
    val provider_id: String,
    val provider_type: String,
    val last_modifed: String,
    val located_street: String,
    val located_city: String,
    val located_state: String,
    val located_zipcode: String,
    val located_country: String,
    val tele_token: String,
    val status: String,
    val consultation_charge: String,
    val service_charge: String,
    val synchealth_fee: String,
    val gst: String,
    val payment_gateway_charge: String,
    val total: String,
    val pc_title: String,
    val pc_eventDate: String,
    val pc_apptstatus: String,
    val pc_duration: String,
    val pc_startTime: String,
    val pc_time: String,
    val pc_endTime: String,
    val fname: String,
    val lname: String,
    val physician_type: String,
    val username: String,
    val appt_id: String
)

data class DoctorsList(
    val id: String,
    val username: String,
    val fname: String,
    val mname: String,
    val lname: String,
    val facility: String,
    val email: String,
    val phonecell: String,
    val phonew1: String,
    val phonew2: String,
    val physician_type: String,
    val rating: String,
    val educational_qualification: String,
    val consultation_charge: String,
    val languages_spoken: String,
    val yrs_of_experience: String
)

data class PrevSpecialist(
    val list_id: String,
    val option_id: String,
    val title: String,
    val fname: String,
    val lname: String,
    val id: String,
    val pid: String,
    val provider_id: String,
    val appt_id: String
)


data class ProfileDetails(
    val id: String,
    val title: String,
    val language: String,
    val financial: String,
    val fname: String,
    val lname: String,
    val mname: String,
    val DOB: String,
    val street: String,
    val postal_code: String,
    val city: String,
    val state: String,
    val country_code: String,
    val ss: String,
    val occupation: String,
    val phone_home: String,
    val phone_biz: String,
    val phone_contact: String,
    val phone_cell: String,
    val pharmacy_id: String,
    val status: String,
    val contact_relationship: String,
    val date: String,
    val sex: String,
    val referrer: String,
    val referrerID: String,
    val providerID: String,
    val ref_providerID: String,
    val email: String,
    val email_direct: String,
    val ethnoracial: String,
    val race: String,
    val ethnicity: String,
    val religion: String,
    val interpretter: String,
    val migrantseasonal: String,
    val family_size: String,
    val monthly_income: String,
    val billing_note: String,
    val homeless: String,
    val financial_review: String,
    val pubpid: String,
    val pid: String,
    val type_of_license_id: String,
    val state_selection_of_license: String,
    val zip_of_license_id: String,
    val license_id_info: String,
    val hipaa_mail: String,
    val hipaa_voice: String,
    val hipaa_notice: String,
    val hipaa_message: String,
    val hipaa_allowsms: String,
    val hipaa_allowemail: String,
    val squad: String,
    val fitness: String,
    val referral_source: String,
    val usertext1: String,
    val usertext2: String,
    val usertext3: String,
    val usertext4: String,
    val usertext5: String,
    val usertext6: String,
    val usertext8: String,
    val userlist1: String,
    val userlist2: String,
    val userlist3: String,
    val userlist4: String,
    val userlist5: String,
    val userlist6: String,
    val usertext7: String,
    val pricelevel: String,
    val regdate: String,
    val contrastart: String,
    val completed_ad: String,
    val ad_reviewed: String,
    val vfc: String,
    val mothersname: String,
    val guardiansname: String,
    val allow_imm_reg_use: String,
    val allow_imm_info_share: String,
    val allow_health_info_ex: String,
    val allow_patient_portal: String,
    val deceased_date: String,
    val deceased_reason: String,
    val soap_import_status: String,
    val cmsportal_login: String,
    val care_team: String,
    val county: String,
    val industry: String,
    val imm_reg_status: String,
    val imm_reg_stat_effdate: String,
    val publicity_code: String,
    val publ_code_eff_date: String,
    val protect_indicator: String,
    val prot_indi_effdate: String,
    val guardianrelationship: String,
    val guardiansex: String,
    val guardianaddress: String,
    val guardiancity: String,
    val guardianstate: String,
    val guardianpostalcode: String,
    val guardiancountry: String,
    val guardianphone: String,
    val guardianworkphone: String,
    val guardianemail: String,
    val auth_code: String,
    val ec_Name: String,
    val EC_Phone_number: String,
    val drivers_license: String,
    val admission_date: String,
    val discharge_date: String
)

data class InsuranceDetails(
    val id: String,
    val type: String,
    val provider: String,
    val plan_name: String,
    val policy_number: String,
    val group_number: String,
    val subscriber_lname: String,
    val subscriber_mname: String,
    val subscriber_fname: String,
    val subscriber_relationship: String,
    val subscriber_ss: String,
    val subscriber_DOB: String,
    val subscriber_street: String,
    val subscriber_postal_code: String,
    val subscriber_city: String,
    val subscriber_state: String,
    val subscriber_country: String,
    val subscriber_phone: String,
    val subscriber_employer: String,
    val subscriber_employer_street: String,
    val subscriber_employer_postal_code: String,
    val subscriber_employer_state: String,
    val subscriber_employer_country: String,
    val subscriber_employer_city: String,
    val copay: String,
    val date: String,
    val pid: String,
    val subscriber_sex: String,
    val accept_assignment: String,
    val policy_type: String,
    val provider_name: String
)

data class Vitals(
    val id: String,
    val date: String,
    val pid: String,
    val user: String,
    val groupname: String,
    val authorized: String,
    val activity: String,
    val bps: String,
    val bpd: String,
    val weight: String,
    val height: String,
    val temperature: String,
    val temp_method: String,
    val pulse: String,
    val respiration: String,
    val note: String,
    val BMI: String,
    val BMI_status: String,
    val waist_circ: String,
    val head_circ: String,
    val oxygen_saturation: String,
    val external_id: String
)

data class Prescription(
    val id: String,
    val patient_id: String,
    val filled_by_id: String,
    val pharmacy_id: String,
    val date_added: String,
    val date_modified: String,
    val provider_id: String,
    val encounter: String,
    val start_date: String,
    val drug: String,
    val drug_id: String,
    val rxnorm_drugcode: String,
    val form: String,
    val dosage: String,
    val quantity: String,
    val size: String,
    val unit: String,
    val route: String,
    val interval: String,
    val substitute: String,
    val refills: String,
    val per_refill: String,
    val filled_date: String,
    val medication: String,
    val note: String,
    val active: String,
    val datetime: String,
    val user: String,
    val site: String,
    val prescriptionguid: String,
    val erx_source: String,
    val erx_uploaded: String,
    val drug_info_erx: String,
    val external_id: String,
    val end_date: String,
    val indication: String,
    val prn: String,
    val ntx: String,
    val rtx: String,
    val txDate: String,
    val appt_id: String,
)

data class Articles(
    val aid: String,
    val link: String,
    val image: String,
    val image_url: String,
    val description: String,
    val title: String,
    val feed_url: String,
    val active: String,
    val last_modofied: String,
    val category: String
)