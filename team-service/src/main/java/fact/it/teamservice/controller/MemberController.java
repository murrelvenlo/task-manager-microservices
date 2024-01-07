package fact.it.teamservice.controller;

import fact.it.teamservice.dto.*;
import fact.it.teamservice.exception.EntityCreationException;
import fact.it.teamservice.exception.EntityNotFoundException;
import fact.it.teamservice.model.Member;
import fact.it.teamservice.service.MemberService;
import fact.it.teamservice.validator.MemberValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberValidator memberValidator;

    @PostMapping("/add")
    public ResponseEntity<String> addMember(@RequestBody MemberRequest request) {
        try {
            // validate member request
            Map<String, List<String>> validationErrors = memberValidator.validateMemberRequest(request);

            // If validation succeeds, proceed with user registration
            if (!validationErrors.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrors.toString());
            }
            Member newMember = memberService.addMember(request);
            String fullName = newMember.getFirstName() + " " + newMember.getLastName();
            String successMessage = "The member, " + fullName + ", is successfully added.";
            return ResponseEntity.ok(successMessage);
        } catch (EntityCreationException e) {
            // Handle the exception and provide a specific error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating member: " + e.getMessage());
        }
    }

    @PostMapping("/addMemberToTeam/{teamNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addMemberToTeam(
            @PathVariable String teamNumber,
            @RequestBody MemberRequest memberRequest) {
        memberService.addMemberToTeam(teamNumber, memberRequest.getRNumber());
    }

    @GetMapping("/get/all")
    public List<MemberResponse> getAllMembers() {
        return memberService.getAllMembers();
    }

    @GetMapping("/get/{rNumber}")
    public MemberResponse findMember(@PathVariable String rNumber) {
        return memberService.findMemberByrNumber(rNumber);
    }

    @DeleteMapping("/delete/{rNumber}")
    public ResponseEntity<String> deleteMember(@PathVariable String rNumber) {
        try {
            memberService.deleteByrNumber(rNumber);
            return new ResponseEntity<>("Member deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/update/{rNumber}")
    public ResponseEntity<?> updateMember(@PathVariable String rNumber, @RequestBody UpdateMemberRequest request) {
        try {
            memberService.updateMember(rNumber, request);
            return ResponseEntity.ok("Member updated successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found with RNumber: " + rNumber);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating member with RNumber: " + rNumber);
        }
    }

    @DeleteMapping("/remove-from-team")
    public ResponseEntity<String> removeMemberFromTeam(@RequestParam Long teamId, @RequestParam String rNumber) {
        try {
            memberService.removeMemberFromTeam(teamId, rNumber);
            return ResponseEntity.ok("Member removed from team successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing member from team: " + e.getMessage());
        }
    }

}
