package com.example.payment.repository;

import com.example.payment.entity.CustomerPayment;
import com.example.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomerPaymentRepository
        extends JpaRepository<CustomerPayment, Long> , JpaSpecificationExecutor<CustomerPayment>{

    List<CustomerPayment> findByDueDateAndStatusAndReminderSentFalse(
            LocalDate dueDate,
            PaymentStatus status
    );
}