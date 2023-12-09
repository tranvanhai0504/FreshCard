package com.example.freshcard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var userDAO: UserDAO
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDAO = UserDAO()

        // Lấy địa chỉ email từ Intent
        email = intent.getStringExtra("email").orEmpty()

        // Hiển thị email trong TextView
        binding.email.text = email

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            // Lấy mật khẩu mới từ EditText
            val newPassword = binding.editNewpassword.text.toString().trim()

            // Lấy mật khẩu nhập lại từ EditText
            val confirmPassword = binding.editCnewpassword.text.toString().trim()

            // Kiểm tra xem mật khẩu mới có hợp lệ không
            val passwordError = isValidPassword(newPassword)
            if (passwordError == null) {
                // Kiểm tra xem mật khẩu mới có khớp với mật khẩu nhập lại không
                if (newPassword == confirmPassword) {
                    // Gọi hàm resetPassword từ UserDAO
                    userDAO.resetPassword(email, newPassword) { result ->
                        if (result != null) {
                            if (result["state"] as Boolean) {
                                // Mật khẩu đã được thay đổi thành công
                                showToast(result["message"].toString())
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)

                                finish()
                            } else {
                                // Thay đổi mật khẩu không thành công
                                showToast(result["message"].toString())
                            }
                        } else {
                            showToast("Error occurred.")
                        }
                    }
                } else {
                    showToast("Passwords do not match.")
                }
            } else {
                // Hiển thị thông báo chi tiết lỗi password
                showToast(passwordError)
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
