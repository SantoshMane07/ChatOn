package com.example.chaton.views

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.chaton.R
import com.example.chaton.databinding.ActivityEditProfileBinding
import com.example.chaton.models.User
import com.example.chaton.repository.DataRepository
import com.example.chaton.viewmodels.EditProfileViewModel
import com.example.chaton.viewmodels.EditProfileViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
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

        editProfileViewModel.getuserProfileData().observe(this, Observer {
            imgUri=it.imgUri
            name=it.name
            status=it.status
            editProfileBinding.edtUserStatus.setText(status)
            editProfileBinding.edtUserName.setText(name)
            Picasso.get().load(imgUri).into(editProfileBinding.cimgvEditProfile)
            //
            USERDATAobj.name=name
            USERDATAobj.imgUri=imgUri
            USERDATAobj.status=status
            USERDATAobj.id=it.id
            USERDATAobj.email=it.email
            USERDATAobj.phone=it.phone
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
                    storageDB.getReference().child("Uploads").child("${mauth.currentUser!!.uid}").downloadUrl.addOnSuccessListener {
                        imgUri=it.toString()
                        //Save Data to DataBase

                        var user:User = User()
                        user.id=mauth.currentUser!!.uid
                        user.imgUri=imgUri
                        user.email=USERDATAobj.email
                        user.phone=USERDATAobj.phone
                        user.status=editProfileBinding.edtUserStatus.text.toString()
                        user.name=editProfileBinding.edtUserName.text.toString()
                        editProfileViewModel.saveUserProfileData(user,"${mauth.currentUser!!.uid}",this)
                    }
                }
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
                editProfileBinding.cimgvEditProfile.setImageURI(SelectedImgUri)
            }
        }
    }
}