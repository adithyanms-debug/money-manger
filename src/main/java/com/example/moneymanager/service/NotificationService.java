package com.example.moneymanager.service;

import com.example.moneymanager.dto.ExpenseDto;
import com.example.moneymanager.entity.ProfileEntity;
import com.example.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *", zone = "IST")
    public void sendDailyIncomeExpenseReminder() {
        log.info("Job started: sendDailyIncomeExpenseReminder");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for(ProfileEntity profile : profiles) {
            String body = "Hi " + profile.getFullName() + ",<br><br>"
                    + "This is a friendly reminder to add your income and expenses for today in Money Manager. <br><br>"
                    + "<a href="+frontendUrl+" style='display:inline-block;padding:10px 20px;background-color:#4CAF50;color:#fff;text-decoration:none;border-radius:5px;font-weight:bold'>Go to Money Manager</a>"
                    + "<br><br>Best regards, <br>Money Manager Team";
            emailService.sendEmail(profile.getEmail(), "Daily reminder : Add your income and expenses", body);
        }
        log.info("Job completed: sendDailyExpenseIncomeReminder()");
    }

    //@Scheduled(cron = "0 * * * * *", zone = "IST") for testing
    @Scheduled(cron = "0 0 23 * * *", zone = "IST")
    public void sendEmailSummary() {
        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
            List<ExpenseDto> todayExpenses = expenseService.getExpenseForUserOnDate(profile.getId(), LocalDate.now());
            if (!todayExpenses.isEmpty()) {
                StringBuilder table = new StringBuilder();
                table.append("<table style='border-collapse:collapse; width:100%'>");
                table.append("<thead>");
                table.append("<tr style='background-color:#4CAF50; color:white'>");
                table.append("<th style='padding:12px; text-align:left; border:1px solid #ddd'>Name</th>");
                table.append("<th style='padding:12px; text-align:left; border:1px solid #ddd'>Category</th>");
                table.append("<th style='padding:12px; text-align:right; border:1px solid #ddd'>Amount</th>");
                table.append("</tr>");
                table.append("</thead>");
                table.append("<tbody>");

                BigDecimal total = BigDecimal.ZERO;
                for (ExpenseDto expense : todayExpenses) {
                    table.append("<tr>");
                    table.append("<td style='padding:10px; border:1px solid #ddd'>").append(expense.getName()).append("</td>");
                    table.append("<td style='padding:10px; border:1px solid #ddd'>").append(expense.getCategoryName()).append("</td>");
                    table.append("<td style='padding:10px; text-align:right; border:1px solid #ddd'>₹").append(expense.getAmount()).append("</td>");
                    table.append("</tr>");
                    total = total.add(expense.getAmount());
                }

                table.append("<tr style='background-color:#f2f2f2; font-weight:bold'>");
                table.append("<td colspan='2' style='padding:10px; border:1px solid #ddd; text-align:right'>Total:</td>");
                table.append("<td style='padding:10px; text-align:right; border:1px solid #ddd'>₹").append(total).append("</td>");
                table.append("</tr>");
                table.append("</tbody>");
                table.append("</table>");

                String body = "Hi " + profile.getFullName() + ",<br><br>"
                        + "Here is the summary of your expenses for today (" + LocalDate.now() + "):<br><br>"
                        + table.toString()
                        + "<br><br>Best regards,<br>Money Manager Team";
                emailService.sendEmail(profile.getEmail(), "Your daily expenses", body);
            }
        }
        log.info("Job completed: sendEmailSummary()");
    }
}
