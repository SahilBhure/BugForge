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
    private UsersRepository usersRepository;


    @Scheduled(cron = "0 0 20 * * *", zone = "Asia/Kolkata")
    public void sendDailyReminders() {
        log.info("Starting daily reminder email job...");


        List<Users> allUsers = usersRepository.findAll();

        for (Users user : allUsers) {
            try {
                String to = user.getMail();
                String subject = "Your Daily Task Reminder from BugForge";
                String text = String.format("Hello %s,\n\nThis is your daily reminder to check your assigned tasks on BugForge!", user.getUsername());

                emailService.sendSimpleMessage(to, subject, text);
                log.info("Sent reminder to {}", to);

            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", user.getMail(), e.getMessage());
            }
        }

        log.info("Daily reminder email job finished.");
    }
}
