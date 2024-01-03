package fact.it.teamservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String depCode;

    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Member> members = new ArrayList<>();
}
