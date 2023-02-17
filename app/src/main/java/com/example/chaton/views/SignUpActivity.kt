package com.example.chaton.views

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chaton.R
import com.example.chaton.databinding.ActivitySignUpBinding
import com.example.chaton.models.User
import com.example.chaton.viewmodels.SignUpViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    //Initializations
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var mauth: FirebaseAuth = Firebase.auth
    private var storageDB: FirebaseStorage = FirebaseStorage.getInstance()
    lateinit var signUpBinding:ActivitySignUpBinding
    lateinit var signUpViewModel: SignUpViewModel
    lateinit var imageUriString:String
    lateinit var imageUri: Uri
    fun String.isEmailValid() =
        Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        ).matcher(this).matches()
    fun String.isPhoneValid()=
        Pattern.compile(
            "[0-9]{10}"
        ).matcher(this).matches()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpBinding=ActivitySignUpBinding.inflate(layoutInflater)
        signUpViewModel= SignUpViewModel()
        setContentView(signUpBinding.root)

        //ProgressDialog
        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Please wait")

        //SignUp to FireBase
        signUpBinding.btnSignUp.setOnClickListener {
            progressDialog.show()
            val name=signUpBinding.edtName.text.toString()
            val phone=signUpBinding.edtPhone.text.toString()
            val email=signUpBinding.edtEmail.text.toString()
            val password=signUpBinding.edtPassword.text.toString()
            val cpassword=signUpBinding.edtConfirmPassword.text.toString()

            if(name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty() && cpassword.isNotEmpty()){
                if (email.isEmailValid() && phone.isPhoneValid() && cpassword.equals(password) && password.length>5){

                    mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                        if (it.isSuccessful){
                            //
                            imageUriString="https://firebasestorage.googleapis.com/v0/b/bzechat.appspot.com/o/defaultprofileimg.png?alt=media&token=1b68cce0-2022-4f62-9fc8-5910b26c392b"

                            val user = User(mauth.currentUser!!.uid,name,phone,email,imageUriString,"Hey! there Iam using ChatOn")
                            db.getReference().child("Users").child("${mauth.currentUser!!.uid}").setValue(user)
                            //storageDB.getReference().child("Upload").child("${mauth.currentUser!!.uid}").putFile(imageUri)
                            //
                            progressDialog.dismiss()
                            //store token to database
                            retreiveAndStoreToken()
                            //
                            startActivity(Intent(this,LoginActivity::class.java))
                            Toast.makeText(this,"Successfully SignUp", Toast.LENGTH_SHORT).show()
                            finish()
                            //
                        }
                        else{
                            progressDialog.dismiss()
                            Toast.makeText(this,"Email already exists", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else if (!email.isEmailValid()){
                    progressDialog.dismiss()
                    Toast.makeText(this,"Enter Valid Email", Toast.LENGTH_SHORT).show()
                }
                else if(!phone.isPhoneValid()){
                    progressDialog.dismiss()
                    Toast.makeText(this,"Enter Valid Phone number", Toast.LENGTH_SHORT).show()
                }
                else if (!cpassword.equals(password)){
                    progressDialog.dismiss()
                    Toast.makeText(this,"Password Do not match", Toast.LENGTH_SHORT).show()
                }
                else if(password.length<5){
                    progressDialog.dismiss()
                    Toast.makeText(this,"Password Length should be 6 digit or greater", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                progressDialog.dismiss()
                Toast.makeText(this,"Fill all Fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun retreiveAndStoreToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful){
                val token = it.result
                val currUserId = mauth.currentUser!!.uid
                db.getReference("tokens").child(currUserId).setValue(token)
            }
        }
    }
}