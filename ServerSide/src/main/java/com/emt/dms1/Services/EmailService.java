package com.emt.dms1.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetToken(String email, String token) {
        String subject = "Password Reset Request";
        String message = "To reset your password, click the link below:\n"
                + "http://yourdomain.com/reset-password?token=" + token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom("your-email@gmail.com"); // Use the same email as your username

        mailSender.send(mailMessage);
    }
}
