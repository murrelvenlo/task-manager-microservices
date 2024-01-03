package fact.it.teamservice.repository;

import fact.it.teamservice.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findByTeamNumber(String teamNumber);
    boolean existsByName(String name);
}
