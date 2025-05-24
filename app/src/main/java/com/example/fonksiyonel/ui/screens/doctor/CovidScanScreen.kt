package com.example.fonksiyonel.ui.screens.doctor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.fonksiyonel.R
import com.example.fonksiyonel.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CovidScanScreen(
    onNavigateBack: () -> Unit,
    onScanComplete: (CovidReport) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var diagnosisResult by remember { mutableStateOf<CovidDiagnosisResult?>(null) }
    var patientName by remember { mutableStateOf("") }
    var patientId by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            diagnosisResult = null  // Reset previous result
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("COVID-19 Taraması") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Geri"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Heading
            Text(
                text = "COVID-19 Akciğer X-Ray Taraması",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Image Selection
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable(enabled = !isAnalyzing) { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(selectedImageUri),
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_photo),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Akciğer X-Ray görüntüsü seçmek için tıklayın",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                if (isAnalyzing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Patient Info Fields
            OutlinedTextField(
                value = patientName,
                onValueChange = { patientName = it },
                label = { Text("Hasta Adı") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isAnalyzing
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = patientId,
                onValueChange = { patientId = it },
                label = { Text("Hasta ID") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isAnalyzing
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Analyze Button
            Button(
                onClick = {
                    if (selectedImageUri != null) {
                        isAnalyzing = true
                        
                        coroutineScope.launch {
                            // Here we would use the real classifier
                            // For now, simulate the analysis with a delay
                            delay(2000)
                            
                            val covidClassifier = CovidClassifier(context)
                            diagnosisResult = selectedImageUri?.let { covidClassifier.classifyImage(it) }
                            covidClassifier.close()
                            
                            isAnalyzing = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedImageUri != null && !isAnalyzing
            ) {
                Text("Taramayı Başlat")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Results Section
            if (diagnosisResult != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (diagnosisResult?.covidStatus) {
                            CovidStatus.NORMAL -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                            CovidStatus.COVID_19 -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tarama Sonucu",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Durum:")
                            Text(
                                text = when (diagnosisResult?.covidStatus) {
                                    CovidStatus.NORMAL -> "Normal"
                                    CovidStatus.COVID_19 -> "COVID-19 Pozitif"
                                    else -> "Bilinmiyor"
                                },
                                fontWeight = FontWeight.Bold,
                                color = when (diagnosisResult?.covidStatus) {
                                    CovidStatus.NORMAL -> MaterialTheme.colorScheme.tertiary
                                    CovidStatus.COVID_19 -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Confidence
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Güven Oranı:")
                            Text(
                                text = "${(diagnosisResult?.confidencePercentage?.times(100))?.toInt()}%",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Severity
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Şiddet Seviyesi:")
                            Text(
                                text = when (diagnosisResult?.severityLevel) {
                                    SeverityLevel.NONE -> "Yok"
                                    SeverityLevel.MILD -> "Hafif"
                                    SeverityLevel.MODERATE -> "Orta"
                                    SeverityLevel.SEVERE -> "Ciddi"
                                    else -> "Bilinmiyor"
                                },
                                fontWeight = FontWeight.Bold,
                                color = when (diagnosisResult?.severityLevel) {
                                    SeverityLevel.NONE -> MaterialTheme.colorScheme.tertiary
                                    SeverityLevel.MILD -> Color(0xFF4CAF50)
                                    SeverityLevel.MODERATE -> Color(0xFFFFA000)
                                    SeverityLevel.SEVERE -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Doctor Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Doktor Notları") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    enabled = !isAnalyzing
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Save Report Button
                Button(
                    onClick = {
                        if (selectedImageUri != null && diagnosisResult != null) {
                            val report = CovidReport(
                                id = UUID.randomUUID().toString(),
                                userId = patientId.ifEmpty { "unknown_patient" },
                                doctorId = "current_doctor_id", // In a real app, get from authentication
                                imageUrl = selectedImageUri.toString(),
                                diagnosisResult = diagnosisResult,
                                doctorNotes = notes.ifEmpty { null }
                            )
                            onScanComplete(report)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = diagnosisResult != null && !isAnalyzing
                ) {
                    Text("Raporu Kaydet")
                }
            }
        }
    }
}
