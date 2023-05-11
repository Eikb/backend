package de.vikz.wumtbackend.exam;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ResultsController {
    private final ResultsRepository resultsRepository;
    private final ExamRepository examRepository;

    public ResultsController(ResultsRepository resultsRepository,
                             ExamRepository examRepository) {
        this.resultsRepository = resultsRepository;
        this.examRepository = examRepository;
    }

    @GetMapping("/results")
    public ResponseEntity<List<Results>> getAllResults() {
        return ResponseEntity.ok(resultsRepository.findAll());
    }

    @DeleteMapping("/results")
    public ResponseEntity<String> deleteAllResults() {
        resultsRepository.deleteAll();
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/results/{examId}")
    public ResponseEntity<List<Results>> getAllResultsByExamId(@PathVariable Integer examId) {
        List<Results> allResults = resultsRepository.findByExam_Id(examId);
        return ResponseEntity.ok(allResults);
    }
}
