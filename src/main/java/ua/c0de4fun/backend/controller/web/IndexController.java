package ua.c0de4fun.backend.controller.web;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.c0de4fun.backend.repository.UserRepository;

@Controller
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String getIndex() {
        return "index";
    }

    @GetMapping("/error")
    public ModelAndView getError() {
        val view = new ModelAndView("error");
        view.addObject("PageTitle", "Errors");
        return view;
    }
}
