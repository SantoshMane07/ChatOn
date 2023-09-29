package com.example.chaton.views

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chaton.R
import com.example.chaton.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {

        //Initializations
        private var db: FirebaseDatabase = FirebaseDatabase.getInstance()
        private var mauth: FirebaseAuth = Firebase.auth
        lateinit var logInBinding:ActivityLoginBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            logInBinding=ActivityLoginBinding.inflate(layoutInflater)
            setContentView(logInBinding.root)

            //ProgressDialog
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Please wait")
            progressDialog.setCancelable(false)

            //Create new Account
            logInBinding.tvCreateNewAccount.setOnClickListener {
                startActivity(Intent(this,SignUpActivity::class.java))
            }
            //Login To App
            if (mauth.currentUser!=null){
                startActivity(Intent(this,HomePageActivity::class.java))
                finish()
            }
            logInBinding.btnLogin.setOnClickListener {
                progressDialog.show()
                val email = logInBinding.edtEmail.text.toString()
                val password = logInBinding.edtPassword.text.toString()
                if(email.isNotEmpty() && password.isNotEmpty()){
                    mauth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                        Toast.makeText(this,"${it.isSuccessful}", Toast.LENGTH_SHORT).show()
                        if (it.isSuccessful){
                            //Store Token to DB
                            retreiveAndStoreToken()
                            //
                            startActivity(Intent(this,HomePageActivity::class.java))
                            progressDialog.dismiss()
                            Toast.makeText(this,"Logged in Successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        else{
                            progressDialog.dismiss()
                            Toast.makeText(this,"Invalid Credentials", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    progressDialog.dismiss()
                    Toast.makeText(this,"Please Enter all fields", Toast.LENGTH_SHORT).show()
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