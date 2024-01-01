package fact.it.userservice.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String userCode;
    private String firstName;
    private String lastName;
    private String username;
    @NotEmpty
    @Email
    @Column(unique = true)
    private String email;
    @Size(min = 6, max = 225, message = "De size should be between 6 and 225")
    private String password;
    private String phone;
    private String taskCode;
}
