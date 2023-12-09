package com.example.freshcard

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.databinding.ActivityLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var isShowPassword : Boolean = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.linkSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivityForResult(intent, 0);
        }

        binding.forgotPassword.setOnClickListener {
            val intent = Intent(this, SendForgotPasswordEmailActivity::class.java)
            startActivity(intent)
        }

        binding.btnShowPassword.setOnClickListener{ e ->
            isShowPassword = !isShowPassword
            if(isShowPassword){
                binding.editPassword.transformationMethod = null
            }else{
                binding.editPassword.transformationMethod = PasswordTransformationMethod()
            }
        }

        binding.btnSubmit.setOnClickListener {v ->
            setLoading()
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            var result = validate(email, password)

            if(result){
                login(email, password)
            }else{
                cancelLoading()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK){
            binding.editEmail.setText(data!!.getStringExtra("name"))
        }
    }

    fun setLoading(){
        binding.btnSubmit.text = ""
        binding.progressCircle.visibility = ProgressBar.VISIBLE
        binding.editEmail.isEnabled = false
        binding.editPassword.isEnabled = false
    }

    fun cancelLoading(){
        binding.progressCircle.visibility = ProgressBar.INVISIBLE
        binding.btnSubmit.text = "Login"
        binding.editEmail.isEnabled = true
        binding.editPassword.isEnabled = true
    }

    fun login(email: String, password: String){
        UserDAO().login(email, password){
            it ->

            if (it != null) {
                //check state of login
                if (it["state"] as Boolean) {
                    UserDAO().UpdateDateLogin(it["message"].toString())
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("idUser", it["message"].toString())
                    startActivity(intent)
                    finish()
                } else {

                }
            }else{
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            }
            cancelLoading()
        }
    }

    fun validate(email: String, password: String) : Boolean{
        binding.alertEmail.setText("")
        binding.alertPassword.setText("")
        var emailMessage = ""
        var stateEmail = true
        var passwordMessage = ""
        var statePassword = true
        if (email.isEmpty()) {
            stateEmail = false
            emailMessage = "Field can't be empty"
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            stateEmail = false
            emailMessage = "Please enter a valid email"
        }

        if (password.isEmpty()) {
            statePassword = false
            passwordMessage = "Field can't be empty"
        } else if(!(isValidPassword(password)["state"] as Boolean)) {
            statePassword = false
            passwordMessage = isValidPassword(password)["message"] as String
        }

        if(!stateEmail){
            binding.alertEmail.setText(emailMessage)
        }

        if(!statePassword){
            binding.alertPassword.setText(passwordMessage)
        }

        return stateEmail && statePassword
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