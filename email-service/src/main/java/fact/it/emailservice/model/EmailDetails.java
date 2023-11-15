package fact.it.emailservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "emails")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EmailDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private UUID mailCode;
    private String recipient;
    private String messageBody;
    private String messageSubject;
    private String messageAttachment;
    private String taskCode;
    private String userCode;

}
