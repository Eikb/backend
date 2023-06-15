package de.vikz.wumtbackend.user;

import de.vikz.wumtbackend.exam.Exam;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String firstName;
    private String lastName;
    private String role;
    private String university;
    private String semester;
    private Boolean enabled;
    private Integer examsTaken;
    @ManyToMany
    private List<Exam> writtenExams;


}
