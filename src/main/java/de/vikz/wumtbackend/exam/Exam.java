package de.vikz.wumtbackend.exam;

import com.fasterxml.jackson.annotation.JsonFormat;
import de.vikz.wumtbackend.question.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_exam")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String name;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date endTime;
    private String examType;
    private String modul;
    private String status;

    @OneToMany
    private List<Question> staticQuestions;

    @Transient
    private HashMap<String, Integer> categoryWithNumber;

    @OneToMany
    @JoinColumn(name = "question_list_id")
    private List<Question> questionList;

    private Integer numberOfQuestions;

    @ElementCollection
    private List<String> correctAnswers;

    private byte[] pdfDocument;


}
