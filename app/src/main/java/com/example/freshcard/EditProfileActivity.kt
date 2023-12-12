package com.example.freshcard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.freshcard.DAO.ImageDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.databinding.ActivityEditProfileBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID
import java.util.jar.Pack200.Packer

class EditProfileActivity : AppCompatActivity() {
    private val REQUEST_PERMISSION_CODE = 10
    private lateinit var binding: ActivityEditProfileBinding
    private val REQUEST_IMAGE_CAPTURE = 1
    private var imageBitmap: Bitmap? = null
    private lateinit var imageDAO: ImageDAO
    private lateinit var imageUri: Uri
    private var uploadedFileName: String? = null


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
        ImageDAO().getImage(avatarFileName.toString(), binding.avatar, "avatars")

        binding.btnBack.setOnClickListener{
            finish()
        }

        binding.btnChangePassword.setOnClickListener {
            var intent = Intent(this, ChangePasswordActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        binding.btnPhoto.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                dispatchTakePictureIntent()
            } else {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    dispatchTakePictureIntent()
                } else {
                    val permissions = arrayOf(Manifest.permission.CAMERA)
                    requestPermissions(permissions, REQUEST_PERMISSION_CODE)
                }
            }
        }

        binding.btnSubmit.setOnClickListener{
            // Lấy thông tin mới từ các trường nhập liệu
            val newFullName = binding.editFullname.text.toString()
            val newPhoneNumber = binding.editPhone.text.toString()
            /*val fileName = imageDAO.uploadImage(this, imageUri)
            Log.e("imageBitmap", "onActivityResult: $fileName", )*/


            // Đường dẫn của ảnh từ photoUri (nếu đã chụp ảnh)
            val newAvatar: String = avatarFileName
            // Gọi hàm updateUserById để cập nhật thông tin người dùng
            // Gọi hàm updateUserById để cập nhật thông tin người dùng

            val userDAO = UserDAO()
            val userId = sharedPreferences.getString("idUser", null)
            if (userId != null) {
                userDAO.updateUserById(
                    newFullName = newFullName,
                    Email = email,
                    newPhoneNumber = newPhoneNumber,
                    newAvatar = uploadedFileName ?: avatarFileName
                ) { result ->
                    // Xử lý kết quả từ hàm updateUserById
                    if (result["state"] == true) {
                        // Cập nhật thành công
                        finish()
                        showToast(result["message"].toString())
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
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Tạo Uri trực tiếp từ bitmap
            val imageUri = Uri.fromFile(createImageFile(imageBitmap))

            // Gọi hàm uploadImage từ ImageDAO
            val imageDAO = ImageDAO()
            uploadedFileName = imageDAO.uploadAvtar(this, imageUri)
            binding.avatar.setImageBitmap(imageBitmap)

            // Làm điều gì đó với uploadedFileName nếu cần
            Log.d("ImageFileName", "Tên tệp ảnh đã tải lên: $uploadedFileName")
        }
    }

    private fun createImageFile(bitmap: Bitmap): File {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
