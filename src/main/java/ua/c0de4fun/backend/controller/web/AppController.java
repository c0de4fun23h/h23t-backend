package ua.c0de4fun.backend.controller.web;

import jakarta.servlet.http.HttpSession;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.c0de4fun.backend.models.User;
import ua.c0de4fun.backend.repository.AccountRepository;
import ua.c0de4fun.backend.repository.PaymentHistoryRepository;
import ua.c0de4fun.backend.repository.TransactHistoryRepository;

@Controller
@RequestMapping("/app")
public class AppController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    private TransactHistoryRepository transactHistoryRepository;

    User user;

    @GetMapping("/dashboard")
    public ModelAndView getDashboard(HttpSession session) {
        val view = new ModelAndView("dashboard");

        // Отримуюємо юзера із сессії
        user = (User) session.getAttribute("user");

        // Отримуємо аккаунти юзера
        val userAccounts = accountRepository.getUserAccountsById(user.getUser_id());

        // Balance
        val totalBalance = accountRepository.getTotalBalance(user.getUser_id());

        view.addObject("userAccounts", userAccounts);
        view.addObject("totalBalance", totalBalance);

        return view;
    }

    @GetMapping("/payment_history")
    public ModelAndView getPaymentHistory(HttpSession session) {
        val view = new ModelAndView("paymentHistory");

        // Отримуюємо юзера із сессії
        user = (User) session.getAttribute("user");

        // Історія покупок
        val paymentHistory = paymentHistoryRepository.getPaymentRecordsById(user.getUser_id());
        view.addObject("payment_history", paymentHistory);
        return view;
    }

    @GetMapping("/transact_history")
    public ModelAndView getTransactHistory(HttpSession session) {
        val view = new ModelAndView("transactHistory");

        // Отримуюємо юзера із сессії
        user = (User) session.getAttribute("user");

        // Історія транзакцій
        val userTransactHistory = transactHistoryRepository.getTransactionRecordsById(user.getUser_id());

        view.addObject("transact_history", userTransactHistory);

        return view;
    }
}
