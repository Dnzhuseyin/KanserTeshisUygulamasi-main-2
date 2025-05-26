package com.example.fonksiyonel.ui.screens.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fonksiyonel.R
import com.example.fonksiyonel.model.Badge
import com.example.fonksiyonel.model.User
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesScreen(
    currentUser: User,
    scanCount: Int,
    onNavigateBack: () -> Unit
) {
    // Kullanıcının kazandığı rozetleri hesaplıyoruz
    val earnedBadges = remember(currentUser, scanCount) {
        val badgesList = mutableListOf<Badge>()
        
        // İlk tarama rozeti - en az 1 tarama yapıldığında kazanılır
        if (scanCount >= 1) {
            badgesList.add(Badge(
                id = "badge1",
                title = "İlk Tarama",
                description = "İlk taramanızı gerçekleştirdiniz. Sağlığınıza önem verdiğiniz için tebrikler!",
                earnedDate = System.currentTimeMillis()
            ))
        }
        
        // 5 Tarama rozeti
        if (scanCount >= 5) {
            badgesList.add(Badge(
                id = "badge2",
                title = "5 Tarama",
                description = "5 tarama gerçekleştirerek düzenli kontrol alışkanlığı kazandınız.",
                earnedDate = System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000
            ))
        }
        
        // 10 Tarama rozeti
        if (scanCount >= 10) {
            badgesList.add(Badge(
                id = "badge3",
                title = "10 Tarama",
                description = "10 tarama gerçekleştirerek sağlık takibinde uzmanlaştınız!",
                earnedDate = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000
            ))
        }
        
        // Düzenli kullanıcı rozeti - 10'dan fazla tarama yapıldığında
        if (scanCount > 10) {
            badgesList.add(Badge(
                id = "badge4",
                title = "Düzenli Kullanıcı",
                description = "Uygulamayı düzenli olarak kullandınız. Sağlığınız için gösterdiğiniz özen takdire şayan!",
                earnedDate = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000
            ))
        }
        
        // Ek rozetleri de ekleyebiliriz
        // Örneğin ayda bir kez tarama yapan kullanıcılar için özel bir rozet
        if (scanCount > 15) {
            badgesList.add(Badge(
                id = "badge5",
                title = "Uzman Kullanıcı",
                description = "Uygulama kullanımında uzmanlaştınız!",
                earnedDate = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000
            ))
        }
        
        badgesList
    }
    
    // Henüz kazanılmamış rozetler
    val lockedBadges = remember(earnedBadges) {
        val badgesList = mutableListOf<Badge>()
        
        // İlk tarama rozeti kontrolü
        if (earnedBadges.none { it.id == "badge1" }) {
            badgesList.add(Badge(
                id = "badge1",
                title = "İlk Tarama",
                description = "İlk taramanızı gerçekleştirin",
                earnedDate = 0
            ))
        }
        
        // 5 Tarama rozeti kontrolü
        if (earnedBadges.none { it.id == "badge2" }) {
            badgesList.add(Badge(
                id = "badge2",
                title = "5 Tarama",
                description = "5 tarama gerçekleştirin",
                earnedDate = 0
            ))
        }
        
        // 10 Tarama rozeti kontrolü
        if (earnedBadges.none { it.id == "badge3" }) {
            badgesList.add(Badge(
                id = "badge3",
                title = "10 Tarama",
                description = "10 tarama gerçekleştirin",
                earnedDate = 0
            ))
        }
        
        // Düzenli kullanıcı rozeti kontrolü
        if (earnedBadges.none { it.id == "badge4" }) {
            badgesList.add(Badge(
                id = "badge4",
                title = "Düzenli Kullanıcı",
                description = "30 gün boyunca uygulamayı düzenli kullanın",
                earnedDate = 0
            ))
        }
        
        badgesList
    }
    
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }
    
    // Seçilen rozet detayları için dialog
    selectedBadge?.let { badge ->
        AlertDialog(
            onDismissRequest = { selectedBadge = null },
            title = { Text(badge.title) },
            text = {
                Column {
                    Text(badge.description)
                    
                    if (badge.earnedDate > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Kazanıldı: ${
                                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                    .format(Date(badge.earnedDate))
                            }",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { selectedBadge = null }) {
                    Text("Tamam")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rozetlerim") },
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
                .padding(16.dp)
        ) {
            // Stats Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Rozet İstatistikleri",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Kazanılan Rozetler
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${earnedBadges.size}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Kazanılan",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        
                        // Toplam Tarama Sayısı
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$scanCount",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Toplam Tarama",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        
                        // Kalan Rozetler
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${lockedBadges.size}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Kalan",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            
            // Earned Badges
            Text(
                text = "Kazanılan Rozetler",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Start
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(earnedBadges) { badge ->
                    BadgeItem(
                        badge = badge,
                        isLocked = false,
                        onClick = { selectedBadge = badge }
                    )
                }
            }
            
            // Locked Badges
            Text(
                text = "Kilitli Rozetler",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Start
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(lockedBadges) { badge ->
                    BadgeItem(
                        badge = badge,
                        isLocked = true,
                        onClick = { selectedBadge = badge }
                    )
                }
            }
        }
    }
}

@Composable
fun BadgeItem(
    badge: Badge,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Badge Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        if (isLocked)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        else
                            MaterialTheme.colorScheme.primary
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isLocked) R.drawable.ic_lock else R.drawable.ic_badge
                    ),
                    contentDescription = null,
                    tint = if (isLocked)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else
                        Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Badge Title
            Text(
                text = badge.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isLocked)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
