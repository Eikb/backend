package de.vikz.wumtbackend.exam;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Get All Results")
    @GetMapping("/results")
    public ResponseEntity<List<Results>> getAllResults() {
        return ResponseEntity.ok(resultsRepository.findAll());
    }

    @Operation(summary = "Delete Results")
    @DeleteMapping("/results")
    public ResponseEntity<String> deleteAllResults() {
        resultsRepository.deleteAll();
        return ResponseEntity.ok("Deleted");
    }

    @Operation(summary = "Get All Results by ExamId")
    @GetMapping("/results/{examId}")
    public ResponseEntity<List<Results>> getAllResultsByExamId(@PathVariable Integer examId) {
        List<Results> allResults = resultsRepository.findByExam_Id(examId);
        return ResponseEntity.ok(allResults);
    }
}
