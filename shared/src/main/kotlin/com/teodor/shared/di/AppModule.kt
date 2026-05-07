package com.teodor.shared.di

import com.teodor.shared.persistence.ExerciseDbRepository
import com.teodor.shared.persistence.ExerciseRepository
import com.teodor.shared.persistence.UserDbRepository
import com.teodor.shared.persistence.UserRepository
import com.teodor.shared.persistence.workout.PlannedExerciseDbRepository
import com.teodor.shared.persistence.workout.PlannedExerciseRepository
import com.teodor.shared.persistence.workout.WorkoutDayDbRepository
import com.teodor.shared.persistence.workout.WorkoutDayRepository
import com.teodor.shared.persistence.workout.WorkoutPlanDbRepository
import com.teodor.shared.persistence.workout.WorkoutPlanRepository
import com.teodor.shared.service.AuthService
import com.teodor.shared.service.AuthServiceImpl
import com.teodor.shared.service.ExerciseService
import com.teodor.shared.service.ExerciseServiceImpl
import com.teodor.shared.service.ProfileService
import com.teodor.shared.service.ProfileServiceImpl
import com.teodor.shared.service.WorkoutPlanService
import com.teodor.shared.service.WorkoutPlanServiceImpl
import com.teodor.shared.utils.Config
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
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = Config.getProperty("supabase.url"),
            supabaseKey = Config.getProperty("supabase.key")
        ) {
            install(Auth)
            install(Postgrest)

            defaultSerializer = KotlinXSerializer(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }
    }

    single<UserRepository> { UserDbRepository(get()) }
    single<ExerciseRepository> { ExerciseDbRepository(get()) }
    single<WorkoutPlanRepository> { WorkoutPlanDbRepository(get()) }
    single<WorkoutDayRepository> { WorkoutDayDbRepository(get()) }
    single<PlannedExerciseRepository> { PlannedExerciseDbRepository(get()) }

    single<AuthService> { AuthServiceImpl(get()) }
    single<ProfileService> { ProfileServiceImpl(get()) }
    single<ExerciseService> { ExerciseServiceImpl(get()) }
    single<WorkoutPlanService> { WorkoutPlanServiceImpl(get(), get(), get()) }

    viewModel { AppShellViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { ExerciseLibraryViewModel(get()) }
    viewModel { ExerciseEditorViewModel(get()) }
    viewModel { WorkoutPlanViewModel(get()) }
    viewModel { WorkoutPlanEditorViewModel(get(), get()) }
    viewModel { WorkoutDayEditorViewModel(get(), get()) }
    viewModel { PlannedExerciseEditorViewModel(get(), get(), get()) }
}