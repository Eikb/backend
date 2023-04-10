package de.vikz.wumtbackend.modul;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ModulRepository extends JpaRepository<Modul, Integer> {

    @Transactional
    @Modifying
    @Query("update Modul m set m.name = ?1 WHERE m.id = ?2")
    void updateNameBy(String name, Integer id);


}
