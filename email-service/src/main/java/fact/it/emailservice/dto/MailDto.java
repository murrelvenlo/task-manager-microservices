package fact.it.emailservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailDto {
    private String mailCode;
    private String recipient;
    private String messageBody;
    private String messageSubject;
    private String messageAttachment;
//    private String taskCode;
//    private String userCode;
}
