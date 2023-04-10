package de.vikz.wumtbackend.questionCatalog;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questioncatalog")
public class QuestionCatalogController {
    private final QuestionCatalogRepository questionCatalogRepository;

    public QuestionCatalogController(QuestionCatalogRepository questionCatalogRepository) {
        this.questionCatalogRepository = questionCatalogRepository;
    }

    @GetMapping
    public ResponseEntity<List<QuestionCatalog>> getAllQuestionCatalogs() {
        return ResponseEntity.ok(questionCatalogRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<String> createQuestionCatalog(@RequestBody QuestionCatalog questionCatalog) {
        questionCatalogRepository.save(questionCatalog);
        return ResponseEntity.ok("Fragenkatalog mit dem namen " + questionCatalog.getName() + " wurde erstellt.");
    }
}
