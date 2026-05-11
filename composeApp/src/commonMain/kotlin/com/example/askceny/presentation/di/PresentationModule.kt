package com.example.askceny.presentation.di

import com.example.askceny.presentation.viewmodels.AuthViewModel
import com.example.askceny.presentation.viewmodels.QuizViewModel
import org.koin.dsl.module

val presentationModule = module {
    factory { AuthViewModel(get()) }
    factory { QuizViewModel(get()) }
}
