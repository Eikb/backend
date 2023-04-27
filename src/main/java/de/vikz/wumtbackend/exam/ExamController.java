package de.vikz.wumtbackend.exam;

import de.vikz.wumtbackend.config.JwtService;
import de.vikz.wumtbackend.question.Question;
import de.vikz.wumtbackend.question.QuestionRepository;
import de.vikz.wumtbackend.questionCatalog.QuestionCatalog;
import de.vikz.wumtbackend.questionCatalog.QuestionCatalogRepository;
import de.vikz.wumtbackend.user.User;
import de.vikz.wumtbackend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/v1")
public class ExamController {
    private final ExamRepository examRepository;
    private final QuestionCatalogRepository questionCatalogRepository;
    private final QuestionRepository questionRepository;
    @Autowired
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public ExamController(ExamRepository examRepository,
                          QuestionCatalogRepository questionCatalogRepository,
                          QuestionRepository questionRepository, JwtService jwtService,
                          UserRepository userRepository) {
        this.examRepository = examRepository;
        this.questionCatalogRepository = questionCatalogRepository;
        this.questionRepository = questionRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @GetMapping("/publicexam")
    public ResponseEntity<List<Exam>> getAllExams() {
        return ResponseEntity.ok(examRepository.findAll());
    }

    @PostMapping("/exam/{catalogId}")
    public Object createExam(@RequestBody Exam exam, @PathVariable("catalogId") String catalogName) {
        QuestionCatalog catalog = questionCatalogRepository.findByName(catalogName);
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

    @DeleteMapping("/exam/{id}")
    public ResponseEntity<String> deleteExam(@PathVariable("id") Integer examId) {
        List<Exam> writeenExam = new ArrayList<>();
        userRepository.findAll().forEach(e -> e.setWrittenExams(writeenExam));
        userRepository.saveAll(userRepository.findAll());
        examRepository.deleteAll();
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/written")
    public ResponseEntity<List<Exam>> getAllWrittenExams(@RequestHeader("Authorization") String bearerToken) {
        bearerToken = bearerToken.substring(7);
        User user = userRepository.findByEmail(jwtService.extractUsername(bearerToken)).get();
        return ResponseEntity.ok(user.getWrittenExams());
    }

    @PostMapping("/exampublic/answers/{id}")
    public ResponseEntity<String> checkAnswers(@RequestBody List<String> answers, @PathVariable("id") Integer examId, @RequestHeader("Authorization") String bearerToken) {
        Exam exam = examRepository.findById(examId).get();
        List<Question> answersList = exam.getQuestionList();
        List<Question> correctAnswers = new ArrayList<>();
        List<Question> falseAnswers = new ArrayList<>();

        List<List<Question>> collectedList = new ArrayList<>();

        for (int i = 0; i < answersList.size(); i++) {
            if (answers.get(i).equals(answersList.get(i).getCorrectAnswer())) {
                correctAnswers.add(answersList.get(i));
            } else {
                falseAnswers.add(answersList.get(i));
            }

        }
        collectedList.add(correctAnswers);
        collectedList.add(falseAnswers);
        bearerToken = bearerToken.substring(7);

        String username = jwtService.extractUsername(bearerToken);
        User user = userRepository.findByEmail(username).stream().iterator().next();
        assert user.getWrittenExams() != null;
        user.getWrittenExams().add(exam);
        userRepository.save(user);
        return ResponseEntity.ok("Antworten wurden korrigiert");

    }
}
