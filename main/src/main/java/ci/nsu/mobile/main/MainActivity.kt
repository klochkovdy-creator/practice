package ci.nsu.mobile.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ci.nsu.mobile.main.data.repository.DepositRepository
import ci.nsu.mobile.main.data.token.TokenManager
import ci.nsu.mobile.main.di.ServiceLocator
import ci.nsu.mobile.main.ui.screens.*
import ci.nsu.mobile.main.ui.theme.PracticeTheme
import ci.nsu.mobile.main.ui.viewmodels.DepositViewModel
import ci.nsu.mobile.main.ui.viewmodels.UsersViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        TokenManager.init(applicationContext)
        ServiceLocator.init(applicationContext)
        setContent {
            PracticeTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val startDestination = if (TokenManager.token != null) "main" else "login"
    val depositRepository = ServiceLocator.depositRepository

    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") { popUpTo("login") { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("login") { popUpTo("register") { inclusive = true } }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable("main") {
            MainScreenWithBottomNav(
                onGlobalLogout = {
                    TokenManager.clear()
                    navController.navigate("login") { popUpTo("main") { inclusive = true } }
                },
                depositRepository = depositRepository,
                navController = navController
            )
        }

        composable(
            "userDetail/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            UserDetailScreen(
                userId = userId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MainScreenWithBottomNav(
    onGlobalLogout: () -> Unit,
    depositRepository: DepositRepository,
    navController: androidx.navigation.NavController
) {
    val tabs = listOf("Пользователи", "Мои расчёты", "Новый расчёт")
    val icons = listOf(Icons.Default.Person, Icons.Default.List, Icons.Default.Add)
    var selectedTab by remember { mutableIntStateOf(0) }

    val usersViewModel: UsersViewModel = viewModel()
    val depositViewModel: DepositViewModel = remember { DepositViewModel(depositRepository) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(icons[index], contentDescription = title) },
                        label = { Text(title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> MainScreen(
                    onLogout = onGlobalLogout,
                    viewModel = usersViewModel,
                    onUserClick = { userId -> navController.navigate("userDetail/$userId") }
                )
                1 -> MyCalculationsScreen(viewModel = depositViewModel)
                2 -> DepositCalculatorScreen(
                    onCalculationSaved = {
                        selectedTab = 1
                        depositViewModel.loadUserCalculations()
                    },
                    viewModel = depositViewModel
                )
            }
        }
    }
}
