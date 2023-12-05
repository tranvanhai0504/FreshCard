package com.example.freshcard.Structure

import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage



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

        // TODO: Implement actual email sending using an email library or API
        // Example using JavaMailSender with Gmail SMTP server
        val props: Properties = System.getProperties()
        props.put("mail.smtp.host", "smtp.gmail.com")
        props.put("mail.smtp.auth", "true")
        props.put("mail.smtp.starttls.enable", "true")
        props.put("mail.smtp.port", "465")

        val session = Session.getInstance(props,
            object : Authenticator() {
                protected override fun getPasswordAuthentication(): PasswordAuthentication {
                    val appPassword = "rngbcimomrdqbvzc"
                    return PasswordAuthentication("freshcard01@gmail.com", appPassword)
                }

            })

        val message = MimeMessage(session)
        message.addRecipient(Message.RecipientType.TO, InternetAddress(recipientEmail))
        message.subject = subject
        message.setContent(body, "text/html; charset=utf-8")

        Transport.send(message)

        // Replace with your preferred email sending implementation
        println("Email sent to $recipientEmail with subject: $subject and body: $body")
    }
}

