package com.example.freshcard

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.R
import com.example.freshcard.databinding.ActivityLoginBinding
import com.example.freshcard.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private  lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            var name = binding.editName.text.toString()
            var email = binding.editEmail.text.toString()
            var password = binding.editPassword.text.toString()
            var rePassword = binding.editCfpassword.text.toString()

            var isValid = validate(email, password, rePassword, name)
            var result = false
            if(isValid){
                result = register(email, password, name)
            }

            if(result){
                var intent = Intent()
                intent.putExtra("email", email)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    private fun register(email: String, password : String, name : String) : Boolean{
        //hashing password
        var hashPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        val result : Boolean = UserDAO().register(email, hashPassword, name);
        if(result){
            Toast.makeText(this, "Register successful!", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Register failed, please try again!", Toast.LENGTH_SHORT).show()
        }

        return result
    }

    fun validate(email: String, password: String, rePassword : String, name : String) : Boolean{

        var message = ""
        var state = true

        if (email.isEmpty()) {
            state = false
            message = "Please fill all fields"
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            state = false
            message = "Please enter a valid email"
        }

        if (password.isEmpty() && state) {
            state = false
            message = "Field can't be empty"
        } else if(!(isValidPassword(password)["state"] as Boolean)  && state) {
            state = false
            message = isValidPassword(password)["message"] as String
        }else if(password != rePassword && state){
            state = false
            message = "Passwords are not match"
        }

        if (name.isEmpty()  && state) {
            state = false
            message = "Please fill all fields"
        }

        if(!state){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        return state
    }

    private fun isValidPassword(password: String): MutableMap<String, Any?> {
        var result = mutableMapOf<String, Any?>()
        result["state"] = true
        if (password.length < 8) {
            result["state"] = false
            result["message"] = "password must have at least 8 letter"
        }else
            if (password.filter { it.isDigit() }.firstOrNull() == null) {
                result["state"] = false
                result["message"] = "password must have at least 1 digit"
            }else
                if (password.filter { it.isLetter() }.filter { it.isUpperCase() }.firstOrNull() == null) {
                    result["state"] = false
                    result["message"] = "password must have at least 1 upper character"
                }else
                    if (password.filter { it.isLetter() }.filter { it.isLowerCase() }.firstOrNull() == null) {
                        result["state"] = false
                        result["message"] = "password must have at least 1 lower character"
                    }

        return result
    }
}