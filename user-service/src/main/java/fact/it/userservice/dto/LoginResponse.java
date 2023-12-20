package fact.it.userservice.dto;

public record LoginResponse(String jwtToken, UserResponse user) {
}
