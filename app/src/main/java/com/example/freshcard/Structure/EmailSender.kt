package com.example.freshcard.Structure

import com.sendgrid.*
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email

class EmailSender(private val recipientEmail: String) {

    private var subject: String? = null
    private var body: String? = null

    fun setSubject(subject: String): EmailSender {
        this.subject = subject
        return this
    }

    fun setBody(body: String): EmailSender {
        this.body = body
        return this
    }

    fun send() {
        try {
            // Lấy giá trị API Key từ biến môi trường
            val apiKey = "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"
            if (apiKey == null || apiKey.isEmpty()) {
                println("API Key is null or empty.")
                return
            }

            // Tạo đối tượng SendGrid
            val sg = SendGrid(apiKey)

            // Tạo đối tượng Email từ và đến
            val from = Email("Freshcard01@gmail.com")
            val to = Email(recipientEmail)

            // Tạo nội dung email
            val content = Content("text/plain", body)

            // Tạo đối tượng Mail
            val mail = Mail(from, subject, to, content)

            // Tạo đối tượng Request
            val request = Request()

            // Cấu hình thông tin Request
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = mail.build()

            // Gửi email và nhận Response
            val response = sg.api(request)

            // In log với thông tin Response
            println("Email sent to $recipientEmail with subject: $subject and body: $body. Response: ${response.statusCode}")
        } catch (e: Exception) {
            // In log nếu có lỗi xảy ra
            println("Error sending email: ${e.message}")
        }
    }
}
