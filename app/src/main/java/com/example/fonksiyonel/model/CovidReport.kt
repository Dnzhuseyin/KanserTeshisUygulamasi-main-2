package com.example.fonksiyonel.model

import java.util.Date

data class CovidReport(
    val id: String = "",
    val userId: String = "",
    val doctorId: String = "",
    val imageUrl: String = "",
    val diagnosisResult: CovidDiagnosisResult? = null,
    val createdAt: Long = Date().time,
    val patientFeedback: String? = null,
    val doctorNotes: String? = null
)
