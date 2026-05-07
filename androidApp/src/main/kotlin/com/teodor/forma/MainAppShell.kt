package com.teodor.forma

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.teodor.forma.ui.theme.EmeraldGreen
import com.teodor.forma.ui.view.ExerciseEditorView
import com.teodor.forma.ui.view.ExerciseLibraryView
import com.teodor.forma.ui.view.LoginScreen
import com.teodor.forma.ui.view.PlannedExerciseEditorView
import com.teodor.forma.ui.view.ProfileView
import com.teodor.forma.ui.view.RegisterView
import com.teodor.forma.ui.view.WorkoutDayEditorView
import com.teodor.forma.ui.view.WorkoutPlanEditorView
import com.teodor.forma.ui.view.WorkoutPlanView
import com.teodor.shared.viewmodel.AppAuthState
import com.teodor.shared.viewmodel.AppShellViewModel
import com.teodor.shared.viewmodel.ExerciseEditorViewModel
import com.teodor.shared.viewmodel.ExerciseLibraryViewModel
import com.teodor.shared.viewmodel.LoginViewModel
import com.teodor.shared.viewmodel.PlannedExerciseEditorViewModel
import com.teodor.shared.viewmodel.ProfileViewModel
import com.teodor.shared.viewmodel.RegisterViewModel
import com.teodor.shared.viewmodel.WorkoutDayEditorViewModel
import com.teodor.shared.viewmodel.WorkoutPlanEditorViewModel
import com.teodor.shared.viewmodel.WorkoutPlanViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainAppShell(
    viewModel: AppShellViewModel = koinViewModel()
) {
    val authState by viewModel.authState.collectAsState()

    when (authState) {
        is AppAuthState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EmeraldGreen)
            }
        }
        is AppAuthState.Authenticated -> {
            AuthenticatedApp()
        }
        is AppAuthState.Unauthenticated -> {
            UnauthenticatedNavGraph()
        }
    }
}

@Composable
fun AuthenticatedApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("?")

    Scaffold(
        topBar = {
            FormaTopBar(title = getTitleForRoute(currentRoute))
        },
        bottomBar = {
            FormaBottomNav(navController = navController, currentRoute = currentRoute)
        }
    ) { paddingValues ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun UnauthenticatedNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("login") {
            val viewModel: LoginViewModel = koinViewModel()

            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            val viewModel: RegisterViewModel = koinViewModel()

            RegisterView(
                viewModel = viewModel,
                onNavigateBackToLogin = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = "exercises",
        modifier = modifier
    ) {
        composable("routines") { backStackEntry ->
            val viewModel: WorkoutPlanViewModel = koinViewModel()

            val savedStateHandle = backStackEntry.savedStateHandle
            val shouldRefresh by savedStateHandle.getStateFlow("refresh_plans", false).collectAsState()

            LaunchedEffect(shouldRefresh) {
                if (shouldRefresh) {
                    viewModel.loadPlans()
                    savedStateHandle["refresh_plans"] = false
                }
            }

            WorkoutPlanView(
                viewModel = viewModel,
                onNavigateToEditor = { planId ->
                    if (planId != null) {
                        navController.navigate("plan_editor?planId=$planId")
                    } else {
                        navController.navigate("plan_editor")
                    }
                }
            )
        }

        composable(
            route = "plan_editor?planId={planId}",
            arguments = listOf(
                navArgument("planId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val viewModel: WorkoutPlanEditorViewModel = koinViewModel()
            val currentPlanId = backStackEntry.arguments?.getString("planId")

            val savedStateHandle = backStackEntry.savedStateHandle
            val shouldRefresh by savedStateHandle.getStateFlow("refresh_plan", false).collectAsState()
            LaunchedEffect(shouldRefresh) {
                if (shouldRefresh) {
                    viewModel.refreshDays()
                    savedStateHandle["refresh_plan"] = false
                }
            }

            WorkoutPlanEditorView(
                viewModel = viewModel,
                onNavigateBack = { refreshOnReturn ->
                    if (refreshOnReturn) {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("refresh_plans", true)
                    }

                    navController.popBackStack()
                },
                onNavigateToDayEditor = { dayId ->
                    if (dayId != null) {
                        navController.navigate("day_editor?dayId=$dayId&planId=$currentPlanId")
                    } else {
                        navController.navigate("day_editor?dayId=null&planId=$currentPlanId")
                    }
                }
            )
        }

        composable(
            route = "day_editor?dayId={dayId}&planId={planId}",
            arguments = listOf(
                navArgument("dayId") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("planId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val viewModel: WorkoutDayEditorViewModel = koinViewModel()
            val currentDayId = backStackEntry.arguments?.getString("dayId")

            val savedStateHandle = backStackEntry.savedStateHandle
            val shouldRefresh by savedStateHandle.getStateFlow("refresh_day", false).collectAsState()
            LaunchedEffect(shouldRefresh) {
                if (shouldRefresh) {
                    viewModel.refreshExercises()
                    savedStateHandle["refresh_day"] = false
                }
            }

            WorkoutDayEditorView(
                viewModel = viewModel,
                onNavigateBack = { refreshOnReturn ->
                    if (refreshOnReturn) {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("refresh_plan", true)
                    }
                    navController.popBackStack()
                },
                onNavigateToExerciseEditor = { exerciseId ->
                    if (exerciseId != null) {
                        navController.navigate("planned_exercise_editor?exerciseId=$exerciseId")
                    } else {
                        navController.navigate("planned_exercise_editor?exerciseId=null&dayId=$currentDayId")
                    }
                },
                onStartWorkout = { _ ->
                    //navController.navigate("active_workout/$dayIdToStart")
                }
            )
        }

        composable(
            route = "planned_exercise_editor?exerciseId={exerciseId}&dayId={dayId}",
            arguments = listOf(
                navArgument("exerciseId") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("dayId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            val viewModel: PlannedExerciseEditorViewModel = koinViewModel()

            PlannedExerciseEditorView(
                viewModel = viewModel,
                onNavigateBack = { refreshOnReturn ->
                    if (refreshOnReturn) {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("refresh_day", true)
                    }
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
                viewModel = profileViewModel
            )
        }
    }
}

fun getTitleForRoute(route: String?): String {
    return when {
        "exercises" == route -> "Exercises"
        "profile" == route -> "Profile"
        "routines" == route -> "Routines"
        route?.contains("exercise_editor") == true -> "Exercises"
        route?.contains("plan_editor") == true -> "Workout Plan"
        route?.contains("day_editor") == true -> "Workout Day"
        route?.contains("planned_exercise") == true -> "Planned Exercise"
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
                    painter = painterResource(id = R.drawable.ic_nav_plans),
                    contentDescription = "Workout plans",
                    modifier = Modifier.size(28.dp)
                )
            },
            selected = currentRoute == "routines",
            onClick = { navigateTo("routines") }
        )

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
        )
    }
}