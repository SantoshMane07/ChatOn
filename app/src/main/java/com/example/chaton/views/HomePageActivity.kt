package com.example.chaton.views

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chaton.R
import com.example.chaton.databinding.ActivityHomePageBinding
import com.example.chaton.models.User
import com.example.chaton.models.adapters.UsersAadapter
import com.example.chaton.models.adapters.iUserAdapter
import com.example.chaton.repository.DataRepository
import com.example.chaton.viewmodels.HomePageViewModel
import com.example.chaton.viewmodels.HomePageViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class HomePageActivity : AppCompatActivity() , iUserAdapter{
    //Initializations
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var mauth: FirebaseAuth = Firebase.auth
    private var storageDB: FirebaseStorage = FirebaseStorage.getInstance()

    lateinit var usersArray:ArrayList<User>
    val mAdapter: UsersAadapter =UsersAadapter(this)
    lateinit var homePageBinding: ActivityHomePageBinding
    lateinit var homePageViewModel: HomePageViewModel
    //Setting user id
    lateinit var id:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homePageBinding=ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(homePageBinding.root)
        //Setting User id
        id=mauth.currentUser!!.uid

        val Repo: DataRepository =DataRepository(db)
        homePageViewModel = ViewModelProvider(this, HomePageViewModelFactory(Repo)).get(HomePageViewModel::class.java)
        homePageBinding.homePageRecview.layoutManager= LinearLayoutManager(this)
        //SettingToolBar
        setSupportActionBar(homePageBinding.homeToolBar)
        supportActionBar!!.title="Your Chats"
        //
        usersArray= ArrayList<User>()
        //Getting Data in Array and sending to Adapter
        homePageViewModel.getUserArray().observe(this, Observer {
            //DataChanged
            usersArray.clear()
            usersArray.addAll(it)
            //NotifyDataSetChanged
            mAdapter.usersListChanged(usersArray)
        })
        homePageBinding.homePageRecview.adapter=mAdapter
    }
    //On Pause
    override fun onPause() {
        super.onPause()
        homePageViewModel.setUserStatusOffline(id)
    }
    //On Resume
    override fun onResume() {
        super.onResume()
        homePageViewModel.setUserStatusOnline(mauth.currentUser!!.uid)
    }
    //On Start
    override fun onStart() {
        super.onStart()
        homePageViewModel.setUserStatusOnline(mauth.currentUser!!.uid)
    }
    //ToolBar Creation
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.homemenus,menu)
        return super.onCreateOptionsMenu(menu)
    }

    //ToolBar Option Selection
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemId=item.itemId
        if (itemId== R.id.edit_profile_btn){
            //
            startActivity( Intent(this, EditProfileActivity::class.java))
        }
        else{
            //
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.logout_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            dialog.show()
            dialog.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                //Clear Token
                clearToken(mauth.currentUser!!.uid)
                //
                mauth.signOut()
                dialog.dismiss()
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
            dialog.findViewById<Button>(R.id.btn_no).setOnClickListener {
                dialog.dismiss()
            }
            //
        }
        return super.onOptionsItemSelected(item)
    }
    //On Row Clicked to Chat with User
    override fun onRowClicked(senderID: String, receiverID: String) {
        val intent: Intent = Intent(this,ChattingPageActivity::class.java)
        intent.putExtra("senderID","${senderID}")
        intent.putExtra("receiverID","${receiverID}")
        startActivity(intent)
    }
    private fun clearToken(userId:String){
        db.getReference("tokens").child(userId).removeValue()
    }
}