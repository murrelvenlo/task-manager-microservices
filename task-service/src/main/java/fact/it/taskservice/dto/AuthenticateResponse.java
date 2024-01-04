package fact.it.taskservice.dto;

public record AuthenticateResponse(String jwtToken, MemberResponse user) {
}
