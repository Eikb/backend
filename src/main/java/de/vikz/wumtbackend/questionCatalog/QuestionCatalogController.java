package de.vikz.wumtbackend.questionCatalog;

import de.vikz.wumtbackend.category.Category;
import de.vikz.wumtbackend.question.Question;
import de.vikz.wumtbackend.question.QuestionRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/questioncatalog")
public class QuestionCatalogController {
    private final QuestionCatalogRepository questionCatalogRepository;
    private final QuestionRepository questionRepository;

    public QuestionCatalogController(QuestionCatalogRepository questionCatalogRepository,
                                     QuestionRepository questionRepository) {
        this.questionCatalogRepository = questionCatalogRepository;
        this.questionRepository = questionRepository;
    }

    @Operation(summary = "Get All Question Catalogs")
    @GetMapping
    public ResponseEntity<List<QuestionCatalog>> getAllQuestionCatalogs() {
        return ResponseEntity.ok(questionCatalogRepository.findAll());
    }

    @Operation(summary = "Get Question Catalog By ID")
    @GetMapping("/{id}")
    public ResponseEntity<Optional<QuestionCatalog>> getQuestionCatalogById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(questionCatalogRepository.findById(id));
    }

    @Operation(summary = "Create Question Catalog")
    @PostMapping
    public ResponseEntity<String> createQuestionCatalog(@RequestBody QuestionCatalog questionCatalog) {
        questionCatalogRepository.save(questionCatalog);
        return ResponseEntity.ok("Fragenkatalog mit dem namen " + questionCatalog.getName() + " wurde erstellt.");
    }

    @Operation(summary = "Update Question Catalog")
    @PutMapping
    public ResponseEntity<String> updateQuestionCatalog(@RequestBody QuestionCatalog questionCatalog) {
        questionCatalogRepository.updateNameAndModulBy(questionCatalog.getName(), questionCatalog.getModul(), questionCatalog.getId());
        return ResponseEntity.ok("Das Modul mit der ID: " + questionCatalog.getId() + " wurde bearbeitet.");
    }

    @Operation(summary = "Delete Question Catalog")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestionCatalog(@PathVariable("id") Integer id) {
        questionCatalogRepository.deleteById(id);
        return ResponseEntity.ok("Katalog mit der id: " + id + " wurde gelöscht");
    }

    @Operation(summary = "Get All Categories")
    @GetMapping("/category/{id}")
    public ResponseEntity<List<Category>> getAllCategoriey(@PathVariable("id") Integer id) {
        Optional<QuestionCatalog> questionCatalog = questionCatalogRepository.findById(id);
        return ResponseEntity.ok(questionCatalog.stream().iterator().next().getCategories());
    }

    @Operation(summary = "Get Category By Name")
    @GetMapping("/categoryname/{name}")
    public ResponseEntity<List<Category>> getAllCategorieName(@PathVariable("name") String name) {
        QuestionCatalog questionCatalog = questionCatalogRepository.findByName(name);
        return ResponseEntity.ok(questionCatalog.getCategories());
    }

    @Operation(summary = "Delete Questio ")
    @DeleteMapping("/question/{questionId}/{catalogId}")
    public ResponseEntity<String> deleteQuestion(@PathVariable("questionId") Integer questionId, @PathVariable("catalogId") Integer catalogId) {

        QuestionCatalog questionCatalog = questionCatalogRepository.findById(catalogId).stream().iterator().next();
        Question question = questionRepository.findById(questionId).stream().iterator().next();

        List<Question> questions = questionCatalog.getQuestions();
        assert questions != null;
        List<Question> collect = questions.stream().filter(item -> item.getQuestion().equals(question.getQuestion())).toList();

        questions.remove(collect.get(0));

        questionCatalog.setQuestions(questions);
        questionCatalogRepository.save(questionCatalog);

        questionRepository.deleteById(questionId);
        return ResponseEntity.ok("Frage wurde erfolgreich gelöscht");
    }
}
