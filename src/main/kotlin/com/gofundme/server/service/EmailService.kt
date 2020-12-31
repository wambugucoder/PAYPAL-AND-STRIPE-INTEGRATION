package com.gofundme.server.service

import com.gofundme.server.requestHandler.RegisterHandler
import com.gofundme.server.responseHandler.LogStreamResponse
import io.rocketbase.commons.colors.ColorPalette
import io.rocketbase.commons.email.EmailTemplateBuilder
import io.rocketbase.commons.email.model.HtmlTextEmail
import io.rocketbase.commons.email.template.styling.FontWeight
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class EmailService {

    @Autowired
    lateinit var javaMailSender: JavaMailSender

    @Autowired
    lateinit var logStream: LogStream

    fun sendAccountActivationEmail(token: String?,registerHandler: RegisterHandler) {
        val builder = EmailTemplateBuilder.builder()
        val header = "test"

        val htmlTextEmail: HtmlTextEmail = builder
            //.header()
            //.and()
            .logo("https://www.rocketbase.io/img/logo-dark.png","",0,41)
            .and()
            .addText("Welcome, ${registerHandler.username}!").fontWeight(FontWeight.BOLD).center()
            .and()
            .addText("Thanks for Registering with GoFundMe. We’re thrilled to have you on board. To get started you will need to verify your Account:")
            .and()
            .addButton("Verify Account", "https://localhost:8443/api/v1/auth/activate/$token").color(ColorPalette.FRENCH_BLUE)
            .and()
            .addHtml(
                "If you have any questions, feel free to <a href=\"mailto:gofundme@gfm.com\">email our customer success team</a>. (We're lightning quick at replying.) We also offer <a href=\"{{live_chat_url}}\">live chat</a> during business hours."
            ).and()
            .addText(
                """
        Cheers,
        The GoFundMe Team
        """.trimIndent()
            ).and()
            .copyright("rocketbase").url("https://www.rocketbase.io")
            .and()
            .addPlainTextFooter(
                """
        [GoFundMe, LLC]
        1234 Street Rd.
        Suite 1234
        """.trimIndent()
            ).and()
            .addImage("https://cdn.rocketbase.io/assets/loading/no-image.jpg","",100,0)
            .linkUrl("https://www.rocketbase.io").and()
            .build()

        createAndSendEmail(htmlTextEmail,registerHandler)

    }
    fun createAndSendEmail(htmlTextEmail: HtmlTextEmail,registerHandler: RegisterHandler){
        val message=javaMailSender.createMimeMessage()
        val messageHelper = MimeMessageHelper(message,MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,StandardCharsets.UTF_8.name())
        messageHelper.setTo(registerHandler.email)
        messageHelper.setSubject("GoFundMe Account Activation")
        messageHelper.setText(htmlTextEmail.text,htmlTextEmail.html)
        messageHelper.setFrom("gofundMeTeam@gfm.com")

        try {
            javaMailSender.send(message)
        }
        catch (e:MailException){
            logStream.sendToLogConsole(LogStreamResponse("ERROR","EmailService","$e"))
        }

        logStream.sendToLogConsole(LogStreamResponse("INFO","EmailService","An Account Activation Email has been sent to ${registerHandler.email}"))
    }
}

