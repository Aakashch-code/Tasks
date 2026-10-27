package com.example.empora.service;

import com.example.empora.dto.PageResponse;
import com.example.empora.entity.Employee;
import com.example.empora.repository.EmployeeRepository;
import com.example.empora.dto.EmployeeRequest;
import com.example.empora.dto.EmployeeResponse;
import com.example.empora.repository.EmployeeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Employee employee = new Employee();

        employee.setName(request.getName());
        employee.setGender(request.getGender());
        employee.setAge(request.getAge());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setExtraDetails(request.getExtraDetail());

        Employee savedEmployee = employeeRepository.save(employee);
        return mapToResponse(savedEmployee);
    }

    public PageResponse<EmployeeResponse> getAllEmployees(
            int pageNo, int pageSize, String sortBy, String sortDir,
            String search, String gender, String department) {

        // 1. Sorting setup
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // 2. Pagination setup
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // 3. Specification (Filtering & Global Search) setup
        Specification<Employee> spec = EmployeeSpecification.filterEmployees(search, gender, department);

        // 4. Fetch Data
        Page<Employee> employeePage = employeeRepository.findAll(spec, pageable);

        // 5. Map to DTOs
        List<EmployeeResponse> content = employeePage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        // 6. Build PageResponse
        return new PageResponse<>(
                content,
                employeePage.getNumber(),
                employeePage.getSize(),
                employeePage.getTotalElements(),
                employeePage.getTotalPages(),
                employeePage.isLast()
        );
    }

    public EmployeeResponse getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .map(this::mapToResponse)
                .orElse(null);
    }

    private EmployeeResponse mapToResponse(Employee employee) {

        EmployeeResponse response = new EmployeeResponse();

        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setGender(employee.getGender());
        response.setAge(employee.getAge());
        response.setEmail(employee.getEmail());
        response.setPhone(employee.getPhone());
        response.setExtraDetail(employee.getExtraDetails());

        return response;
    }
}