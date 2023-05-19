package ua.c0de4fun.backend.controller.advisor;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import ua.c0de4fun.backend.models.User;

@ControllerAdvice
public class AdvisorController {

    @ModelAttribute("registerUser")
    public User getUserDefaults() {
        return new User();
    }
}
