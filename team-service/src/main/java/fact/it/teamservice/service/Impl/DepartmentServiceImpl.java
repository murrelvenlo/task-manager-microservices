package fact.it.teamservice.service.Impl;

import fact.it.teamservice.dto.DepartmentRequest;
import fact.it.teamservice.dto.DepartmentResponse;
import fact.it.teamservice.exception.DuplicateEntityException;
import fact.it.teamservice.model.Department;
import fact.it.teamservice.model.Member;
import fact.it.teamservice.model.Team;
import fact.it.teamservice.repository.DepartmentRepository;
import fact.it.teamservice.repository.MemberRepository;
import fact.it.teamservice.service.DepartmentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper mapper;

    @PostConstruct
    public void loadData() {
        if (departmentRepository.count() <= 0) {
            Department department = Department.builder()
                    .name("Application Development")
                    .depCode("APP-3")
                    .build();

            Department department1 = Department.builder()
                    .name("Cloud & Cyber Security")
                    .depCode("CCS-3")
                    .build();

            Department department2 = Department.builder()
                    .name("Artificial intelligence")
                    .depCode("AI-3")
                    .build();

            departmentRepository.save(department);
            departmentRepository.save(department1);
            departmentRepository.save(department2);
        }
    }
    @Override
    public void addDepartment(DepartmentRequest departmentRequest) {
        if (departmentRepository.existsByName(departmentRequest.getName())) {
            throw new DuplicateEntityException("Department", "Department with the same name, " + departmentRequest.getName() + ", already exists");
        }

        // Extracting the first letters of each word in the department name
        String[] words = departmentRequest.getName().split("\\s");
        StringBuilder abbreviation = new StringBuilder();
        for (String word : words) {
            abbreviation.append(word.charAt(0));
        }

        // Building the department code
        String depCode = abbreviation.toString().toUpperCase() + "-3";

        Department department = Department.builder()
                .depCode(depCode)
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
