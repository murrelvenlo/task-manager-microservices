package fact.it.userservice.controller;

import fact.it.userservice.dto.LoginRequest;
import fact.it.userservice.dto.LoginResponse;
import fact.it.userservice.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequiredArgsConstructor
public class AuthController {
     Logger logger = LoggerFactory.getLogger(AuthController.class);

     @Autowired
    private AuthService authService;

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login (HttpServletRequest request,
                                                @RequestBody LoginRequest loginRequest) throws Exception {
        logger.info("Executing login");

        ResponseEntity<LoginResponse> response = null;
        response = authService.login(loginRequest);

        return response;
    }
}
