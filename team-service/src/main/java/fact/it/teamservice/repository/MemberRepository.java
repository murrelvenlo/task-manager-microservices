package fact.it.teamservice.repository;

import fact.it.teamservice.model.Department;
import fact.it.teamservice.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByrNumber(String rNumber);
    boolean existsByrNumber(String rNumber);
    void deleteByrNumber(String rNumber);
    List<Member> findByDepartmentName(String depName);
    List<Member> findByDepartment(Department department);
    List<Member> findByrNumberAndDepartment(String rNumber, Department department);
    List<Member> findByrNumberIn(List<String> rNumbers);
}
