package com.example.empora.repository;

import com.example.empora.entity.Employee;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecification {

    public static Specification<Employee> filterEmployees(String search, String gender, String department) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. GLOBAL SEARCH (All Columns including Age and JSONB)
            if (StringUtils.hasText(search)) {
                String searchPattern = "%" + search.toLowerCase() + "%";

                Predicate namePredicate = cb.like(cb.lower(root.get("name")), searchPattern);
                Predicate emailPredicate = cb.like(cb.lower(root.get("email")), searchPattern);
                Predicate phonePredicate = cb.like(cb.lower(root.get("phone")), searchPattern);
                Predicate genderPredicate = cb.like(cb.lower(root.get("gender")), searchPattern);

                // Cast Integer to String to perform LIKE search
                Predicate agePredicate = cb.like(root.get("age").as(String.class), searchPattern);

                // Cast JSONB to String to search ALL keys and values inside the JSON object
                Predicate extraDetailsPredicate = cb.like(cb.lower(root.get("extraDetails").as(String.class)), searchPattern);

                predicates.add(cb.or(
                        namePredicate,
                        emailPredicate,
                        phonePredicate,
                        genderPredicate,
                        agePredicate,
                        extraDetailsPredicate
                ));
            }

            // 2. Exact Match Filter (Gender)
            if (StringUtils.hasText(gender)) {
                predicates.add(cb.equal(cb.lower(root.get("gender")), gender.toLowerCase()));
            }

            // 3. JSONB Exact Filter Example (Department)
            if (StringUtils.hasText(department)) {
                Predicate departmentPredicate = cb.equal(
                        cb.lower(cb.function("jsonb_extract_path_text", String.class, root.get("extraDetails"), cb.literal("department"))),
                        department.toLowerCase()
                );
                predicates.add(departmentPredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}