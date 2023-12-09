package com.example.freshcard

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.databinding.ActivityCheckEmailOtpBinding
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener

class checkEmailOTPActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckEmailOtpBinding
    private lateinit var userDAO: UserDAO
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckEmailOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDAO = UserDAO()
        email = intent.getStringExtra("email").orEmpty()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.resendOtp.setOnClickListener {
            // Chặn người dùng không thể nhấn nút "Resend OTP" trong thời gian chờ
            binding.resendOtp.isEnabled = false

            // Bắt đầu đếm ngược 1 phút 30 giây (90 giây)
            object : CountDownTimer(90 * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // Cập nhật giao diện người dùng với thời gian đếm ngược
                    val secondsRemaining = millisUntilFinished / 1000
                    val text = "Don't resend OTP in ${secondsRemaining}s"
                    val spannableString = SpannableString(text)

                    // Đặt màu đỏ cho phần số giây
                    val colorSpan = ForegroundColorSpan(Color.RED)
                    val start = text.indexOf(secondsRemaining.toString())
                    val end = start + secondsRemaining.toString().length
                    spannableString.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    binding.resendOtp.text = spannableString
                }

                override fun onFinish() {
                    // Khi đếm ngược kết thúc, cho phép người dùng nhấn lại nút "Resend OTP"
                    binding.resendOtp.isEnabled = true
                    binding.resendOtp.text = "Resend OTP"
                }
            }.start()

            // Gọi lại hàm sendForgotPasswordCode từ UserDAO
            userDAO.sendForgotPasswordCode(email) { result ->
                if (result != null) {
                    if (result["state"] as Boolean) {
                        // Mã OTP đã được gửi lại thành công
                        showToast("New OTP has been sent.")
                        showNotification(result["otp"].toString())
                    } else {
                        // Gửi lại mã OTP không thành công
                        showToast(result["message"].toString())
                    }
                } else {
                    showToast("Error occurred.")
                }
            }
        }

        binding.backtologin.setOnClickListener {
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnSubmit.setOnClickListener {
            val enteredOTP = binding.editOtp.text.toString().trim()

            if (isValidOTP(enteredOTP)) {
                // Nếu OTP hợp lệ, kiểm tra trong database
                userDAO.checkForgotPasswordCode(email, enteredOTP) { result ->
                    if (result != null) {
                        if (result["state"] as Boolean) {
                            // Mã OTP hợp lệ, thực hiện các bước tiếp theo
                            showToast("OTP is valid. Proceed with the reset password.")

                             val intent = Intent(this, ResetPasswordActivity::class.java)
                             intent.putExtra("email", email)
                             startActivity(intent)
                        } else {
                            // Mã OTP không hợp lệ
                            showToast(result["message"].toString())
                        }
                    } else {
                        showToast("Error occurred.")
                    }
                }
            } else {
                showToast("Invalid OTP. Please try again.")
            }
        }
    }

    private fun isValidOTP(otp: String): Boolean {
        return otp.isNotEmpty()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showNotification(otp: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "channel_id"
        val channelName = "channel_name"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.iconlogo)
            .setContentTitle("OTP Notification")
            .setContentText("Your new OTP is: $otp \n OTP is only valid for 2 minutes")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }
}
