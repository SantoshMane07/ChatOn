package com.example.chaton.views

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.chaton.R
import com.example.chaton.databinding.ActivityEditProfileBinding
import com.example.chaton.models.User
import com.example.chaton.repository.DataRepository
import com.example.chaton.viewmodels.EditProfileViewModel
import com.example.chaton.viewmodels.EditProfileViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class EditProfileActivity : AppCompatActivity() {
    //Initializations
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var mauth: FirebaseAuth = Firebase.auth
    private var storageDB: FirebaseStorage = FirebaseStorage.getInstance()
    private var Repo: DataRepository = DataRepository(db)
    lateinit var editProfileBinding: ActivityEditProfileBinding
    lateinit var editProfileViewModel: EditProfileViewModel

    private var imgUri:String =""
    private var name:String = ""
    private var status:String = ""
    var SelectedImgUri: Uri? = null
    var USERDATAobj : User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editProfileBinding=ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(editProfileBinding.root)
        editProfileViewModel= ViewModelProvider(this, EditProfileViewModelFactory(Repo)).get(EditProfileViewModel::class.java)
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Saving Your Profile Please Wait")
        progressDialog.setCancelable(false)

//        editProfileViewModel.getuserProfileData().observe(this, Observer {
//            imgUri=it.imgUri
//            name=it.name
//            status=it.status
//            editProfileBinding.edtUserStatus.setText(status)
//            editProfileBinding.edtUserName.setText(name)
//            Picasso.get().load(imgUri).into(editProfileBinding.cimgvEditProfile)
        var defaultimg = "https://firebasestorage.googleapis.com/v0/b/chaton-a9700.appspot.com/o/defaultprofileimg.png?alt=media&token=25ec315e-cecb-4339-8035-5f1a40dcefa1"
        Picasso.get().load(defaultimg).into(editProfileBinding.cimgvEditProfile)
//            //
//            USERDATAobj.name=name
//            USERDATAobj.imgUri=imgUri
//            USERDATAobj.status=status
//            USERDATAobj.id=it.id
//            USERDATAobj.email=it.email
//            USERDATAobj.phone=it.phone
//            USERDATAobj.active=it.active
//        })
        db.reference.child("Users").child(mauth.currentUser!!.uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    //imgUri = user!!.imgUri
                    storageDB.getReference().child("Uploads").child("${mauth.currentUser!!.uid}").downloadUrl.addOnSuccessListener {
                        imgUri=it.toString()
                        //Save Data to DataBase
                        Log.d("img", "onCreate on Save btn: ${imgUri}")
                        Picasso.get().load(imgUri).into(editProfileBinding.cimgvEditProfile)
                    }
                    name = user!!.name
                    status = user.status
//                    imgUri = user.imgUri
                    editProfileBinding.edtUserStatus.setText(status)
                    editProfileBinding.edtUserName.setText(name)
//                    Picasso.get().load(imgUri).into(editProfileBinding.cimgvEditProfile)
                    Log.d("SS", "$imgUri : $name : $status")
                    USERDATAobj.name = name
                    USERDATAobj.imgUri = imgUri
                    USERDATAobj.status = status
                    USERDATAobj.id = user.id
                    USERDATAobj.email = user.email
                    USERDATAobj.phone = user.phone
                    USERDATAobj.active = user.active
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //Toast.makeText(this,"Unable to Fetch User Profile $error", Toast.LENGTH_SHORT).show()
            }
        })

        //Clicked on EditImage
        editProfileBinding.cimgvEditProfile.setOnClickListener{
            val intent: Intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent,"Select image"),10)
        }
        //Clicked on Save Button
        editProfileBinding.saveBtn.setOnClickListener{
            progressDialog.show()

            if (SelectedImgUri!=null){
                //Saving img to storage
                storageDB.getReference().child("Uploads").child("${mauth.currentUser!!.uid}").putFile(SelectedImgUri!!).addOnCompleteListener{
//                    storageDB.getReference().child("Uploads").child("${mauth.currentUser!!.uid}").downloadUrl.addOnSuccessListener {
//                        imgUri=it.toString()
//                        //Save Data to DataBase
//                        Log.d("img", "onCreate on Save btn: ${imgUri}")
//                    }
                }
                var user:User = User()
                user.id=mauth.currentUser!!.uid
                user.imgUri=imgUri
                user.email=USERDATAobj.email
                user.phone=USERDATAobj.phone
                user.status=editProfileBinding.edtUserStatus.text.toString()
                user.name=editProfileBinding.edtUserName.text.toString()
                user.active = USERDATAobj.active
                editProfileViewModel.saveUserProfileData(user,"${mauth.currentUser!!.uid}",this)
            }
            else{
                var user:User = User()
                user.id=mauth.currentUser!!.uid
                user.imgUri=USERDATAobj.imgUri
                user.email=USERDATAobj.email
                user.phone=USERDATAobj.phone
                user.status=editProfileBinding.edtUserStatus.text.toString()
                user.name=editProfileBinding.edtUserName.text.toString()
                editProfileViewModel.saveUserProfileData(user,"${mauth.currentUser!!.uid}",this)
            }
            progressDialog.dismiss()
            onBackPressed()
        }
        //ToolBar
        setSupportActionBar(editProfileBinding.EditProfileToolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title=""
        getSupportActionBar()!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);



    }
    //On Pause
    override fun onPause() {
        super.onPause()
        editProfileViewModel.setUserStatusOffline(mauth.currentUser!!.uid)
    }
    //On Resume
    override fun onResume() {
        super.onResume()
        editProfileViewModel.setUserStatusOnline(mauth.currentUser!!.uid)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onBackPressed()
        //finish()
        return super.onOptionsItemSelected(item)
    }
    //
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==10){
            if (data!=null){
                SelectedImgUri=data.data!!
                Toast.makeText(this,"Image Selected", Toast.LENGTH_SHORT).show()
                //editProfileBinding.cimgvEditProfile.setImageURI(Uri.parse(SelectedImgUri.toString()))
                Log.d("IMGS", "onActivityResult: Selected $SelectedImgUri : imgURI $imgUri ")
                //Picasso.get().load(Uri.parse(SelectedImgUri.toString())).into(editProfileBinding.cimgvEditProfile)
                val uri = Uri.parse(SelectedImgUri.toString())
                Glide.with(this)
                    .load(uri.toString())
                    .into(editProfileBinding.cimgvEditProfile)
            }
            else{
                Toast.makeText(this,"Image not Selected", Toast.LENGTH_SHORT).show()
            }
        }//
    }
}