package fact.it.taskservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailDto {
    private UUID mailCode;
    private String recipient;
    private String messageBody;
    private String messageSubject;
    private String messageAttachment;
}
