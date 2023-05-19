package ua.c0de4fun.backend.controller.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.c0de4fun.backend.models.User;
import ua.c0de4fun.backend.repository.AccountRepository;
import ua.c0de4fun.backend.repository.PaymentRepository;
import ua.c0de4fun.backend.repository.TransactRepository;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/transact")
public class TransactController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TransactRepository transactRepository;

    User user;
    double currentBalance;
    double newBalance;
    LocalDateTime currentDateTime = LocalDateTime.now();

    @PostMapping("/deposit")
    public String deposit(@RequestParam("deposit_amount") String depositAmount,
                          @RequestParam("account_id") String accountID,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {

        if (depositAmount.isEmpty() || accountID.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Deposit Amount or Account Depositing to Cannot Be Empty!");
            return "redirect:/app/dashboard";
        }

        user = (User) session.getAttribute("user");

        int acc_id = Integer.parseInt(accountID);

        double depositAmountValue = Double.parseDouble(depositAmount);

        if (depositAmountValue == 0) {
            redirectAttributes.addFlashAttribute("error", "Deposit Amount Cannot Be of 0 (Zero) Value");
            return "redirect:/app/dashboard";
        }

        currentBalance = accountRepository.getAccountBalance(user.getUser_id(), acc_id);

        newBalance = currentBalance + depositAmountValue;

        accountRepository.changeAccountBalanceById(newBalance, acc_id);

        transactRepository.logTransaction(acc_id, "deposit", depositAmountValue, "online", "success", "Deposit Transaction Successful", currentDateTime);

        redirectAttributes.addFlashAttribute("success", "Amount Deposited Successfully");
        return "redirect:/app/dashboard";
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam("transfer_from") String transfer_from,
                           @RequestParam("transfer_to") String transfer_to,
                           @RequestParam("transfer_amount") String transfer_amount,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        String errorMessage;

        if (transfer_from.isEmpty() || transfer_to.isEmpty() || transfer_amount.isEmpty()) {
            errorMessage = "The account transferring from and to along with the amount cannot be empty!";
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/app/dashboard";
        }

        int transferFromId = Integer.parseInt(transfer_from);
        int transferToId = Integer.parseInt(transfer_to);
        double transferAmount = Double.parseDouble(transfer_amount);

        if (transferFromId == transferToId) {
            errorMessage = "Cannot Transfer Into The same Account, Please select the appropriate account to perform transfer";
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/app/dashboard";
        }

        if (transferAmount == 0) {
            errorMessage = "Cannot Transfer an amount of 0 (Zero) value, please enter a value greater than 0 (Zero) ";
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/app/dashboard";
        }

        user = (User) session.getAttribute("user");

        double currentBalanceOfAccountTransferringFrom = accountRepository.getAccountBalance(user.getUser_id(), transferFromId);

        if (currentBalanceOfAccountTransferringFrom < transferAmount) {
            errorMessage = "You Have insufficient Funds to perform this Transfer!";
            transactRepository.logTransaction(transferFromId, "Transfer", transferAmount, "online", "failed", "Insufficient Funds", currentDateTime);
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/app/dashboard";
        }

        double currentBalanceOfAccountTransferringTo = accountRepository.getAccountBalance(user.getUser_id(), transferToId);

        double newBalanceOfAccountTransferringFrom = currentBalanceOfAccountTransferringFrom - transferAmount;

        double newBalanceOfAccountTransferringTo = currentBalanceOfAccountTransferringTo + transferAmount;

        accountRepository.changeAccountBalanceById(newBalanceOfAccountTransferringFrom, transferFromId);

        accountRepository.changeAccountBalanceById(newBalanceOfAccountTransferringTo, transferToId);

        transactRepository.logTransaction(transferFromId, "Transfer", transferAmount, "online", "success", "Transfer Transaction Successful", currentDateTime);

        String successMessage = "Amount Transferred Successfully!";
        redirectAttributes.addFlashAttribute("success", successMessage);
        return "redirect:/app/dashboard";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam("withdrawal_amount") String withdrawalAmount,
                           @RequestParam("account_id") String accountID,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        String errorMessage;
        String successMessage;

        if (withdrawalAmount.isEmpty() || accountID.isEmpty()) {
            errorMessage = "Withdrawal Amount and Account Withdrawing From Cannot be Empty ";
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/app/dashboard";
        }

        double withdrawal_amount = Double.parseDouble(withdrawalAmount);
        int account_id = Integer.parseInt(accountID);

        if (withdrawal_amount == 0) {
            errorMessage = "Withdrawal Amount Cannot be of 0 (Zero) value, please enter a value greater than 0 (Zero)";
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/app/dashboard";
        }

        user = (User) session.getAttribute("user");

        currentBalance = accountRepository.getAccountBalance(user.getUser_id(), account_id);

        if (currentBalance < withdrawal_amount) {
            errorMessage = "You Have insufficient Funds to perform this Withdrawal!";
            transactRepository.logTransaction(account_id, "Withdrawal", withdrawal_amount, "online", "failed", "Insufficient Funds", currentDateTime);
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/app/dashboard";
        }

        newBalance = currentBalance - withdrawal_amount;

        accountRepository.changeAccountBalanceById(newBalance, account_id);

        transactRepository.logTransaction(account_id, "Withdrawal", withdrawal_amount, "online", "success", "Withdrawal Transaction Successful", currentDateTime);

        successMessage = "Withdrawal Successful!";
        redirectAttributes.addFlashAttribute("success", successMessage);
        return "redirect:/app/dashboard";
    }

    @PostMapping("/payment")
    public String payment(@RequestParam("beneficiary") String beneficiary,
                          @RequestParam("account_number") String account_number,
                          @RequestParam("account_id") String account_id,
                          @RequestParam("reference") String reference,
                          @RequestParam("payment_amount") String payment_amount,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {

        String errorMessage;
        String successMessage;

        if (beneficiary.isEmpty() || account_number.isEmpty() || account_id.isEmpty() || payment_amount.isEmpty()) {
            errorMessage = "Beneficiary, Account Number, Account Paying From and Payment Amount Cannot be Empty! ";
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/app/dashboard";
        }

        int accountID = Integer.parseInt(account_id);
        double paymentAmount = Double.parseDouble(payment_amount);

        if (paymentAmount == 0) {
            errorMessage = "Payment Amount Cannot be of 0 (Zero) value, please enter a value greater than 0 (Zero) ";
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/app/dashboard";
        }

        user = (User) session.getAttribute("user");

        currentBalance = accountRepository.getAccountBalance(user.getUser_id(), accountID);

        if (currentBalance < paymentAmount) {
            errorMessage = "You Have insufficient Funds to perform this payment";
            String reasonCode = "Could not Processed Payment due to insufficient funds!";
            paymentRepository.makePayment(accountID, beneficiary, account_number, paymentAmount, reference, "failed", reasonCode, currentDateTime);
            transactRepository.logTransaction(accountID, "Payment", paymentAmount, "online", "failed", "Insufficient Funds", currentDateTime);
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/app/dashboard";
        }

        newBalance = currentBalance - paymentAmount;

        String reasonCode = "Payment Processed Successfully!";
        paymentRepository.makePayment(accountID, beneficiary, account_number, paymentAmount, reference, "success", reasonCode, currentDateTime);

        accountRepository.changeAccountBalanceById(newBalance, accountID);

        transactRepository.logTransaction(accountID, "Payment", paymentAmount, "online", "success", "Payment Transaction Successful", currentDateTime);

        successMessage = reasonCode;
        redirectAttributes.addFlashAttribute("success", successMessage);
        return "redirect:/app/dashboard";
    }
}
