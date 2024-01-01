package fact.it.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userCode;
    private String firstName;
    private String lastName;
    private String username;
    @Email
    @Column(unique = true)
    private String email;
    @Size(min = 6, max = 225, message = "De size should be between 6 and 225")
    private String password;
    private String phone;
    @ElementCollection
    @CollectionTable(name = "user_task_codes", joinColumns = @JoinColumn(name = "user_code"))
    @Column(name = "task_code")
    private List<String> taskCodes;
    @Enumerated(EnumType.STRING)
    private Role role;
}
