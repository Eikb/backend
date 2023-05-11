package de.vikz.wumtbackend.exam;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultsRepository extends JpaRepository<Results, Integer> {
    List<Results> findByExam_Id(Integer id);

}
