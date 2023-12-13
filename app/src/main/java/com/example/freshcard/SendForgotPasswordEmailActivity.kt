package com.example.freshcard

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.databinding.ActivitySendForgotPassEmailBinding


class SendForgotPasswordEmailActivity : AppCompatActivity() {
    private  val REQUEST_NOTIFICATION_CODE = 12
    private lateinit var binding: ActivitySendForgotPassEmailBinding
    private val handler = Handler(Looper.getMainLooper())
    private var isOperationAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkNotificationPermission()

        binding = ActivitySendForgotPassEmailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            val email = binding.editEmail.text.toString().trim()
            var result = validate(email)

            if (result) {
                sendForgotPasswordEmail(email)
            }
        }

    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        else{
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Quyền đã được cấp, thực hiện công việc cần thiết
            } else {
                // Chưa cấp quyền, yêu cầu quyền
                val permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
                requestPermissions(permissions, REQUEST_NOTIFICATION_CODE)
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

    private fun sendForgotPasswordEmail(email: String) {
        UserDAO().sendForgotPasswordCode(email) { it ->
            if (it != null) {
                // Check state of login
                if (it["state"] as Boolean) {
                    Toast.makeText(this, "Please check email to see OTP and reset your password", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, checkEmailOTPActivity::class.java)
                    intent.putExtra("email", email)
                    startActivity(intent)
                } else {
                    val errorMessage = it["message"].toString()
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()

                    // Nếu thông báo là "Email does not exist", cho phép thực hiện lại ngay lập tức
                    if (errorMessage == "Email does not exist") {
                        isOperationAllowed = true
                    } else {
                        // Ngược lại, đặt trạng thái của hoạt động không được phép và đặt lịch sau 2 phút
                        isOperationAllowed = false
                        handler.postDelayed({ isOperationAllowed = true }, 2 * 60 * 1000)
                    }
                }
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
