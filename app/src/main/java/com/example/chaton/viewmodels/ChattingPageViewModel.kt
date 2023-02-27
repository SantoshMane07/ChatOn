package com.example.chaton.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaton.models.Messeage
import com.example.chaton.models.User
import com.example.chaton.repository.DataRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChattingPageViewModel(val Repo: DataRepository) : ViewModel() {
    //Initializations
    public var ReceiverProfileData: MutableLiveData<User> = MutableLiveData()
    var MessArray: MutableLiveData<ArrayList<Messeage>> = MutableLiveData()
    //Functions
    //
    fun getReceiverProfileData(id:String): MutableLiveData<User> {
        ReceiverProfileData=Repo.getReceiverProfileData(id)
        return ReceiverProfileData
    }
    //
    fun storechatsinDB(message:Messeage,senderRoom:String,receiverRoom:String,receiverID:String){
        Repo.storechatsinDB(message,senderRoom,receiverRoom,receiverID)
    }
    //
    fun getConversations(senderRoom: String) : MutableLiveData<ArrayList<Messeage>> {
        MessArray=Repo.getConversations(senderRoom)
        return MessArray
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