package fact.it.teamservice.service.Impl;

import fact.it.teamservice.dto.DepartmentRequest;
import fact.it.teamservice.dto.DepartmentResponse;
import fact.it.teamservice.exception.DuplicateEntityException;
import fact.it.teamservice.model.Department;
import fact.it.teamservice.model.Member;
import fact.it.teamservice.repository.DepartmentRepository;
import fact.it.teamservice.repository.MemberRepository;
import fact.it.teamservice.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper mapper;
    @Override
    public void addDepartment(DepartmentRequest departmentRequest) {
        if (departmentRepository.existsByName(departmentRequest.getName())) {
            throw new DuplicateEntityException("Department", "Department with the same name, " + departmentRequest.getName() + ", already exists");
        }
        Department department = Department.builder()
                .depCode(UUID.randomUUID().toString())
                .name(departmentRequest.getName())
                .build();

        departmentRepository.save(department);
    }


    @Override
    public List<DepartmentResponse> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(department -> mapper.map(department, DepartmentResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentResponse findDepartmentByCode(String depCode) {
        Department department = departmentRepository.findByDepCode(depCode);
        if (department != null) {
            return mapper.map(department, DepartmentResponse.class);
        }
        return null;
    }



}
