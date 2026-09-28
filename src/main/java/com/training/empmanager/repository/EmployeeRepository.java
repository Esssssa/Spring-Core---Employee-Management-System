package com.training.empmanager.repository;

import com.training.empmanager.model.Employee;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    void save(Employee employee);
    Optional<Employee> findById(int id);
    List<Employee> findAll();
}
