package com.teodor.forma

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.teodor.forma.ui.view.ExerciseEditorView
import com.teodor.forma.ui.view.ExerciseLibraryView
import com.teodor.forma.ui.view.LoginScreen
import com.teodor.forma.ui.view.ProfileView
import com.teodor.forma.ui.view.RegisterView
import com.teodor.shared.viewmodel.ExerciseEditorViewModel
import com.teodor.shared.viewmodel.ExerciseLibraryViewModel
import com.teodor.shared.viewmodel.LoginViewModel
import com.teodor.shared.viewmodel.ProfileViewModel
import com.teodor.shared.viewmodel.RegisterViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainAppShell() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            if (currentRoute != "login" && currentRoute != "register") {
                FormaTopBar(title = getTitleForRoute(currentRoute))
            }
        },
        bottomBar = {
            if (currentRoute != "login" && currentRoute != "register") {
                FormaBottomNav(navController = navController, currentRoute = currentRoute)
            }
        }
    ) { paddingValues ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            val viewModel: LoginViewModel = koinViewModel()

            LoginScreen(
                viewModel = viewModel,
                onNavigateToHome = {
                    navController.navigate("exercises") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            val viewModel: RegisterViewModel = koinViewModel()

            RegisterView(
                viewModel = viewModel,
                onNavigateBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("exercises") {
            val viewModel: ExerciseLibraryViewModel = koinViewModel()

            ExerciseLibraryView(
                viewModel = viewModel,
                onNavigateToEditor = { exerciseId ->
                    if (exerciseId != null) {
                        navController.navigate("exercise_editor?exerciseId=$exerciseId")
                    } else {
                        navController.navigate("exercise_editor")
                    }
                }
            )
        }

        composable(
            route = "exercise_editor?exerciseId={exerciseId}",
            arguments = listOf(
                navArgument("exerciseId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val viewModel: ExerciseEditorViewModel = koinViewModel()
            val exerciseIdString = backStackEntry.arguments?.getString("exerciseId")

            LaunchedEffect(exerciseIdString) {
                val id = exerciseIdString?.toLongOrNull()
                if (id != null) {
                    viewModel.loadExercise(id)
                } else {
                    viewModel.startNewExercise()
                }
            }

            ExerciseEditorView(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("profile") {
            val profileViewModel: ProfileViewModel = koinViewModel()

            ProfileView(
                viewModel = profileViewModel,
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

fun getTitleForRoute(route: String?): String {
    return when (route) {
        "exercises" -> "Exercises"
        "profile" -> "Profile"
        else -> "Forma"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormaTopBar(title: String) {
    CenterAlignedTopAppBar(
        title = { Text(
            title,
            fontWeight = FontWeight.Bold
        ) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = Color.Unspecified,
            navigationIconContentColor = Color.Unspecified,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = Color.Unspecified
        )
    )
}

@Composable
fun FormaBottomNav(navController: NavHostController, currentRoute: String?) {
    val navigateTo = { route: String ->
        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nav_exercises),
                    contentDescription = "Exercises",
                    modifier = Modifier.size(36.dp)
                )
            },
            selected = currentRoute == "exercises",
            onClick = { navigateTo("exercises") }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_nav_profile),
                    contentDescription = "Profile",
                    modifier = Modifier.size(28.dp)
                )
            },
            selected = currentRoute == "profile",
            onClick = { navigateTo("profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                indicatorColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = Color.Gray,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedTextColor = Color.Gray
            )
        )
    }
}