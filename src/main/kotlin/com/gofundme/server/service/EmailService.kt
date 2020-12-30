package com.gofundme.server.service

import io.rocketbase.commons.colors.ColorPalette
import io.rocketbase.commons.email.EmailTemplateBuilder
import io.rocketbase.commons.email.model.HtmlTextEmail
import io.rocketbase.commons.email.template.styling.FontWeight
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class EmailService {

    @Autowired
    lateinit var javaMailSender: JavaMailSender

    fun sendAccountActivationEmail(token: String?) {
        val builder = EmailTemplateBuilder.builder()
        val header = "test"

        val htmlTextEmail: HtmlTextEmail = builder
            //.header()
            //.and()
            .logo("https://www.rocketbase.io/img/logo-dark.png","",0,41)
            .and()
            .addText("Welcome, {{name}}!").fontWeight(FontWeight.BOLD).center()
            .and()
            .addText("Thanks for trying [Product Name]. We’re thrilled to have you on board. To get the most out of [Product Name], do this primary next step:")
            .and()
            .addButton("Do this Next", "http://localhost").color(ColorPalette.FRENCH_BLUE)
            .and()
            .addHtml(
                "If you have any questions, feel free to <a href=\"mailto:{{support_email}}\">email our customer success team</a>. (We're lightning quick at replying.) We also offer <a href=\"{{live_chat_url}}\">live chat</a> during business hours."
            ).and()
            .addText(
                """
        Cheers,
        The [Product Name] Team
        """.trimIndent()
            ).and()
            .copyright("rocketbase").url("https://www.rocketbase.io")
            .and()
            .addPlainTextFooter(
                """
        [Company Name, LLC]
        1234 Street Rd.
        Suite 1234
        """.trimIndent()
            ).and()
            .addImage("https://cdn.rocketbase.io/assets/loading/no-image.jpg","",100,0)
            .linkUrl("https://www.rocketbase.io").and()
            .build()

        createAndSendEmail(htmlTextEmail)

    }
    fun createAndSendEmail(htmlTextEmail: HtmlTextEmail):String{
        val message=javaMailSender.createMimeMessage()
        val messageHelper = MimeMessageHelper(message,MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,StandardCharsets.UTF_8.name())
        messageHelper.setTo("josyw77@gmail.com")
        messageHelper.setSubject("Trial")
        messageHelper.setText(htmlTextEmail.text,htmlTextEmail.html)
        messageHelper.setFrom("gofundMeTeam@gfm.com")
        javaMailSender.send(message)

        return "Done"
    }
}

