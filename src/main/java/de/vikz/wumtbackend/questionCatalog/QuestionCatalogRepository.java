package de.vikz.wumtbackend.questionCatalog;

import de.vikz.wumtbackend.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface QuestionCatalogRepository extends JpaRepository<QuestionCatalog, Integer> {
    @Transactional
    @Modifying
    @Query("update QuestionCatalog q set q.questions = ?1")
    int updateQuestionsBy(Question questions);

    @Transactional
    @Modifying
    @Query("update QuestionCatalog q set q.name = ?1, q.modul = ?2 WHERE q.id = ?3")
    void updateNameAndModulBy(String name, String modul, Integer id);

    QuestionCatalog findByName(String name);

}
