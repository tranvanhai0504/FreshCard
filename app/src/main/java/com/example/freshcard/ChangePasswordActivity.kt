package com.example.freshcard

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var userDAO: UserDAO
    private lateinit var email: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDAO = UserDAO()
        email = intent.getStringExtra("email").orEmpty()

        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.btnSubmit.setOnClickListener{
            val oldPws = binding.editPassword.text.toString().trim()
            val newPws = binding.editNewpassword.text.toString().trim()
            val cnewPws = binding.editCfnewpassword.text.toString().trim()

            // Validate old password
            val oldPasswordValidation = isValidPassword(oldPws)
            if (oldPasswordValidation != null) {
                showToast("Old Password is invalid: $oldPasswordValidation")
                return@setOnClickListener
            }

            // Validate new password
            val newPasswordValidation = isValidPassword(newPws)
            if (newPasswordValidation != null) {
                showToast("New Password is invalid: $newPasswordValidation")
                return@setOnClickListener
            }

            // Validate confirm new password
            if (newPws != cnewPws) {
                showToast("Confirm New Password doesn't match New Password.")
                return@setOnClickListener
            }


            userDAO.changePassword(email, oldPws, newPws) { result ->
                // Handle the result
                if (result["state"] == true) {
                    // Password change successful
                    showToast("Password change successful: ${result["message"]}")
                    finish()
                } else {
                    // Password change failed
                    showToast("Password change failed: ${result["message"]}")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun isValidPassword(password: String): String? {
        val digitRegex = Regex(".*\\d.*")
        val lowercaseRegex = Regex(".*[a-z].*")
        val uppercaseRegex = Regex(".*[A-Z].*")

        if (password.length < 8) {
            return "Password must have at least 8 characters."
        }

        if (!digitRegex.matches(password)) {
            return "Password must have at least 1 digit."
        }

        if (!lowercaseRegex.matches(password)) {
            return "Password must have at least 1 lowercase character."
        }

        if (!uppercaseRegex.matches(password)) {
            return "Password must have at least 1 uppercase character."
        }

        return null // Mật khẩu hợp lệ
    }
}