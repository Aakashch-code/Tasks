package com.example.empora.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Data
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String gender;

    private Integer age;

    private String email;

    private String phone;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "extra_details", columnDefinition = "jsonb")
    private Map<String, Object> extraDetails;
}