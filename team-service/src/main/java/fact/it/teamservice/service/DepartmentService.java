package fact.it.teamservice.service;

import fact.it.teamservice.dto.DepartmentRequest;
import fact.it.teamservice.dto.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    void addDepartment(DepartmentRequest departmentRequest);
    List<DepartmentResponse> getAllDepartments();
    DepartmentResponse findDepartmentByCode(String depCode);
}
