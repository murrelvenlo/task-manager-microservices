package fact.it.teamservice.service.Impl;

import fact.it.teamservice.dto.DepartmentResponse;
import fact.it.teamservice.dto.MailDto;
import fact.it.teamservice.dto.MemberRequest;
import fact.it.teamservice.dto.MemberResponse;
import fact.it.teamservice.exception.DuplicateEntityException;
import fact.it.teamservice.exception.EntityNotFoundException;
import fact.it.teamservice.model.Department;
import fact.it.teamservice.model.Member;
import fact.it.teamservice.model.Team;
import fact.it.teamservice.repository.DepartmentRepository;
import fact.it.teamservice.repository.MemberRepository;
import fact.it.teamservice.repository.TeamRepository;
import fact.it.teamservice.service.DepartmentService;
import fact.it.teamservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final DepartmentRepository departmentRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final ModelMapper mapper;
    private final WebClient webClient;
    @Override
    public Member addMember(MemberRequest memberRequest) {
        // Check if the department name is provided
        if (memberRequest.getDepName() == null || memberRequest.getDepName().trim().isEmpty()) {
            throw new IllegalArgumentException("Department name must be provided in the request.");
        }

        // Check if the department with the given name exists
        Optional<Department> departmentOptional = Optional.ofNullable(departmentRepository.findByName(memberRequest.getDepName()));

        // If the department doesn't exist, throw an exception
        Department department = departmentOptional.orElseThrow(() ->
                new EntityNotFoundException("Department", "Department with name " + memberRequest.getDepName() + " not found"));

        // Check if a member with the same RNumber already exists
        if (memberRepository.existsByrNumber(memberRequest.getRNumber())) {
            throw new DuplicateEntityException("Member", "Member with the same RNumber, " + memberRequest.getRNumber() + ", already exists");
        }


        Member newMember = Member.builder()
                .rNumber(generateRNumber())
                .firstName(memberRequest.getFirstName())
                .lastName(memberRequest.getLastName())
                .email(memberRequest.getEmail())
                .department(department)
                .build();

        memberRepository.save(newMember);
        sendUserCreationEmail(memberRequest);
        return newMember;
    }

    @Override
    public List<MemberResponse> getAllMembers() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(member -> mapper.map(member, MemberResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public MemberResponse findMemberByrNumber(String rNumber) {
        Member member = memberRepository.findByrNumber(rNumber);
        if (member != null) {
            return mapper.map(member, MemberResponse.class);
        }
        return null;
    }

    @Override
    public void updateMember(String rNumber, MemberRequest request) {
        // Check if the department with the given name exists
        Optional<Department> departmentOptional = Optional.ofNullable(departmentRepository.findByName(request.getDepName()));
        // If the department doesn't exist, throw an exception
        Department department = departmentOptional.orElseThrow(() ->
                new EntityNotFoundException("Department", "Department with name " + request.getDepName() + " not found"));

        // Retrieve the existing member
        Member existingMember = memberRepository.findByrNumber(rNumber);
        if (existingMember == null) {
            throw new EntityNotFoundException("Member", "Member with RNumber " + rNumber + " not found");
        }

        // Update the existing member's fields
        existingMember.setFirstName(request.getFirstName());
        existingMember.setLastName(request.getLastName());
        existingMember.setEmail(request.getEmail());
        existingMember.setDepartment(department);

        // Save the updated member back to the repository
        memberRepository.save(existingMember);
    }

    @Override
    @Transactional
    public void deleteByrNumber(String rNumber) {
        Optional<Member> member = Optional.ofNullable(memberRepository.findByrNumber(rNumber));
        if (member.isPresent()) {
            memberRepository.deleteByrNumber(rNumber);
        } else {
            throw new RuntimeException("Member with r_number " + rNumber + " not found");
        }

    }

    @Override
    public void addMemberToTeam(String teamNumber, String rNumber) {
        try {
            Team team = teamRepository.findByTeamNumber(teamNumber);
            Member member = memberRepository.findByrNumber(rNumber);

            if (team != null && member != null) {
                member.setTeam(team);
                memberRepository.save(member);
            } else {
                throw new RuntimeException("Member with r_number " + rNumber + " or " + teamNumber + " not found");
            }
        } catch (EntityNotFoundException ex) {
            // Handle the exception, log a message, or rethrow if necessary
            throw ex;
        } catch (Exception ex) {
            // Handle other exceptions, log a message, or rethrow if necessary
            throw new RuntimeException("An error occurred while processing the request", ex);
        }
    }

    private static String generateRNumber() {
        Random random = new Random();

        // Generate 7 random digits and concatenate them to "r"
        String code = IntStream.range(0, 7)
                .mapToObj(i -> String.valueOf(random.nextInt(10)))
                .collect(Collectors.joining());

        return "r"+code;
//        StringBuilder stringBuilder = new StringBuilder();
//        Random random = new Random();
//
//        for (int i = 0; i < 7; i++) {
//            int randomDigit = random.nextInt(10);
//            stringBuilder.append(randomDigit);
//        }
//        return stringBuilder.toString();
    }

    private void sendUserCreationEmail(MemberRequest memberRequest) {
        // Create a MailDto with user information
        MailDto mailDto = MailDto.builder()
                .recipient(memberRequest.getEmail())
                .messageSubject("Member Created")
                .messageBody("Dear " + memberRequest.getFirstName() + ",\nYour account has been successfully created.")
                .build();

        // Send the email using WebClient to the mail-service
        webClient.post()
                .uri("http://localhost:8082/api/email/send-email")
                .bodyValue(mailDto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
