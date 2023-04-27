package de.vikz.wumtbackend.auth;


import de.vikz.wumtbackend.config.JwtService;
import de.vikz.wumtbackend.user.Role;
import de.vikz.wumtbackend.user.User;
import de.vikz.wumtbackend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    @Autowired
    private JwtService jwtService;
    private final UserRepository userRepository;

    @GetMapping("/{token}")
    public ResponseEntity<Boolean> checkIfAdmin(@PathVariable("token") String token) {
        String username = jwtService.extractUsername(token);
        Optional<User> user = userRepository.findByEmail(username);
        if (user.get().getRole().equals(Role.ADMIN)) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }

    }
}
