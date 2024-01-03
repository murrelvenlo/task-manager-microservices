package fact.it.teamservice.repository;

import fact.it.teamservice.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Department findByDepCode(String depCode);
    boolean existsByName(String name);
    Department findByName(String name);
    List<Department> findByDepCodeIn(List<String> depCode);
}
