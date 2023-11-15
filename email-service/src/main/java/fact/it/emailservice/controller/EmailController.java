package fact.it.emailservice.controller;

import fact.it.emailservice.dto.MailDto;
import fact.it.emailservice.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {
    private final MailService mailService;

    @PostMapping("/send-email")
    public ResponseEntity<String> sendMail(@RequestBody MailDto mailDto) {
        try {
            mailService.sendEmailNotification(mailDto);
            return new ResponseEntity<>("Email with the code, " + mailDto.getMailCode() +  " sent successfully", HttpStatus.OK);
        } catch (Exception exc){
            return new ResponseEntity<>("Failed to send email: " + exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
