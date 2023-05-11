package de.vikz.wumtbackend.exam;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_results")
public class Results {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;


    private String firstName;
    private String lastName;
    private String userName;

    @Transient
    private List<Object> correctAnswers;
    @Transient
    private List<Object> falseAnswers;

    private Integer correctAnswersNumber;
    private Integer falseAnswersNumber;


    @OneToMany
    private List<CheckResult> checkResult;

    private Integer resultInPercent;

    @OneToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;


}
