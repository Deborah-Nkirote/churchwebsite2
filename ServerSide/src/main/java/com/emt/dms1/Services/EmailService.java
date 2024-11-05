package com.emt.dms1.Services;
import com.emt.dms1.Models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(UserModel user, String token) {
        String recipientAddress = user.getEmail();
        String subject = "Password Reset Request";
        String resetUrl = "https://yourapp.com/reset-password?token=" + token; // Replace with your app's reset URL
        String message = "Dear " + user.getUsername() + ",\n\n" +
                "We received a request to reset your password. Click the link below to set a new password:\n\n" +
                resetUrl + "\n\n" +
                "If you did not request a password reset, please ignore this email.\n\n" +
                "Thank you,\n" +
                "YourApp Team";

        // Set up the email with necessary details
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);

        // Send the email
        mailSender.send(email);  // mailSender is an instance of JavaMailSender, injected via dependency injection
    }

}
