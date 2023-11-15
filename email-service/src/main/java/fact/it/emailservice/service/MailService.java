package fact.it.emailservice.service;

import fact.it.emailservice.dto.MailDto;

public interface MailService {
    void sendEmailNotification(MailDto mailDto);
}
