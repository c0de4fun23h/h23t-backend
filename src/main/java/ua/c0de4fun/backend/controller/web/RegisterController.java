package ua.c0de4fun.backend.controller.web;

import jakarta.validation.Valid;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ua.c0de4fun.backend.models.User;
import ua.c0de4fun.backend.repository.UserRepository;
import ua.c0de4fun.backend.util.random.RandomUtil;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/register")
    public ModelAndView getRegister() {
        val view = new ModelAndView("register");
        view.addObject("PageTitle", "Register");
        return view;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid @ModelAttribute("registerUser") User user,
                                 BindingResult result,
                                 @RequestParam("first_name") String first_name,
                                 @RequestParam("last_name") String last_name,
                                 @RequestParam("email") String email,
                                 @RequestParam("password") String password,
                                 @RequestParam("confirm_password") String confirm_password) {

        val view = new ModelAndView("register");

        if (result.hasErrors() && confirm_password.isEmpty()) {
            view.addObject("confirm_pass", "The confirm Field is required");
            return view;
        }

        if (!password.equals(confirm_password)) {
            view.addObject("passwordMisMatch", "passwords do not match");
            return view;
        }

        String token = RandomUtil.generateToken();

        int bound = 123;
        int code = bound * RandomUtil.RANDOM.nextInt(bound);

        String hashpw = BCrypt.hashpw(password, BCrypt.gensalt());

        userRepository.registerUser(first_name, last_name, email, hashpw, token, code);

        String successMessage = "Account Registered Successfully, Please Check your Email and Verify Account!";
        view.addObject("success", successMessage);
        return view;
    }
}
