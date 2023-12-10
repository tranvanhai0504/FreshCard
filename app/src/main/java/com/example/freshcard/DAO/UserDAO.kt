package com.example.freshcard.DAO


import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.freshcard.Structure.Database

import com.example.freshcard.Structure.EmailSender

import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.TopicItem

import com.example.freshcard.Structure.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

import java.util.Random
import android.os.Handler
import android.os.Looper
import android.util.Log

import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


public class UserDAO() {
    var db: DatabaseReference = Database().getReference("users")
    var topicRef: DatabaseReference = Database().getReference("topics")

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

    suspend fun getUserInfor(id : String) : DataSnapshot{
        val result = db.child(id).get().await()
        return result
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

                    // Schedule deletion after 2 minutes
                    Handler(Looper.getMainLooper()).postDelayed({
                        userRef.child("forgotPasswordCode").removeValue()
                        userRef.child("forgotPasswordCodeTime").removeValue()
                    }, 2 * 60 * 1000)
                    sendForgotPasswordEmail(email, otpCode)
                    // Trả về kết quả thành công
                    onResult(hashMapOf("state" to true, "message" to "Encrypted OTP sent to your email", "otp" to otpCode))

                } else {
                    // Email không tồn tại
                    onResult(hashMapOf("state" to false, "message" to "Email does not exist"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Lỗi database
                onResult(hashMapOf("state" to false, "message" to "An error has occurred"))
            }
        }

        query.addListenerForSingleValueEvent(valueEventListener)
    }

    private fun sendForgotPasswordEmail(email: String, otpCode: String) {
        GlobalScope.launch(Dispatchers.IO) {
            // Gửi email sử dụng thư viện JavaMail
            val senderEmail = "freshcard01@gmail.com"
            val senderPassword = "qrngpzsdxdlmzbjj"

            val properties = Properties()
            properties["mail.smtp.auth"] = "true"
            properties["mail.smtp.starttls.enable"] = "true"
            properties["mail.smtp.host"] = "smtp.gmail.com" // Đối với Gmail
            properties["mail.smtp.port"] = "587"

            val session = Session.getInstance(properties, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(senderEmail, senderPassword)
                }
            })

            try {
                val message = MimeMessage(session)
                message.setFrom(InternetAddress(senderEmail))
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
                message.subject = "Forgot Password Code"
                message.setText("Your OTP code is: $otpCode")

                // Gửi email
                Transport.send(message)

                // Log thông báo sau khi gửi thành công (có thể thay thế bằng cách khác)
                Log.d("Email", "Email sent successfully")

            } catch (e: MessagingException) {
                // Log thông báo nếu có lỗi
                Log.e("Email", "Error sending email", e)
            }
        }
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
                            onResult(hashMapOf("state" to true, "message" to "Valid OTP code"))
                        } else {
                            // Mã OTP đã hết hạn
                            onResult(hashMapOf("state" to false, "message" to "OTP code has expired"))
                        }
                    } else {
                        // Mã OTP không đúng
                        onResult(hashMapOf("state" to false, "message" to "OTP code is incorrect"))
                    }
                } else {
                    // Email không tồn tại
                    onResult(hashMapOf("state" to false, "message" to "Email does not exist"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Lỗi từ database
                onResult(hashMapOf("state" to false, "message" to "Lỗi database"))
            }
        }
        query.addListenerForSingleValueEvent(valueEventListener)
    }

    fun resetPassword(email: String, newPassword: String, onResult: (HashMap<String, Any?>?) -> Unit) {
        val query = db.orderByChild("email").equalTo(email)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Lấy đối tượng User từ snapshot
                    val userSnapshot = snapshot.children.iterator().next()
                    val userRef = userSnapshot.ref

                    // Hash mật khẩu mới trước khi lưu vào database
                    val hashedPassword =
                        BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())

                    // Lưu mật khẩu mới vào database
                    userRef.child("password").setValue(hashedPassword)

                    // Trả về kết quả thành công
                    onResult(hashMapOf("state" to true, "message" to "Mật khẩu đã được thay đổi"))
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

    fun getUserById(userId: String, onResult: (User?) -> Unit) {
        val userRef = db.child(userId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Chuyển đổi dữ liệu từ snapshot thành đối tượng User
                    val user = snapshot.getValue(User::class.java)
                    onResult(user)
                } else {
                    // Không tìm thấy người dùng với ID tương ứng
                    onResult(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi đọc dữ liệu từ cơ sở dữ liệu
                onResult(null)
            }
        })
    }

    fun updateUserById(newAvatar: String? = null, Email: String? = null, newFullName: String? = null, newPhoneNumber: String? = null,
                       onResult: (HashMap<String, Any?>) -> Unit
    ) {

        val userRef = db.orderByChild("email").equalTo(Email)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userSnapshot = snapshot.children.iterator().next()
                    val userRef = userSnapshot.ref

                        userRef.child("avatar").setValue(newAvatar)

                        userRef.child("fullName").setValue(newFullName)

                        userRef.child("phoneNumber").setValue(newPhoneNumber)

                    // Trả về kết quả thành công
                    onResult(hashMapOf("state" to true, "message" to "\n" +
                            "User information has been updated"))
                } else {
                    // Không tìm thấy người dùng với ID tương ứng
                    onResult(hashMapOf("state" to false, "message" to "User not found"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi đọc dữ liệu từ cơ sở dữ liệu
                onResult(hashMapOf("state" to false, "message" to "Lỗi database"))
            }
        })
    }

    fun changePassword(email: String, oldPassword: String, newPassword: String, onResult: (HashMap<String, Any?>) -> Unit) {
        val query = db.orderByChild("email").equalTo(email)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Lấy đối tượng User từ snapshot
                    val userSnapshot = snapshot.children.iterator().next()
                    val userRef = userSnapshot.ref

                    // Lấy mật khẩu đã lưu trong database
                    val storedPassword = userSnapshot.child("password").value.toString()

                    // Kiểm tra xem mật khẩu cũ nhập vào có khớp với mật khẩu trong database không
                    val isMatchPassword = BCrypt.verifyer().verify(oldPassword.toCharArray(), storedPassword.toCharArray())

                    if (isMatchPassword.verified) {
                        // Nếu mật khẩu cũ đúng, thì tiến hành thay đổi mật khẩu mới
                        // Hash mật khẩu mới trước khi lưu vào database
                        val hashedPassword = BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())

                        // Lưu mật khẩu mới vào database
                        userRef.child("password").setValue(hashedPassword)

                        // Trả về kết quả thành công
                        onResult(hashMapOf("state" to true, "message" to "Password has been changed"))
                    } else {
                        // Mật khẩu cũ không đúng
                        onResult(hashMapOf("state" to false, "message" to "The old password is incorrect"))
                    }
                } else {
                    // Email không tồn tại
                    onResult(hashMapOf("state" to false, "message" to "Email does not exist"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Lỗi từ database
                onResult(hashMapOf("state" to false, "message" to "Lỗi database"))
            }
        }
        query.addListenerForSingleValueEvent(valueEventListener)
    }



    fun pushTopic(topic: Topic) {
            topicRef.child(topic.id).setValue(topic)
        }

        fun getTopicById(id: String): Topic {
            var topic =
                Topic("", "", "", ArrayList(emptyList<TopicItem>()), false, ArrayList(emptyList()))
            topicRef.child("topics").child(id).get().addOnSuccessListener {
                Log.i("firebase", "Got value ${it.child("title")}")
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
            return topic!!
        }

}

