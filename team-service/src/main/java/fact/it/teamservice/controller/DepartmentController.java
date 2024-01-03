package fact.it.teamservice.controller;

import fact.it.teamservice.dto.DepartmentRequest;
import fact.it.teamservice.dto.DepartmentResponse;
import fact.it.teamservice.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dep")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping("/add")
    public ResponseEntity<String> addDepartment(@RequestBody DepartmentRequest request) {
        String depName = request.getName();
        departmentService.addDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Department '" + depName + "' added successfully!");
    }

    @GetMapping("/get/all")
    public List<DepartmentResponse> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    @GetMapping("/get/{depCode}")
    public DepartmentResponse findDepartment(@PathVariable String depCode) {
        return departmentService.findDepartmentByCode(depCode);
    }
}
