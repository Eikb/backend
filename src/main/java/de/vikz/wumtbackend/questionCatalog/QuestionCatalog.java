package de.vikz.wumtbackend.questionCatalog;


import de.vikz.wumtbackend.modul.Modul;
import de.vikz.wumtbackend.question.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_questioncatalog")
public class QuestionCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    private String name;

    @ManyToMany
    private List<Question> questions;

    @OneToOne
    private Modul modul;

}
