package com.SmartEvent.SmartEvent.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    // ✅ Inject both JavaMailSender and TemplateEngine
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    // ✅ Method to send a simple text email (keep your old one)
    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false); // false → plain text

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ New method to send email with HTML template
    public void sendEmailWithTemplate(String to, String subject, String templateName, Context context) {
        try {
            // Generate HTML content from template
            String htmlContent = templateEngine.process(templateName, context);

            // Create a MIME message (supports HTML)
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true → HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    // ✅ Method to send an HTML email with a QR code image
    public void sendEmailWithTemplateAndQr(
            String to,
            String subject,
            String templateName,
            Context context,
            String logoPath
    ) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // true => multipart

            // Generate the HTML content using Thymeleaf
            String htmlContent = templateEngine.process(templateName, context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            if (logoPath != null) {
                String cleanPath = logoPath.startsWith("/") ? logoPath.substring(1) : logoPath;
                helper.addInline("logo", new java.io.File(cleanPath)); // "qrcodeImage" is the name used in the HTML
            }

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send invitation email", e);
        }
    }

}
