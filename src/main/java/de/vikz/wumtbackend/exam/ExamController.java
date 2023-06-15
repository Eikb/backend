package de.vikz.wumtbackend.exam;

import de.vikz.wumtbackend.config.JwtService;
import de.vikz.wumtbackend.question.Question;
import de.vikz.wumtbackend.question.QuestionRepository;
import de.vikz.wumtbackend.questionCatalog.QuestionCatalog;
import de.vikz.wumtbackend.questionCatalog.QuestionCatalogRepository;
import de.vikz.wumtbackend.user.User;
import de.vikz.wumtbackend.user.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ExamController {
    private final ExamRepository examRepository;
    private final QuestionCatalogRepository questionCatalogRepository;
    private final QuestionRepository questionRepository;
    @Autowired
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ResultsRepository resultsRepository;
    private final CheckResultRepository checkResultRepository;

    public ExamController(ExamRepository examRepository,
                          QuestionCatalogRepository questionCatalogRepository,
                          QuestionRepository questionRepository, JwtService jwtService,
                          UserRepository userRepository,
                          ResultsRepository resultsRepository,
                          CheckResultRepository checkResultRepository) {
        this.examRepository = examRepository;
        this.questionCatalogRepository = questionCatalogRepository;
        this.questionRepository = questionRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.resultsRepository = resultsRepository;
        this.checkResultRepository = checkResultRepository;
    }

    @Operation(summary = "Get All Exams")
    @GetMapping("/publicexam")
    public ResponseEntity<List<Exam>> getAllExams() {
        List<Exam> exams = examRepository.findAll();

        Date currentDate = new Date();
        currentDate.setHours(currentDate.getHours() + 2);


        exams = exams.stream().map(e -> {
            if (e.getStartTime().before(currentDate) && e.getEndTime().after(currentDate)) {
                e.setStatus("Aktiv");
            } else {
                e.setStatus("Nicht Aktiv");
            }
            examRepository.save(e);
            return e;
        }).collect(Collectors.toList());


        return ResponseEntity.ok(exams);
    }

    @Operation(summary = "Create Exam")
    @PostMapping("/exam/{catalogId}")
    public Object createExam(@RequestBody Exam exam, @PathVariable("catalogId") String catalogName) {

        QuestionCatalog catalog = questionCatalogRepository.findByName(catalogName);
        List<Question> finalQuestions = new ArrayList<>(Collections.EMPTY_LIST);


        List<List<Question>> questionsFiltered = new ArrayList<>();


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


            Calendar calendar = Calendar.getInstance();
            calendar.setTime(exam.getStartTime());
            calendar.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

            exam.setStartTime(calendar.getTime());

            examRepository.save(exam);

        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(exam.getStartTime());
            calendar.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
            exam.setStartTime(calendar.getTime());
            examRepository.save(exam);

        }
        return ResponseEntity.ok("Erstellt");
    }

    @Operation(summary = "Delete Exam")
    @DeleteMapping("/exam/{id}")
    public ResponseEntity<String> deleteExam(@PathVariable("id") Integer examId) {
        List<Exam> emptyWrittenExam = new ArrayList<>();
        List<Question> emptyQuestionList = new ArrayList<>();
        List<Question> emptyAnswerList = new ArrayList<>();


        userRepository.findAll().stream().filter(e -> e.getWrittenExams().contains(examRepository.findById(examId).get())).forEach(e -> e.setWrittenExams(emptyWrittenExam));
        userRepository.saveAll(userRepository.findAll());
        examRepository.findById(examId).get().setQuestionList(emptyQuestionList);
        examRepository.save(examRepository.findById(examId).get());
        resultsRepository.findAll().stream().filter(e -> e.getExam().equals(examRepository.findById(examId))).forEach(e -> {
            e.setExam(null);
        });
        resultsRepository.deleteAll(resultsRepository.findByExam_Id(examId));
        examRepository.findById(examId).get().setQuestionList(null);
        examRepository.deleteById(examId);

        return ResponseEntity.ok("Deleted");
    }

    @Operation(summary = "Get All Written Exams")
    @GetMapping("/written")
    public ResponseEntity<List<Exam>> getAllWrittenExams(@RequestHeader("Authorization") String bearerToken) {
        bearerToken = bearerToken.substring(7);
        User user = userRepository.findByEmail(jwtService.extractUsername(bearerToken)).get();
        return ResponseEntity.ok(user.getWrittenExams());
    }

    @Operation(summary = "Check Answers")
    @PostMapping("/exampublic/answers/{id}")
    public ResponseEntity<String> checkAnswers(@RequestBody List<String> answers, @PathVariable("id") Integer examId, @RequestHeader("Authorization") String bearerToken) {
        Exam exam = examRepository.findById(examId).get();
        List<Question> answersList = exam.getQuestionList();
        List<Question> correctAnswers = new ArrayList<>();
        List<Question> falseAnswers = new ArrayList<>();

        List<String> correctAnswersPdf = new ArrayList<>();
        List<String> fasleAnswersPdf = new ArrayList<>();


        List<List<Object>> collectedList = new ArrayList<>();
        List<CheckResult> checkResults = new ArrayList<>();

        if (answersList.size() != 0) {
            for (int i = 0; i < answersList.size(); i++) {
                if (answers.get(i).equals(answersList.get(i).getCorrectAnswer())) {
                    correctAnswers.add(answersList.get(i));
                } else {
                    falseAnswers.add(answersList.get(i));
                }
                CheckResult checkResult = new CheckResult();
                checkResult.setSelectedAnswer(answers.get(i));
                checkResult.setCorrectAnswer(answersList.get(i).getCorrectAnswer());
                checkResults.add(checkResult);
            }
        } else {
            for (int i = 0; i < exam.getCorrectAnswers().size(); i++) {
                if (answers.get(i).equals(exam.getCorrectAnswers().get(i))) {
                    correctAnswersPdf.add(answers.get(i));
                } else {
                    fasleAnswersPdf.add(answers.get(i));
                }
                CheckResult checkResult = new CheckResult();
                checkResult.setSelectedAnswer(answers.get(i));
                checkResult.setCorrectAnswer(exam.getCorrectAnswers().get(i));
                checkResults.add(checkResult);

            }
        }

        if (answersList.size() != 0) {
            collectedList.add(Collections.singletonList(correctAnswers));
            collectedList.add(Collections.singletonList(falseAnswers));
        } else {
            collectedList.add(Collections.singletonList(correctAnswersPdf));
            collectedList.add(Collections.singletonList(fasleAnswersPdf));
        }
        bearerToken = bearerToken.substring(7);

        String username = jwtService.extractUsername(bearerToken);
        User user = userRepository.findByEmail(username).stream().iterator().next();
        List<Exam> test = user.getWrittenExams();
        test.add(exam);
        assert user.getWrittenExams() != null;
        user.setWrittenExams(test);
        userRepository.save(user);


        checkResultRepository.saveAll(checkResults);

        Results results = new Results();
        results.setFirstName(user.getFirstName());
        results.setLastName(user.getLastName());
        results.setUserName(user.getUsername());
        results.setCorrectAnswers(Collections.singletonList(collectedList.get(0)));
        results.setFalseAnswers(Collections.singletonList(collectedList.get(1)));
        results.setExam(exam);
        if (exam.getExamType().equals("pdf")) {
            results.setCorrectAnswersNumber(correctAnswersPdf.size());
            results.setFalseAnswersNumber(fasleAnswersPdf.size());
        } else {
            results.setCorrectAnswersNumber(correctAnswers.size());
            results.setFalseAnswersNumber(falseAnswers.size());
        }

        results.setCheckResult(checkResults);
        resultsRepository.save(results);

        return ResponseEntity.ok("Antworten wurden korrigiert");

    }

    @Operation(summary = "Upload PDF File")
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<InputStreamResource> downloadPDFFile()
            throws IOException {

        ClassPathResource pdfFile = new ClassPathResource("pdf-sample.pdf");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(pdfFile.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(pdfFile.getInputStream()));
    }

}




