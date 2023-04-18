package de.vikz.wumtbackend.questionCatalog;

import de.vikz.wumtbackend.category.Category;
import de.vikz.wumtbackend.question.Question;
import de.vikz.wumtbackend.question.QuestionRepository;
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

    @GetMapping
    public ResponseEntity<List<QuestionCatalog>> getAllQuestionCatalogs() {
        return ResponseEntity.ok(questionCatalogRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<QuestionCatalog>> getQuestionCatalogById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(questionCatalogRepository.findById(id));
    }

    @PostMapping
    public ResponseEntity<String> createQuestionCatalog(@RequestBody QuestionCatalog questionCatalog) {
        questionCatalogRepository.save(questionCatalog);
        return ResponseEntity.ok("Fragenkatalog mit dem namen " + questionCatalog.getName() + " wurde erstellt.");
    }

    @PutMapping
    public ResponseEntity<String> updateQuestionCatalog(@RequestBody QuestionCatalog questionCatalog) {
        questionCatalogRepository.updateNameAndModulBy(questionCatalog.getName(), questionCatalog.getModul(), questionCatalog.getId());
        return ResponseEntity.ok("Das Modul mit der ID: " + questionCatalog.getId() + " wurde bearbeitet.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestionCatalog(@PathVariable("id") Integer id) {
        questionCatalogRepository.deleteById(id);
        return ResponseEntity.ok("Katalog mit der id: " + id + " wurde gelöscht");
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<List<Category>> getAllCategoriey(@PathVariable("id") Integer id) {
        Optional<QuestionCatalog> questionCatalog = questionCatalogRepository.findById(id);
        return ResponseEntity.ok(questionCatalog.stream().iterator().next().getCategories());
    }

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
