package de.vikz.wumtbackend.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    private ResponseEntity<List<UserEntity>> getAllUser() {
        List<User> users = userRepository.findAll();
        List<UserEntity> userEntities = new ArrayList<>();

        users.stream().map(e -> {
            UserEntity userEntity = new UserEntity();
            userEntity.setFirstName(e.getFirstName());
            userEntity.setLastName(e.getLastName());
            userEntities.add(userEntity);
            return 1;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(userEntities);

    }

}
