package com.example.chaton.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaton.models.User
import com.example.chaton.repository.DataRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class EditProfileViewModel(val Repo: DataRepository) : ViewModel() {

    private val mauth= FirebaseAuth.getInstance()
    public var userProfileData: MutableLiveData<User> = MutableLiveData()
    var usersArray= MutableLiveData<ArrayList<User>>()

    fun getuserProfileData() : MutableLiveData<User> {
        userProfileData=Repo.getUserProfileData(mauth.currentUser!!.uid)
        return userProfileData
    }
    fun saveUserProfileData(user: User, id: String, context: Context){
        Repo.saveUserProfileData(user,mauth.currentUser!!.uid,context)
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