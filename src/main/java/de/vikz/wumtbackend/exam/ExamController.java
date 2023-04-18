package de.vikz.wumtbackend.exam;

import de.vikz.wumtbackend.question.Question;
import de.vikz.wumtbackend.question.QuestionRepository;
import de.vikz.wumtbackend.questionCatalog.QuestionCatalog;
import de.vikz.wumtbackend.questionCatalog.QuestionCatalogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/v1/exam")
public class ExamController {
    private final ExamRepository examRepository;
    private final QuestionCatalogRepository questionCatalogRepository;
    private final QuestionRepository questionRepository;

    public ExamController(ExamRepository examRepository,
                          QuestionCatalogRepository questionCatalogRepository,
                          QuestionRepository questionRepository) {
        this.examRepository = examRepository;
        this.questionCatalogRepository = questionCatalogRepository;
        this.questionRepository = questionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Exam>> getAllExams() {
        return ResponseEntity.ok(examRepository.findAll());
    }

    @PostMapping("/{catalogId}")
    public Object createExam(@RequestBody Exam exam, @PathVariable("catalogId") Integer catalogId) {
        QuestionCatalog catalog = questionCatalogRepository.findById(catalogId).stream().iterator().next();
        List<Question> finalQuestions = new ArrayList<>(Collections.EMPTY_LIST);


        List<List<Question>> questionsFiltered = new ArrayList<>();
        if (Objects.equals(exam.getExamType(), "static")) {
            List<Question> staticQuestions = exam.getStaticQuestions();
        }


        if (Objects.equals(exam.getExamType(), "random")) {

            Integer value = exam.getCategoryWithNumber().size();
            for (int i = 0; i <= value - 1; i++) {
                assert catalog.getCategories() != null;
                String nameOfCategory = catalog.getCategories().get(i).getName();
                Integer haufigkeit = exam.getCategoryWithNumber().get(nameOfCategory);

                //Muss random gemacht werden
                questionsFiltered.add(catalog.getQuestions().stream().filter(e -> e.getCategory().getName().equals(nameOfCategory)).toList());
                Integer randomNum;
                List<Integer> usedInts = new ArrayList<>();
                for (int j = 0; j <= haufigkeit - 1; j++) {
                    if (haufigkeit > questionsFiltered.get(i).size()) {
                        return ResponseEntity.status(404);
                    }

                    do {
                        randomNum = ThreadLocalRandom.current().nextInt(0, questionsFiltered.get(i).size());
                    } while (usedInts.contains(randomNum));

                    usedInts.add(randomNum);
                    assert exam.getQuestionList() != null;
                    finalQuestions.add(questionsFiltered.get(i).get(randomNum));

                }
            }

            exam.setQuestionList(finalQuestions.stream().toList());
            examRepository.save(exam);

        }
        return ResponseEntity.ok("Erstellt");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExam(@PathVariable("id") Integer examId) {
        examRepository.deleteById(examId);
        return ResponseEntity.ok("Deleted");
    }
}
