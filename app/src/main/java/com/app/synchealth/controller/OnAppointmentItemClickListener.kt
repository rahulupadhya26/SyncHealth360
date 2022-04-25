package com.app.synchealth.controller

import com.app.synchealth.data.*

interface OnAppointmentItemClickListener {
    fun OnAppointmentItemClickListener(event: Int, appointmentList: AppointmentList)
}

interface OnDoctorItemClickListener {
    fun OnDoctorItemClickListener(doctorList: DoctorsList)
}

interface OnPrevSpecialistItemClickListener {
    fun OnPrevSpecialistItemClickListener(prevSpecialist: PrevSpecialist)
}

interface OnSuggestedVideoClickListener {
    fun OnSuggestedVideoClickListener(video: String)
}

interface OnQuickOptionClickListener {
    fun OnQuickOptionClickListener(option: Int)
}