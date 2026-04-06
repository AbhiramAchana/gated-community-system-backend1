package com.gatedcommunity.backend.service;

import com.gatedcommunity.backend.entity.Notification;
import com.gatedcommunity.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.gatedcommunity.backend.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender, NotificationRepository notificationRepository) {
        this.mailSender = mailSender;
        this.notificationRepository = notificationRepository;
    }

    @Async
    public void sendPaymentReceipt(User resident, BigDecimal amount, String monthYear) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(resident.getEmail());
        message.setSubject("✅ Payment Receipt: " + monthYear);
        message.setText(
                "Dear " + resident.getName() + ",\n\n" +
                        "Your payment has been successfully received.\n\n" +
                        "──────────────────────────\n" +
                        "Payment Details:\n" +
                        "  Resident    : " + resident.getName() + "\n" +
                        "  Phone       : " + (resident.getPhone() != null ? resident.getPhone() : "Not provided") + "\n" +
                        "  Amount Paid : ₹" + amount + "\n" +
                        "  Month       : " + monthYear + "\n" +
                        "──────────────────────────\n\n" +
                        "Thank you for your timely payment!\n\n" +
                        "Regards,\n" +
                        "Gated Community Management"
        );

        Notification log = Notification.builder()
                .recipient(resident)
                .type("EMAIL")
                .subject(message.getSubject())
                .messageBody(message.getText())
                .build();

        try {
            mailSender.send(message);
            log.setStatus("SENT");
        } catch (Exception e) {
            log.setStatus("FAILED");
            log.setErrorMessage(e.getMessage());
        }

        notificationRepository.save(log);
    }

    @Async
    public void sendNewInvoiceNotification(User resident, BigDecimal amount, LocalDate dueDate, String monthYear) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(resident.getEmail());
        message.setSubject("🧾 New Invoice: " + monthYear);
        message.setText(
                "Dear " + resident.getName() + ",\n\n" +
                        "A new maintenance invoice has been generated for you.\n\n" +
                        "──────────────────────────\n" +
                        "Invoice Details:\n" +
                        "  Resident    : " + resident.getName() + "\n" +
                        "  Phone       : " + (resident.getPhone() != null ? resident.getPhone() : "Not provided") + "\n" +
                        "  Amount Due  : ₹" + amount + "\n" +
                        "  Month       : " + monthYear + "\n" +
                        "  Due Date    : " + dueDate + "\n" +
                        "──────────────────────────\n\n" +
                        "Please login to your portal to pay before the due date.\n\n" +
                        "Regards,\n" +
                        "Gated Community Management"
        );

        Notification log = Notification.builder()
                .recipient(resident)
                .type("EMAIL")
                .subject(message.getSubject())
                .messageBody(message.getText())
                .build();

        try {
            mailSender.send(message);
            log.setStatus("SENT");
        } catch (Exception e) {
            log.setStatus("FAILED");
            log.setErrorMessage(e.getMessage());
        }

        notificationRepository.save(log);
    }

    @Async
    public void sendSmsReminder(User resident, String monthYear, BigDecimal amount) {
        String body = "Reminder: ₹" + amount + " pending for " + monthYear;

        Notification log = Notification.builder()
                .recipient(resident)
                .type("SMS")
                .subject("Payment Reminder")
                .messageBody(body)
                .status("SENT")
                .build();

        notificationRepository.save(log);
    }

    @Async
    public void sendComplaintUpdate(User resident, String subject, String status, String adminResponse) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(resident.getEmail());
        message.setSubject("🔧 Complaint Update: " + subject);
        message.setText(
                "Dear " + resident.getName() + ",\n\n" +
                        "Your complaint has been updated.\n\n" +
                        "──────────────────────────\n" +
                        "Complaint : " + subject + "\n" +
                        "Status    : " + status + "\n" +
                        "Response  : " + adminResponse + "\n" +
                        "──────────────────────────\n\n" +
                        "Login to your portal for more details.\n\n" +
                        "Regards,\n" +
                        "Gated Community Management"
        );

        Notification log = Notification.builder()
                .recipient(resident)
                .type("EMAIL")
                .subject(message.getSubject())
                .messageBody(message.getText())
                .build();

        try {
            mailSender.send(message);
            log.setStatus("SENT");
        } catch (Exception e) {
            log.setStatus("FAILED");
            log.setErrorMessage(e.getMessage());
        }

        notificationRepository.save(log);
    }

    @Async
    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
            System.out.println("✅ Email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email to " + toEmail + ": " + e.getMessage());
        }
    }

}