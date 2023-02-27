package com.example.chaton.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaton.models.User
import com.example.chaton.repository.DataRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class HomePageViewModel(val Repo: DataRepository): ViewModel() {
    //Initializations
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var mauth: FirebaseAuth = Firebase.auth
    private var storageDB: FirebaseStorage = FirebaseStorage.getInstance()

    var usersArray= MutableLiveData<ArrayList<User>>()

    //Functions
    fun getUserArray(): MutableLiveData<ArrayList<User>> {
        usersArray=Repo.getUsers()
        return usersArray
    }
    //ACTIVE
    fun setUserStatusOnline(id:String){
        viewModelScope.launch {
            Repo.setUserStatusOnline(id)
        }
    }
    fun setUserStatusOffline(id:String){
        viewModelScope.launch {
            Repo.setUserStatusOffline(id)
        }
    }
    //
}