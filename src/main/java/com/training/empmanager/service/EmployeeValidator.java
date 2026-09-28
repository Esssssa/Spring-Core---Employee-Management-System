package com.training.empmanager.service;

import com.training.empmanager.model.Employee;

public class EmployeeValidator {
    public void validate(Employee employee) {
        if (employee == null) {
            throw new InvalidEmployeeException("Employee must not be null");
        }
        if (employee.getName() == null || employee.getName().isBlank()) {
            throw new InvalidEmployeeException("Employee name must not be blank");
        }
        if (employee.getSalary() < 0) {
            throw new InvalidEmployeeException("Salary must not be negative (was " + employee.getSalary() + ")");
        }
    }
}
