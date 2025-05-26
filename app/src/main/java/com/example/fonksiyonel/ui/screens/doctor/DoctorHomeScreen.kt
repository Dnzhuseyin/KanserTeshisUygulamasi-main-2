package com.example.fonksiyonel.ui.screens.doctor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.fonksiyonel.R
import com.example.fonksiyonel.model.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorHomeScreen(
    currentUser: User,
    onNavigateToReportDetail: (String) -> Unit,
    onNavigateToCovidScan: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogout: () -> Unit
) {
    // In a real app, this would come from a ViewModel
    val pendingAppointments = remember {
        listOf(
            Appointment(
                id = "appointment1",
                patientId = "user1",
                doctorId = "doctor123",
                date = System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000, // 2 gün sonra
                description = "Cilt lezyonu kontrolü",
                status = AppointmentStatus.PENDING
            ),
            Appointment(
                id = "appointment2",
                patientId = "user2",
                doctorId = "doctor123",
                date = System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000, // 5 gün sonra
                description = "Periyodik kontrol",
                status = AppointmentStatus.PENDING
            )
        )
    }
    
    val patientReports = remember {
        listOf(
            Report(
                id = "report1",
                userId = "user1",
                imageUrl = "",
                diagnosisResult = DiagnosisResult(
                    cancerType = CancerType.BENIGN,
                    confidencePercentage = 0.92f,
                    riskLevel = RiskLevel.LOW
                ),
                createdAt = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
                sharedWithDoctors = listOf("doctor123"),
                doctorFeedback = null
            ),
            Report(
                id = "report2",
                userId = "user2",
                imageUrl = "",
                diagnosisResult = DiagnosisResult(
                    cancerType = CancerType.MALIGNANT,
                    confidencePercentage = 0.78f,
                    riskLevel = RiskLevel.HIGH
                ),
                createdAt = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000,
                sharedWithDoctors = listOf("doctor123"),
                doctorFeedback = null
            ),
            Report(
                id = "report3",
                userId = "user3",
                imageUrl = "",
                diagnosisResult = DiagnosisResult(
                    cancerType = CancerType.MALIGNANT,
                    confidencePercentage = 0.85f,
                    riskLevel = RiskLevel.MEDIUM
                ),
                createdAt = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000,
                sharedWithDoctors = listOf("doctor123"),
                doctorFeedback = "Hastanın durumu takip edilmeli. 1 ay sonra kontrol önerilir."
            )
        )
    }
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Çıkış Yap") },
            text = { Text("Hesabınızdan çıkış yapmak istediğinize emin misiniz?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Evet")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("İptal")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doktor Paneli") },
                actions = {
                    // Settings Button
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = "Ayarlar"
                        )
                    }
                    
                    // Logout Button
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logout),
                            contentDescription = "Çıkış Yap"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Doctor Profile Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Photo
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            if (currentUser.profilePhotoUrl != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(currentUser.profilePhotoUrl),
                                    contentDescription = "Profile Photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = currentUser.name.first().toString(),
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Doctor Info
                        Column {
                            Text(
                                text = currentUser.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = "Doktor",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            
            // Pending Appointments Section
            item {
                Text(
                    text = "Yaklaşan Randevular",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            if (pendingAppointments.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Bekleyen randevunuz bulunmamaktadır.",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                items(pendingAppointments) { appointment ->
                    AppointmentItem(appointment = appointment)
                }
            }
            
            // Patient Reports Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Hasta Raporları",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            if (patientReports.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Hasta raporu bulunmamaktadır.",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            } else {
                items(patientReports) { report ->
                    PatientReportItem(
                        report = report,
                        onClick = { onNavigateToReportDetail(report.id) }
                    )
                }
            }
            
            // Add New Analysis Button
            item {
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onNavigateToCovidScan,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_scan),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Yeni Cilt Analizi",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppointmentItem(appointment: Appointment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Randevu detayına gitmek için */ },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Date and Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(appointment.date)),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(appointment.date)),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Patient Name - This would come from a database in a real app
            Text(
                text = "Hasta: ${appointment.patientId}", // Bu gerçek bir uygulama olsaydı hasta adı gösterilirdi
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Description
            Text(
                text = appointment.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Status Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        when (appointment.status) {
                            AppointmentStatus.PENDING -> Color(0xFFFFA000)
                            AppointmentStatus.CONFIRMED -> MaterialTheme.colorScheme.tertiary
                            AppointmentStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                            AppointmentStatus.CANCELLED -> MaterialTheme.colorScheme.error
                        }
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = when (appointment.status) {
                        AppointmentStatus.PENDING -> "Bekliyor"
                        AppointmentStatus.CONFIRMED -> "Onaylandı"
                        AppointmentStatus.COMPLETED -> "Tamamlandı"
                        AppointmentStatus.CANCELLED -> "İptal Edildi"
                    },
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun PatientReportItem(
    report: Report,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Date and Patient ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Hasta: ${report.userId}", // Bu gerçek bir uygulama olsaydı hasta adı gösterilirdi
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(report.createdAt)),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Report Preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image Preview
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (report.imageUrl.isNotEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(report.imageUrl),
                            contentDescription = "Report Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_image),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Diagnosis Result
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = when (report.diagnosisResult?.cancerType) {
                            CancerType.BENIGN -> "İyi Huylu (Benign)"
                            CancerType.MALIGNANT -> "Kötü Huylu (Malignant)"
                            else -> "Bilinmiyor"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Güven: ${(report.diagnosisResult?.confidencePercentage?.times(100))?.toInt()}%",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Risk Level
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (report.diagnosisResult?.riskLevel) {
                                    RiskLevel.LOW -> MaterialTheme.colorScheme.tertiary
                                    RiskLevel.MEDIUM -> Color(0xFFFFA000)
                                    RiskLevel.HIGH -> Color(0xFFF57C00)
                                    RiskLevel.VERY_HIGH -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when (report.diagnosisResult?.riskLevel) {
                                RiskLevel.LOW -> "Düşük Risk"
                                RiskLevel.MEDIUM -> "Orta Risk"
                                RiskLevel.HIGH -> "Yüksek Risk"
                                RiskLevel.VERY_HIGH -> "Çok Yüksek Risk"
                                else -> "Bilinmiyor"
                            },
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
