package ua.c0de4fun.backend.controller.web;

import jakarta.servlet.http.HttpSession;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.c0de4fun.backend.models.User;
import ua.c0de4fun.backend.repository.AccountRepository;
import ua.c0de4fun.backend.util.random.RandomUtil;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/create_account")
    public String createAccount(@RequestParam("account_name") String accountName,
                                @RequestParam("account_type") String accountType,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {

        // Перевірка на існування String в полях при створені аккаунту
        if (accountName.isEmpty() || accountType.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Account Name and Type Cannot be Empty!");
            return "redirect:/app/dashboard";
        }

        // Отримуємо юзера із сессії
        val user = (User) session.getAttribute("user");

        // Генеруємо номер аккаунт для юзера
        val accountNumber = Integer.toString(RandomUtil.generateAccountNumber());

        // Ствоюємо аккаунт для юзера
        accountRepository.createBankAccount(user.getUser_id(), accountNumber, accountName, accountType);

        // Повідомлення про успішне створення
        redirectAttributes.addFlashAttribute("success", "Account Created Successfully!");
        return "redirect:/app/dashboard";

    }
}
