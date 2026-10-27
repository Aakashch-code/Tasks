package com.example.payment.controller;

import com.example.payment.dto.PaymentRequest;
import com.example.payment.entity.CustomerPayment;
import com.example.payment.entity.PaymentStatus;
import com.example.payment.service.CustomerPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customer-payments")
@RequiredArgsConstructor
@Tag(name = "Customer Payments", description = "APIs for managing customer payments")
public class CustomerPaymentController {

    private final CustomerPaymentService paymentService;

    @PostMapping
    @Operation(summary = "Add customer payment", description = "Creates a new customer payment record")
    public ResponseEntity<?> addPayment(@Valid @RequestBody PaymentRequest request) {
        try {
            CustomerPayment payment = paymentService.addPayment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while adding customer payment: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Get all customer payments", description = "Retrieves a paginated list of all customer payments with optional search, filters, and sorting")
    public ResponseEntity<Page<CustomerPayment>> getAllPayments(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(defaultValue = "id", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) PaymentStatus status
    ) {
        Page<CustomerPayment> paymentPage = paymentService.getAllPayments(
                pageNo, pageSize, sortBy, sortDir, search, name, email, status);
        return ResponseEntity.ok(paymentPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves a specific customer payment by its ID")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        try {
            CustomerPayment payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(payment);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer payment", description = "Updates an existing customer payment")
    public ResponseEntity<?> updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
        try {
            CustomerPayment payment = paymentService.updatePayment(id, request);
            return ResponseEntity.ok(payment);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}