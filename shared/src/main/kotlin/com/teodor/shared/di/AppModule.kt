package com.teodor.shared.di

import com.teodor.shared.persistence.ExerciseDbRepository
import com.teodor.shared.persistence.ExerciseRepository
import com.teodor.shared.persistence.UserDbRepository
import com.teodor.shared.persistence.UserRepository
import com.teodor.shared.service.AuthService
import com.teodor.shared.service.AuthServiceImpl
import com.teodor.shared.service.ExerciseService
import com.teodor.shared.service.ExerciseServiceImpl
import com.teodor.shared.service.ProfileService
import com.teodor.shared.service.ProfileServiceImpl
import com.teodor.shared.utils.Config
import com.teodor.shared.viewmodel.ExerciseEditorViewModel
import com.teodor.shared.viewmodel.ExerciseLibraryViewModel
import com.teodor.shared.viewmodel.LoginViewModel
import com.teodor.shared.viewmodel.ProfileViewModel
import com.teodor.shared.viewmodel.RegisterViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
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
        }
    }

    single<UserRepository> { UserDbRepository(get()) }
    single<ExerciseRepository> { ExerciseDbRepository(get()) }

    single<AuthService> { AuthServiceImpl(get()) }
    single<ProfileService> { ProfileServiceImpl(get()) }
    single<ExerciseService> { ExerciseServiceImpl(get()) }

    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { ExerciseLibraryViewModel(get()) }
    viewModel { ExerciseEditorViewModel(get()) }
}