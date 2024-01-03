package fact.it.teamservice.validator;

import fact.it.teamservice.dto.MemberRequest;
import fact.it.teamservice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

@Component
@RequiredArgsConstructor
public class MemberValidator {
    private final MemberRepository memberRepository;

    public Map<String, List<String>> validateMemberRequest(MemberRequest request) {
        Map<String, List<String>> validationErrors = new HashMap<>();

        validateField(() -> memberRepository.existsByrNumber(request.getRNumber()),
                "rNumber", "Member with the same rNumber already exists", validationErrors);

        return validationErrors;

    }

    private void validateField(BooleanSupplier condition, String fieldName, String errorMessage, Map<String, List<String>> validationErrors) {
        if (condition.getAsBoolean()) {
            validationErrors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        }
    }
}
