package de.vikz.wumtbackend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Transactional
    @Modifying
    @Query("update User u set u.enabled = ?1 where u.id = ?2")
    void updateEnabledById(Boolean enabled, Integer id);

    @Transactional
    @Modifying
    @Query("update User u set u.role = ?1 where u.id = ?2")
    void updateRole(Role role, Integer id);

    Optional<User> findByEmail(String email);

    @Override
    void deleteById(Integer integer);
}
