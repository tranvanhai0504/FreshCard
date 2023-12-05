package com.example.freshcard.DAO


import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.freshcard.Structure.Database
import com.example.freshcard.Structure.EmailSender
import com.example.freshcard.Structure.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.util.Random
import android.os.Handler
import android.os.Looper

public class UserDAO() {
    var db: DatabaseReference = Database().getReference("users")

    fun login(email: String, password: String, onResult: (HashMap<String, Any?>?) -> Unit) {
        val query = db.orderByChild("email").equalTo(email)

        val valueEventListener = object : ValueEventListener {
            var result = hashMapOf<String, Any?>()

            override fun onDataChange(snapshot: DataSnapshot) {

                result["state"] = true

                for (userSnapshot in snapshot.children) {
                    val hashPass = userSnapshot.child("password").value.toString()

                    //check password
                    val isMatchPassword =
                        BCrypt.verifyer().verify(password.toCharArray(), hashPass.toCharArray())
                    if (!isMatchPassword.verified) {
                        result["state"] = false
                        result["message"] = "Password is not correct"
                        onResult(result)
                        return
                    }

                    //login successful
                    val userId = userSnapshot.key
                    result["message"] = userId

                    onResult(result)
                    return
                }

                //message user not exist
                result["state"] = false
                result["message"] = "Email is not exist!"

                onResult(result)
            }

            override fun onCancelled(error: DatabaseError) {
                result["state"] = false
                result["message"] = "Can't find account!"

                onResult(result)
            }
        }
        query.addValueEventListener(valueEventListener)
    }

    fun register(email: String, password: String, name: String): Boolean {
        val key = db.push().key;

        if (key != null) {
            var newUser = User(password, name, "", email, "", null, 0, null)
            db.child(key).setValue(newUser)
            return true;
        } else {
            return false;
        }
    }

    fun getDbUser(): DatabaseReference {
        return this.db
    }

    fun sendForgotPasswordCode(email: String, onResult: (HashMap<String, Any?>?) -> Unit) {
        val query = db.orderByChild("email").equalTo(email)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Tạo mã OTP ngẫu nhiên
                    val random = Random()
                    val otpCode = String.format("%04d", random.nextInt(10000))

                    // Lưu mã OTP vào database (cùng với thời gian tạo)
                    val userRef = snapshot.children.iterator().next().ref

                    val dataToSave = hashMapOf(
                        "forgotPasswordCode" to otpCode,
                        "forgotPasswordCodeTime" to System.currentTimeMillis(),
                    )
                    userRef.updateChildren(dataToSave as Map<String, Any>)

                    // Gửi mã OTP qua email
                    val emailSender = EmailSender(email)
                    emailSender.setSubject("FreshCard - Quên mật khẩu")
                    emailSender.setBody("Mã OTP của bạn để đặt lại mật khẩu là: $otpCode")
                    emailSender.send()

                    // Schedule deletion after 2 seconds
                    Handler(Looper.getMainLooper()).postDelayed({
                        userRef.child("forgotPasswordCode").removeValue()
                        userRef.child("forgotPasswordCodeTime").removeValue()
                    }, 2000)

                    // Trả về kết quả thành công
                    onResult(hashMapOf("state" to true, "message" to "Đã gửi mã OTP đến email của bạn"))
                } else {
                    // Email không tồn tại
                    onResult(hashMapOf("state" to false, "message" to "Email không tồn tại"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Lỗi database
                onResult(hashMapOf("pair" to Pair(true, "Đã gửi mã OTP đến email của bạn")))
            }
        }
        query.addListenerForSingleValueEvent(valueEventListener)
    }

    fun checkForgotPasswordCode(email: String, enteredCode: String, onResult: (HashMap<String, Any?>?) -> Unit) {
        val query = db.orderByChild("email").equalTo(email)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Lấy dữ liệu của user từ snapshot
                    val userSnapshot = snapshot.children.iterator().next()
                    // Lấy mã OTP đã lưu trong database
                    val storedCode = userSnapshot.child("forgotPasswordCode").value?.toString()
                    // Lấy thời gian mã OTP được tạo ra
                    val storedCodeTime = userSnapshot.child("forgotPasswordCodeTime").value as Long?

                    // Kiểm tra xem mã OTP nhập vào có khớp với mã trong database không
                    if (storedCode != null && storedCode == enteredCode && storedCodeTime != null) {
                        // Kiểm tra xem mã OTP có còn hợp lệ không (ví dụ: trong khoảng thời gian nhất định)
                        val currentTime = System.currentTimeMillis()
                        val expirationTime = 2 * 60 * 1000 // 2 phút trong milliseconds

                        if (currentTime - storedCodeTime <= expirationTime) {
                            // Mã OTP hợp lệ
                            onResult(hashMapOf("state" to true, "message" to "Mã OTP hợp lệ"))
                        } else {
                            // Mã OTP đã hết hạn
                            onResult(hashMapOf("state" to false, "message" to "Mã OTP đã hết hạn"))
                        }
                    } else {
                        // Mã OTP không đúng
                        onResult(hashMapOf("state" to false, "message" to "Mã OTP không đúng"))
                    }
                } else {
                    // Email không tồn tại
                    onResult(hashMapOf("state" to false, "message" to "Email không tồn tại"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Lỗi từ database
                onResult(hashMapOf("state" to false, "message" to "Lỗi database"))
            }
        }
        query.addListenerForSingleValueEvent(valueEventListener)
    }

}

