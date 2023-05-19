package ua.c0de4fun.backend.controller.rest;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.c0de4fun.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class RestAuthApiController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public ResponseEntity validateUserEmail(@PathVariable("email") String email) {

        // Отримуюємо пошту юзера
        val userEmail = userRepository.getUserEmail(email);

        // Перевіряємо якщо пошта існує
        if (userEmail != null) {
            val userPassword = userRepository.getUserPassword(email);
            return new ResponseEntity<>(userPassword, HttpStatus.OK);
        }

        // Якщо пошти не існує то вертаємо Response
        return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
    }
}