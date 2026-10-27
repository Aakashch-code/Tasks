package com.example.empora.dto;

import lombok.Data;

import java.util.Map;

@Data
public class EmployeeRequest {

    private String name;
    private String gender;
    private Integer age;
    private String email;
    private String phone;
    private Map<String,Object> extraDetail;
}
