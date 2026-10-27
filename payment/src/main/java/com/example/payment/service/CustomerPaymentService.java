package com.example.payment.service;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.entity.CustomerPayment;
import com.example.payment.entity.PaymentStatus;
import com.example.payment.repository.CustomerPaymentRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerPaymentService {

    private final CustomerPaymentRepository paymentRepository;

    @Transactional
    public CustomerPayment addPayment(PaymentRequest request) {
        CustomerPayment payment = CustomerPayment.builder()
                .name(request.getName())
                .email(request.getEmail())
                .amountDue(request.getAmountDue())
                .dueDate(request.getDueDate())
                .status(PaymentStatus.PENDING)
                .reminderSent(false)
                .build();

        return paymentRepository.save(payment);
    }

    public Page<CustomerPayment> getAllPayments(int pageNo, int pageSize, String sortBy, String sortDir,
                                                String globalSearch, String name, String email, PaymentStatus status) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Specification<CustomerPayment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(globalSearch)) {
                String searchPattern = "%" + globalSearch.toLowerCase() + "%";
                Predicate nameMatch = cb.like(cb.lower(root.get("name")), searchPattern);
                Predicate emailMatch = cb.like(cb.lower(root.get("email")), searchPattern);
                predicates.add(cb.or(nameMatch, emailMatch));
            }

            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(email)) {
                predicates.add(cb.equal(root.get("email"), email));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return paymentRepository.findAll(spec, pageable);
    }

    public CustomerPayment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + id));
    }

    @Transactional
    public CustomerPayment updatePayment(Long id, PaymentRequest request) {
        CustomerPayment payment = getPaymentById(id);
        
        if (payment.isReminderSent()) {
            throw new IllegalStateException("Cannot update payment details because the reminder email has already been sent.");
        }

        payment.setName(request.getName());
        payment.setEmail(request.getEmail());
        payment.setAmountDue(request.getAmountDue());
        payment.setDueDate(request.getDueDate());

        return paymentRepository.save(payment);
    }
}