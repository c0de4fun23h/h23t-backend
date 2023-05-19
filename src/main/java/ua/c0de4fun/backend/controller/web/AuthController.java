package ua.c0de4fun.backend.controller.web;

import jakarta.servlet.http.HttpSession;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.c0de4fun.backend.repository.UserRepository;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public ModelAndView getLogin() {
        val view = new ModelAndView("login");
        view.addObject("PageTitle", "Login");
        return view;
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        @RequestParam("_token") String token,
                        Model model,
                        HttpSession session) {

        if (email.isEmpty() || email == null || password.isEmpty() || password == null) {
            model.addAttribute("error", "Username or Password Cannot be Empty");
            return "login";
        }

        val emailInDatabase = userRepository.getUserEmail(email);

        if (emailInDatabase != null) {
            val passwordInDatabase = userRepository.getUserPassword(emailInDatabase);

            if (!BCrypt.checkpw(password, passwordInDatabase)) {
                model.addAttribute("error", "Incorrect Username or Password");
                return "login";
            }

        } else {
            model.addAttribute("error", "Something went wrong please contact support");
            return "error";
        }

        val user = userRepository.getUserDetails(emailInDatabase);

        session.setAttribute("user", user);
        session.setAttribute("token", token);
        session.setAttribute("authenticated", true);

        return "redirect:/app/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("logged_out", "Logged out successfully");
        return "redirect:/login";
    }
}
