    package com.example.freshcard

    import android.app.NotificationChannel
    import android.app.NotificationManager
    import android.content.Context
    import android.content.Intent
    import android.os.Build
    import android.os.Bundle
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.app.NotificationCompat
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
                val email = binding.editEmail.text.toString().trim()
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
                        Toast.makeText(this, "Please check notification to seen OTP reset your password", Toast.LENGTH_SHORT).show()

                        showNotification(it["otp"].toString())

                        val intent = Intent(this, checkEmailOTPActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it["message"].toString(), Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
                }
            }
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
                .setContentText("Your OTP is: $otp \n OTP is only valid for 2 minutes")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            notificationManager.notify(0, notificationBuilder.build())
        }
    }
