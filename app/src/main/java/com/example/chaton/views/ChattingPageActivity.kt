package com.example.chaton.views

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chaton.NotificationData
import com.example.chaton.PushNotification
import com.example.chaton.R
import com.example.chaton.RetrofitInstance
import com.example.chaton.databinding.ActivityChattingPageBinding
import com.example.chaton.models.Messeage
import com.example.chaton.models.adapters.MessageAdapter
import com.example.chaton.repository.DataRepository
import com.example.chaton.viewmodels.ChattingPageViewModel
import com.example.chaton.viewmodels.ChattingPageViewModelFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*

class ChattingPageActivity : AppCompatActivity() {
    lateinit var chattingPageBinding:ActivityChattingPageBinding
    private val db= FirebaseDatabase.getInstance()
    val Repo= DataRepository(db)
    lateinit var senderID:String
    lateinit var receiverID:String
    lateinit var chattingPageViewModel: ChattingPageViewModel
    var madapter : MessageAdapter = MessageAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        chattingPageBinding=ActivityChattingPageBinding.inflate(layoutInflater)
        setContentView(chattingPageBinding.root)
        chattingPageViewModel= ViewModelProvider(this, ChattingPageViewModelFactory(Repo)).get(ChattingPageViewModel::class.java)
        super.onCreate(savedInstanceState)

        //Creating Room
        senderID = intent.extras!!.get("senderID").toString()
        receiverID = intent.extras!!.get("receiverID").toString()
        var senderRoom:String = senderID+receiverID
        var receiverRoom:String = receiverID+senderID
        //Displaying Receiver Details
        chattingPageViewModel.getReceiverProfileData(receiverID).observe( this, androidx.lifecycle.Observer {
            chattingPageBinding.tvReceiverName.text= it.name
            var defaultimg = "https://firebasestorage.googleapis.com/v0/b/chaton-a9700.appspot.com/o/defaultprofileimg.png?alt=media&token=25ec315e-cecb-4339-8035-5f1a40dcefa1"
            //Picasso.get().load(it.imgUri).into(chattingPageBinding.ChatProfileimgview)
            Picasso.get().load(defaultimg).into(chattingPageBinding.ChatProfileimgview)
            chattingPageBinding.tvReceiverActive.text = it.active
        })
        //
        var messArr:ArrayList<Messeage> = ArrayList()
        //Set Recyclerview
        var llm = LinearLayoutManager(this)
        //llm.stackFromEnd=true
        chattingPageBinding.chattingPageRecview.layoutManager = llm
        //Set Adapter

        //Getting Messeages from DB and Setting it on Recyclerview
        chattingPageViewModel.getConversations(senderRoom).observe(this, androidx.lifecycle.Observer {
            //DataChanged clear array add new data to array
//            messArr.clear()
//            messArr.addAll(it)
            //NotifyDataSetChanged to adapter
            madapter.messgListChanged(it)
            chattingPageBinding.chattingPageRecview.scrollToPosition(it.size-1)
        })
        chattingPageBinding.chattingPageRecview.adapter=madapter

        //On Messeage Send Button Clicked
        chattingPageBinding.btnSendMssge.setOnClickListener {
            var mess=chattingPageBinding.edtMesseage.text.toString().trim()
            if (mess.isNotEmpty() && mess.isNotBlank()){
                var date= Date()
                var message:Messeage=Messeage(mess,date.time,senderID)
                chattingPageViewModel.storechatsinDB(message,senderRoom,receiverRoom,receiverID)
                chattingPageBinding.edtMesseage.setText("")
                //Send Notification to User
                sendNotificationtoUser(senderID,receiverID,mess)
            }
            else{
                Toast.makeText(this,"Please Enter Messeage", Toast.LENGTH_SHORT).show()
                chattingPageBinding.edtMesseage.setText("")
            }
        }





        //ToolBar
        setSupportActionBar(chattingPageBinding.ChattingPageToolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title=""
        getSupportActionBar()!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);

    }
    //On Pause
    override fun onPause() {
        super.onPause()
        chattingPageViewModel.setUserStatusOffline(senderID)
    }
    //On Resume
    override fun onResume() {
        super.onResume()
        chattingPageViewModel.setUserStatusOnline(senderID)
    }
    //On Start
    override fun onStart() {
        super.onStart()
        chattingPageViewModel.setUserStatusOnline(senderID)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onBackPressed()
        //finish()
        return super.onOptionsItemSelected(item)
    }
    //Send Notification
    private fun sendNotificationtoUser(senderID:String,receiverID:String,messeage:String){
//        var receiverToken:String = ""
//        var title:String =""
        var flag:Boolean = true
        var mssg:String = messeage
        db.getReference("tokens").child(receiverID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var receiverToken = snapshot.value.toString()
                db.getReference("Users").child(senderID).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(flag==true){
                            var title = snapshot.child("name").value.toString()
                            Log.d("TESTnoti", "sendNotification: title is $title")
                            Log.d("TESTnoti", "sendNotification: Token is $receiverToken")
                            sendNotification(PushNotification(NotificationData(title,mssg,senderID, receiverID),"$receiverToken"))
                            flag=false
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }
            override fun onCancelled(error: DatabaseError) {
                //Toast.makeText(applicationContext, "Failed to get token to send notification",Toast.LENGTH_SHORT).show()
                Log.d("TESTnoti", "sendNotification: Failed to get token to send notification")
            }
        })
    }
    //Sending notification ...
    private fun sendNotification(notification : PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if (response.isSuccessful){
                //Toast.makeText(applicationContext,"Response send through api",Toast.LENGTH_SHORT).show()
                Log.d("TESTnoti", "sendNotification: Response send through api")
            }
        }
        catch (e:Exception){
            //Toast.makeText(applicationContext, "Error in sending ", Toast.LENGTH_SHORT).show()
            Log.d("TESTnoti", "sendNotification: $e")
        }
    }

}