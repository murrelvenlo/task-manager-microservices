package fact.it.emailservice;

import fact.it.emailservice.dto.MailDto;
import fact.it.emailservice.model.EmailDetails;
import fact.it.emailservice.repository.MailRepository;
import fact.it.emailservice.service.Impl.MailServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thymeleaf.TemplateEngine;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EmailServiceApplicationTests {
    @InjectMocks
    private MailServiceImpl mailService;

    @Mock
    private MailRepository mailRepository;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    public void testSendEmailNotification() {
        // Arrange
        MailDto mailDto = new MailDto();
        mailDto.setMailCode("Mail code");
        mailDto.setRecipient("venlo.mj@hotmail.nl");
        mailDto.setMessageSubject("Mail test subject");
        mailDto.setMessageBody("Simple mail body with no real text.");
        mailDto.setMessageAttachment("");

        // Mock UUID generation
//        UUID mockUuid = UUID.randomUUID();
//        doReturn(mockUuid).when(java.util.UUID.class);
//        java.util.UUID.randomUUID();

        // Capture the argument sent to javaMailSender.send()
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        // Mock the javaMailSender behavior
        doThrow(new RuntimeException()).when(javaMailSender).send(any(SimpleMailMessage.class));

        // Act
        mailService.sendEmailNotification(mailDto);

        // Assert
        verify(javaMailSender, times(1)).send(captor.capture());
        verify(mailRepository, times(1)).save(any(EmailDetails.class));

        SimpleMailMessage sentMessage = captor.getValue();
        assertEquals("venlo.mj@hotmail.nl", sentMessage.getFrom());
        assertEquals("venlo.mj@hotmail.nl", sentMessage.getTo()[0]); // Assuming only one recipient
        assertEquals("Mail test subject", sentMessage.getSubject());
        assertEquals("Simple mail body with no real text.", sentMessage.getText());
        assertEquals("Mail code", mailDto.getMailCode());
    }

}
