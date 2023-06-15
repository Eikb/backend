package de.vikz.wumtbackend.category;

import de.vikz.wumtbackend.questionCatalog.QuestionCatalog;
import de.vikz.wumtbackend.questionCatalog.QuestionCatalogRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {
    private final CategoryRepository categoryRepository;
    private final QuestionCatalogRepository questionCatalogRepository;

    public CategoryController(CategoryRepository categoryRepository,
                              QuestionCatalogRepository questionCatalogRepository) {
        this.categoryRepository = categoryRepository;
        this.questionCatalogRepository = questionCatalogRepository;
    }

    @Operation(summary = "Create Category")
    @PostMapping
    public ResponseEntity<String> createCategory(@RequestBody Category category) {
        Optional<QuestionCatalog> questionCatalog = questionCatalogRepository.findById(category.getCatalogId());
        List<Category> categories = questionCatalog.get().getCategories();
        assert categories != null;
        categories.add(category);
        questionCatalog.stream().iterator().next().setCategories(categories);
        categoryRepository.save(category);
        questionCatalogRepository.save(questionCatalog.get());

        return ResponseEntity.ok("Kategorie wurde erstellt");
    }

    @Operation(summary = "Get Categories by Id")
    @GetMapping("/{id}")
    public ResponseEntity<List<Category>> getCategoriesById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoryRepository.findAllById(Collections.singleton(id)));
    }


}
