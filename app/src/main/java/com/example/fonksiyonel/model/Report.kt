package com.example.fonksiyonel.model

import java.util.Date

data class Report(
    val id: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val diagnosisResult: DiagnosisResult? = null,
    val createdAt: Long = Date().time,
    val sharedWithDoctors: List<String> = emptyList(),
    val doctorFeedback: String? = null
)

data class DiagnosisResult(
    val cancerType: CancerType,
    val confidencePercentage: Float,
    val riskLevel: RiskLevel
)

enum class CancerType {
    BENIGN,   // İyi huylu
    MALIGNANT, // Kötü huylu (tüm kanser türlerini temsil eder)
    UNKNOWN
}

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    VERY_HIGH
}
