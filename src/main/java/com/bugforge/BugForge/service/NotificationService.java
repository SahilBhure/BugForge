package com.bugforge.BugForge.service;

import com.bugforge.BugForge.data.Users;
import com.bugforge.BugForge.data.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

public class NotificationService {

	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsersRepository usersRepository; // Inject the repository to get user data

    /**
     * This task runs daily at 8:00 AM IST.
     * It finds all users and sends them a reminder email.
     */
    @Scheduled(cron = "0 27 15 * * *", zone = "Asia/Kolkata")
    public void sendDailyReminders() {
        log.info("Starting daily reminder email job...");

        // 1. Fetch all users from the database
        List<Users> allUsers = usersRepository.findAll();

        // 2. Loop through each user
        for (Users user : allUsers) {
            try {
                String to = user.getMail();
                String subject = "Your Daily Task Reminder from BugForge";
                // You can customize the text with user-specific info
                String text = String.format("Hello %s,\n\nThis is your daily reminder to check your assigned tasks on BugForge!", user.getUsername());

                // 3. Send the email
                emailService.sendSimpleMessage(to, subject, text);
                log.info("Sent reminder to {}", to);

            } catch (Exception e) {
                // Log errors for individual email failures so the loop can continue
                log.error("Failed to send email to {}: {}", user.getMail(), e.getMessage());
            }
        }

        log.info("Daily reminder email job finished.");
    }
}
