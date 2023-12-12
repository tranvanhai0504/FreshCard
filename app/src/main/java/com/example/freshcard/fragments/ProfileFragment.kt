package com.example.freshcard.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.freshcard.ChangePasswordActivity
import com.example.freshcard.DAO.ImageDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.EditProfileActivity
import com.example.freshcard.R
import java.io.File


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
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

    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}