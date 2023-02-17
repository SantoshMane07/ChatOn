package com.example.chaton.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chaton.repository.DataRepository

class HomePageViewModelFactory(val Repo: DataRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return HomePageViewModel(Repo) as T
        //return super.create(modelClass)
    }
}