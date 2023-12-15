package com.example.freshcard.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.freshcard.ChangePasswordActivity
import com.example.freshcard.DAO.ImageDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.EditProfileActivity
import com.example.freshcard.LoginActivity
import com.example.freshcard.R
import java.io.File

class ProfileFragment : Fragment(R.layout.fragment_profile) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textName = view.findViewById<TextView>(R.id.textName)
        val textEmail = view.findViewById<TextView>(R.id.textEmail)
        val textPhone = view.findViewById<TextView>(R.id.textPhone)
        val avatarImageView = view.findViewById<ImageView>(R.id.avatar)
        val btnEdit = view.findViewById<Button>(R.id.btn_edit)

        val sharedPreferences = requireContext().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val savedId = sharedPreferences.getString("idUser", null)

        if (savedId != null) {
            // Sử dụng UserDAO để lấy thông tin người dùng theo ID
            UserDAO().getUserById(savedId) { user ->
                if (user != null) {
                    // Xử lý dữ liệu người dùng
                    textName.text = "${user.fullName}"
                    textEmail.text = "${user.email}"
                    textPhone.text = "${user.phoneNumber}"

                    // Kiểm tra xem user.avatar có giá trị không
                    if (user.avatar != null) {
                        // Nếu có, sử dụng Glide để tải và hiển thị hình ảnh
                        ImageDAO().getImage(user.avatar.toString(), avatarImageView, "avatars")


                        btnEdit.setOnClickListener {
                            // Chuyển sang ActivityEditProfile
                            val intent = Intent(activity, EditProfileActivity::class.java)
                            intent.putExtra("fullName", textName.text.toString())
                            intent.putExtra("email", textEmail.text.toString())
                            intent.putExtra("phone", textPhone.text.toString())
                            intent.putExtra("avatar", user.avatar)
                            startActivity(intent)
                        }

                    }
                    else {
                        // Nếu không có giá trị, đặt hình ảnh mặc định (ví dụ: @drawable/user)
                        avatarImageView.setImageResource(R.drawable.user)
                    }
                } else {
                    // Xử lý khi không tìm thấy người dùng
                    val message = "User not found"
                    showToast(message)
                }
            }
        }

        var btnLogOut = this.requireView().findViewById<ImageButton>(R.id.btnLogOut)
        btnLogOut.setOnClickListener {

            fragmentManager?.let { it1 -> SignOutDialogFragment().show(it1, "SIGN_OUT_DIALOG") }
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

class SignOutDialogFragment() : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction.
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Sign out")
                .setPositiveButton("Sign out") { dialog, id ->
                    var intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    // User cancelled the dialog.
                }
            // Create the AlertDialog object and return it.
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
