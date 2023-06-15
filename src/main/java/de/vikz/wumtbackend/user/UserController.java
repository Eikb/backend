package de.vikz.wumtbackend.user;

import de.vikz.wumtbackend.config.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserRepository userRepository;
    @Autowired
    private final JwtService jwtService;

    public UserController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Get All Users")
    @GetMapping("/")
    public ResponseEntity<List<UserEntity>> getAllUser() {
        List<User> users = userRepository.findAll();
        List<UserEntity> userEntities = new ArrayList<>();

        users.stream().map(e -> {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(e.getId());
            userEntity.setFirstName(e.getFirstName());
            userEntity.setLastName(e.getLastName());
            userEntity.setRole(e.getRole().toString());
            userEntity.setSemester(e.getSemester());
            userEntity.setUniversity(e.getUniversity());
            userEntity.setEnabled(e.getEnabled());
            userEntity.setWrittenExams(e.getWrittenExams());
            assert e.getWrittenExams() != null;
            userEntity.setExamsTaken(e.getWrittenExams().size());
            userEntities.add(userEntity);

            return 1;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(userEntities);
    }

    @Operation(summary = "Delete User By Id")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
        try {
            userRepository.deleteById(id);
            ResponseEntity.ok("Gelöscht");
        } catch (Exception e) {
            ResponseEntity.notFound();
        }

        return null;
    }

    @Operation(summary = "Update Role from User")
    @PutMapping("/{id}/{role}")
    public ResponseEntity<String> updateRole(@PathVariable String role, @PathVariable Integer id) {
        User user = userRepository.findById(id).get();
        Role oldRole = user.getRole();
        if (oldRole.toString().toUpperCase().equals(role.toUpperCase())) {
            return ResponseEntity.ok("Der User hat schon die Rolle" + oldRole.toString());
        } else if (Objects.equals(role.toUpperCase(), "USER")) {
            userRepository.updateRole(Role.USER, id);
        } else if (Objects.equals(role.toUpperCase(), "ADMIN")) {
            userRepository.updateRole(Role.ADMIN, id);

        } else {
            return ResponseEntity.ok("Es ist ein Fehler aufgetreten");
        }

        return ResponseEntity.ok("Role wurde erfolgreich geupdatet");

    }

    @Operation(summary = "Enable or Disable User")
    @PutMapping("/update/{id}/{sperre}")
    public ResponseEntity<String> updateEnable(@PathVariable Integer id, @PathVariable String sperre) {
        if (sperre.equals("false")) {
            userRepository.updateEnabledById(false, id);
            return ResponseEntity.ok("Wurde gesperrt");
        } else if (sperre.equals("true")) {
            userRepository.updateEnabledById(true, id);
            return ResponseEntity.ok("Wurde entsperrt");

        } else {
            return ResponseEntity.ok("Problem");
        }
    }

    @Operation(summary = "GET User By ID")
    @GetMapping("/{token}")
    public ResponseEntity<UserEntity> getUserByID(@PathVariable String token) {
        User user = userRepository.findByEmail(jwtService.extractUsername(token)).get();

        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setRole(user.getRole().toString());
        userEntity.setSemester(user.getSemester());
        userEntity.setUniversity(user.getUniversity());
        userEntity.setEnabled(user.getEnabled());
        userEntity.setWrittenExams(user.getWrittenExams());
        assert user.getWrittenExams() != null;
        userEntity.setExamsTaken(user.getWrittenExams().size());

        return ResponseEntity.ok(userEntity);
    }


}
