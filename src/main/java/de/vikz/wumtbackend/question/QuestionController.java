package de.vikz.wumtbackend.question;

import de.vikz.wumtbackend.category.CategoryRepository;
import de.vikz.wumtbackend.questionCatalog.QuestionCatalog;
import de.vikz.wumtbackend.questionCatalog.QuestionCatalogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/question")
public class QuestionController {
    private final QuestionCatalogRepository questionCatalogRepository;
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;

    public QuestionController(QuestionCatalogRepository questionCatalogRepository,
                              QuestionRepository questionRepository, CategoryRepository categoryRepository) {
        this.questionCatalogRepository = questionCatalogRepository;
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping("/{catalogId}")
    public ResponseEntity<String> createQuestion(@RequestBody Question question, @PathVariable("catalogId") Integer catalogId) {
        QuestionCatalog questionCatalog = questionCatalogRepository.findById(catalogId).stream().iterator().next();
        List<Question> questions = questionCatalog.getQuestions();


        assert questions != null;

        question.setCategory(categoryRepository.findByName(question.getCategoryName()));

        questionRepository.save(question);
        questions.add(question);
        questionCatalog.setQuestions(questions);
        questionCatalogRepository.save(questionCatalog);

        return ResponseEntity.ok("Frage wurde erstellt");

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable Integer id) {
        questionRepository.deleteById(id);
        return ResponseEntity.ok("Erfolgreich gelöscht");
    }

}
