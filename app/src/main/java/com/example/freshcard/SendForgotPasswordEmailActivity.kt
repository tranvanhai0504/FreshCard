package com.example.freshcard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.databinding.ActivitySendForgotPassEmailBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue


class SendForgotPasswordEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySendForgotPassEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySendForgotPassEmailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            val email = binding.editEmail.text.toString()
            var result = validate(email)

            if(result){
                sendForgotPasswordEmail(email)
            }
        }
    }

    private fun validate(email: String) : Boolean{
        var emailMessage = ""
        var stateEmail = true
        if (email.isEmpty()) {
            stateEmail = false
            emailMessage = "Field can't be empty"
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            stateEmail = false
            emailMessage = "Please enter a valid email"
        }

        if(!stateEmail){
            binding.alertEmail.setText(emailMessage)
        }

        return stateEmail
    }

    private fun sendForgotPasswordEmail(email: String){
        UserDAO().sendForgotPasswordCode(email){
                it ->

            if (it != null) {
                //check state of login
                if (it["state"] as Boolean) {
                    Toast.makeText(this, "Please check your email to reset your password", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, it["message"].toString(), Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
