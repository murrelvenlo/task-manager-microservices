package fact.it.emailservice.service.Impl;

import fact.it.emailservice.dto.MailDto;
import fact.it.emailservice.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String sender;
    @Override
    public void sendEmailNotification(MailDto mailDto) {
        // Generate a UUID for the mailCode
        UUID mailCode = UUID.randomUUID();
        mailDto.setMailCode(mailCode);


        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(mailDto.getRecipient());
        message.setSubject(mailDto.getMessageSubject());
        message.setText(mailDto.getMessageBody());
        javaMailSender.send(message);
    }

    // Send the mail with custom mail template for recipient
//    @Override
//    public void sendEmailNotification(MailDto mailDto) {
//        // Generate a UUID for the mailCode
//        UUID mailCode = UUID.randomUUID();
//        mailDto.setMailCode(mailCode);
//
//        // Create Thymeleaf context
//        Context context = new Context();
//        context.setVariable("recipient", mailDto.getRecipient());
//        context.setVariable("messageSubject", mailDto.getMessageSubject());
//        context.setVariable("messageBody", mailDto.getMessageBody());
//
//        // Process Thymeleaf template
//        String emailContent = templateEngine.process("email", context);
//
//
//        try {
//            // Create a MimeMessage
//            MimeMessage message = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setFrom(sender);
//            helper.setTo(mailDto.getRecipient());
//            helper.setText(emailContent, true);
//            javaMailSender.send(message);
//        } catch (MessagingException e) {
//            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
//        }
//    }
}
