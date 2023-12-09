package com.example.freshcard

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.databinding.ActivityEditProfileBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val REQUEST_IMAGE_CAPTURE = 1
    private var imageBitmap: Bitmap? = null
    private var photoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fullName = intent.getStringExtra("fullName").orEmpty();
        val email = intent.getStringExtra("email").orEmpty();
        val phone = intent.getStringExtra("phone").orEmpty();
        val avatarFileName = intent.getStringExtra("avatar").orEmpty()

        val sharedPreferences = getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)

        binding.editFullname.setText(fullName)
        binding.editEmail.setText(email)
        binding.editPhone.setText(phone)

        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.btnPhoto.setOnClickListener{
            dispatchTakePictureIntent()
        }

        binding.btnSubmit.setOnClickListener{
            // Lấy thông tin mới từ các trường nhập liệu
            val newFullName = binding.editFullname.text.toString()
            val newPhoneNumber = binding.editPhone.text.toString()

            // Đường dẫn của ảnh từ photoUri (nếu đã chụp ảnh)
            val newAvatar: String? = photoPath ?: avatarFileName

            // Gọi hàm updateUserById để cập nhật thông tin người dùng
            val userDAO = UserDAO()
            val userId = sharedPreferences.getString("idUser", null) // Lấy ID người dùng từ SharedPreferences
            if (userId != null) {
                userDAO.updateUserById(
                    newFullName = newFullName,
                    Email = email,
                    newPhoneNumber = newPhoneNumber,
                    newAvatar = newAvatar
                ) { result ->
                    // Xử lý kết quả từ hàm updateUserById
                    if (result["state"] == true) {
                        // Cập nhật thành công
                        finish()
                        showToast( result["message"].toString())
                        // Thực hiện bất kỳ thao tác hoặc thông báo gì khác ở đây (nếu cần)
                    } else {
                        // Cập nhật thất bại
                        showToast(result["message"].toString())
                        // Hiển thị thông báo cho người dùng (nếu cần)
                    }
                }
            } else {
                // Hiển thị thông báo hoặc xử lý khi không có ID người dùng
                showToast("EditProfileActivity: Không thể lấy ID người dùng từ SharedPreferences")
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            imageBitmap = data?.extras?.get("data") as Bitmap?
            if (imageBitmap != null) {
                // Lưu đường dẫn ảnh
                photoPath = saveImageToInternalStorage(imageBitmap!!)

                // Hiển thị ảnh đã chụp
                Glide.with(this).load(imageBitmap).into(binding.avatar)
            } else {
                Log.e("EditProfileActivity", "Error: Image data is null")
            }
        }
    }

    // Hàm để lưu ảnh vào bộ nhớ trong và trả về đường dẫn của ảnh
    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val fileName = "${UUID.randomUUID()}.jpg"

        val file = File(getDir("images", Context.MODE_PRIVATE), fileName)

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return fileName
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
