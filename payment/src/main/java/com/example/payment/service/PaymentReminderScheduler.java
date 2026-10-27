package com.example.payment.service;

import com.example.payment.entity.CustomerPayment;
import com.example.payment.entity.PaymentStatus;
import com.example.payment.repository.CustomerPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentReminderScheduler {

    private final CustomerPaymentRepository paymentRepository;
    private final JavaMailSender mailSender;


    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void processPaymentReminders() {

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        log.info(
                "Starting payment reminder job for due date: {}",
                tomorrow
        );

        List<CustomerPayment> payments =
                paymentRepository
                        .findByDueDateAndStatusAndReminderSentFalse(
                                tomorrow,
                                PaymentStatus.PENDING
                        );

        log.info(
                "Found {} payment(s) requiring reminders",
                payments.size()
        );

        for (CustomerPayment payment : payments) {

            try {

                sendReminderEmail(payment);

                payment.setReminderSent(true);

                paymentRepository.save(payment);

                log.info(
                        "Reminder sent successfully for payment ID: {}",
                        payment.getId()
                );

            } catch (Exception e) {

                log.error(
                        "Failed to send reminder for payment ID: {}",
                        payment.getId(),
                        e
                );
            }
        }
    }

    private void sendReminderEmail(CustomerPayment payment) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(payment.getEmail());

        message.setSubject("Payment Reminder - Due Tomorrow");

        message.setText(
                String.format(
                        """
                        Dear %s,

                        This is a reminder that your payment is due tomorrow.

                        Amount Due: ₹%s
                        Due Date: %s

                        Please make the payment by the due date.

                        Thank you,
                        Payment Management System
                        """,
                        payment.getName(),
                        payment.getAmountDue(),
                        payment.getDueDate()
                )
        );

        mailSender.send(message);
    }
}