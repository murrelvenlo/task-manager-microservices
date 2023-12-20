package fact.it.apigateway.validator;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/login", "/api/auth/register", "/api/auth/validate-token",
            "/api/user-task/update/**", "/api/user-task/addForCurrentUser", "/api/delete/**", "/api/user-task/getAllTasksForCurrentUser"
    );

    public Predicate<ServerHttpRequest> isSecured =
            serverHttpRequest -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> serverHttpRequest.getURI()
                            .getPath()
                            .contains(uri));
}
