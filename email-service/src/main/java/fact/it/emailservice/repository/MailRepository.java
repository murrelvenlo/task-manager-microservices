package fact.it.emailservice.repository;

import fact.it.emailservice.model.EmailDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepository extends JpaRepository<EmailDetails, Long> {
}
