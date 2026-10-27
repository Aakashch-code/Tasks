package com.example.empora.controller;

import com.example.empora.dto.PageResponse;
import com.example.empora.service.EmployeeService;
import com.example.empora.dto.EmployeeRequest;
import com.example.empora.dto.EmployeeResponse;
import com.example.empora.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @RequestBody EmployeeRequest request) {
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<EmployeeResponse>> getAllEmployees(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "department", required = false) String department) {

        return ResponseEntity.ok(
                employeeService.getAllEmployees(pageNo, pageSize, sortBy, sortDir, search, gender, department)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        
        EmployeeResponse employee = employeeService.getEmployeeById(id);

        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }

        return ResponseEntity.ok(employee);
    }
}