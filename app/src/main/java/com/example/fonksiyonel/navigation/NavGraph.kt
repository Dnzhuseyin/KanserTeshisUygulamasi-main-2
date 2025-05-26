package com.example.fonksiyonel.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fonksiyonel.model.Badge
import com.example.fonksiyonel.model.User
import com.example.fonksiyonel.model.UserType
import com.example.fonksiyonel.ui.screens.appointment.AppointmentScreen
import com.example.fonksiyonel.ui.screens.auth.LoginScreen
import com.example.fonksiyonel.ui.screens.auth.RegisterScreen
import com.example.fonksiyonel.ui.screens.badges.BadgesScreen
import com.example.fonksiyonel.ui.screens.doctor.CovidScanScreen
import com.example.fonksiyonel.ui.screens.doctor.DoctorHomeScreen
import com.example.fonksiyonel.ui.screens.home.HomeScreen
import com.example.fonksiyonel.ui.screens.reports.ReportDetailScreen
import com.example.fonksiyonel.ui.screens.reports.ReportHistoryScreen
import com.example.fonksiyonel.ui.screens.scan.ScanScreen
import com.example.fonksiyonel.ui.screens.settings.SettingsScreen
import com.example.fonksiyonel.ui.screens.share.ShareWithDoctorScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object DoctorHome : Screen("doctor_home")
    object ReportHistory : Screen("report_history")
    object ReportDetail : Screen("report_detail/{reportId}") {
        fun createRoute(reportId: String) = "report_detail/$reportId"
    }
    object Scan : Screen("scan")
    object CovidScan : Screen("covid_scan")
    object ShareWithDoctor : Screen("share_with_doctor/{reportId}") {
        fun createRoute(reportId: String) = "share_with_doctor/$reportId"
    }
    object Appointment : Screen("appointment")
    object Badges : Screen("badges")
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    // Oturum açmış kullanıcı bilgisi için state tutuyoruz
    val currentUser = remember { 
        mutableStateOf<User?>(null) 
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = { userType, email, displayName ->
                    // Kullanıcı giriş yaptığında kullanıcı bilgilerini oluştur
                    // Artık girişte girilen bilgileri kullanıyoruz
                    val user = User(
                        id = "user_${System.currentTimeMillis()}",
                        name = if (userType == UserType.DOCTOR) "Dr. $displayName" else displayName,
                        email = email,
                        userType = userType,
                        profilePhotoUrl = null,
                        points = if (userType == UserType.PATIENT) 120 else 0,
                        badges = if (userType == UserType.PATIENT) {
                            listOf(
                                Badge(
                                    id = "badge1",
                                    title = "İlk Tarama",
                                    description = "İlk taramanızı gerçekleştirdiniz",
                                    earnedDate = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
                                )
                            )
                        } else emptyList(),
                        lastAnalysisDate = if (userType == UserType.PATIENT) System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000 else null
                    )
                    
                    // Kullanıcı state'ini güncelle
                    currentUser.value = user
                    
                    if (userType == UserType.DOCTOR) {
                        navController.navigate(Screen.DoctorHome.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = { userType, email, name, profileImageUri ->
                    // Kayıt olan kullanıcı için bilgiler oluşturulur
                    // Artık kayıt ekranından gelen gerçek bilgileri kullanıyoruz
                    val user = User(
                        id = "user_${System.currentTimeMillis()}",
                        name = if (userType == UserType.DOCTOR) "Dr. $name" else name,
                        email = email,
                        userType = userType,
                        profilePhotoUrl = profileImageUri,
                        points = if (userType == UserType.PATIENT) 120 else 0,
                        badges = if (userType == UserType.PATIENT) {
                            listOf(
                                Badge(
                                    id = "badge1",
                                    title = "İlk Tarama",
                                    description = "İlk taramanızı gerçekleştirdiniz",
                                    earnedDate = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
                                )
                            )
                        } else emptyList(),
                        lastAnalysisDate = if (userType == UserType.PATIENT) System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000 else null
                    )
                    
                    // Kullanıcı state'ini güncelle
                    currentUser.value = user
                    
                    if (userType == UserType.DOCTOR) {
                        navController.navigate(Screen.DoctorHome.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            // Kullanıcı bilgisi yoksa varsayılan oluşturulur (normalde bu duruma düşmemeli)
            val user = currentUser.value ?: User(
                id = "default_user",
                name = "Ahmet Yılmaz",
                email = "ahmet@example.com",
                userType = UserType.PATIENT
            )
            
            HomeScreen(
                currentUser = user,
                onNavigateToReportHistory = {
                    navController.navigate(Screen.ReportHistory.route)
                },
                onNavigateToScan = {
                    navController.navigate(Screen.Scan.route)
                },
                onNavigateToAppointment = {
                    navController.navigate(Screen.Appointment.route)
                },
                onNavigateToBadges = {
                    navController.navigate(Screen.Badges.route)
                },
                onNavigateToReportDetail = { reportId ->
                    navController.navigate(Screen.ReportDetail.createRoute(reportId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onLogout = {
                    // Çıkış yapıldığında kullanıcı bilgisi sıfırlanır
                    currentUser.value = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.DoctorHome.route) {
            // Kullanıcı bilgisi yoksa varsayılan oluşturulur (normalde bu duruma düşmemeli)
            val user = currentUser.value ?: User(
                id = "default_doctor",
                name = "Dr. Mehmet Demir",
                email = "dr.mehmet@example.com",
                userType = UserType.DOCTOR
            )
            
            DoctorHomeScreen(
                currentUser = user,
                onNavigateToReportDetail = { reportId ->
                    navController.navigate(Screen.ReportDetail.createRoute(reportId))
                },
                onNavigateToCovidScan = {
                    navController.navigate(Screen.CovidScan.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onLogout = {
                    // Çıkış yapıldığında kullanıcı bilgisi sıfırlanır
                    currentUser.value = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.DoctorHome.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.CovidScan.route) {
            CovidScanScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onScanComplete = { covidReport ->
                    // Gerçek uygulamada burada raporu veritabanına kaydedip
                    // belki bir detay sayfasına yönlendirme yapılabilir
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.ReportHistory.route) {
            ReportHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToReportDetail = { reportId ->
                    navController.navigate(Screen.ReportDetail.createRoute(reportId))
                }
            )
        }
        
        composable(Screen.ReportDetail.route) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
            ReportDetailScreen(
                reportId = reportId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToShareWithDoctor = { id ->
                    navController.navigate(Screen.ShareWithDoctor.createRoute(id))
                }
            )
        }
        
        composable(Screen.Scan.route) {
            ScanScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onScanComplete = { reportId ->
                    navController.navigate(Screen.ReportDetail.createRoute(reportId)) {
                        popUpTo(Screen.Scan.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.ShareWithDoctor.route) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
            ShareWithDoctorScreen(
                reportId = reportId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onShareComplete = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Appointment.route) {
            AppointmentScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAppointmentBooked = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Badges.route) {
            // Kullanıcı bilgisi yoksa varsayılan oluşturulur
            val user = currentUser.value ?: User(
                id = "default_user",
                name = "Ahmet Yılmaz",
                email = "ahmet@example.com",
                userType = UserType.PATIENT
            )
            
            // Tarama sayısı - gerçek uygulamada veritabanından alınır
            val scanCount = 5
            
            BadgesScreen(
                currentUser = user,
                scanCount = scanCount,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Settings.route) {
            // Kullanıcı bilgisi yoksa varsayılan oluşturulur
            val user = currentUser.value ?: User(
                id = "default_user",
                name = "Ahmet Yılmaz",
                email = "ahmet@example.com",
                userType = UserType.PATIENT
            )
            
            SettingsScreen(
                currentUser = user,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    // Çıkış yapıldığında kullanıcı bilgisi sıfırlanır
                    currentUser.value = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Settings.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
