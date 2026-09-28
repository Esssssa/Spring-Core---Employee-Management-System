package com.training.empmanager.repository;

import com.training.empmanager.model.Employee;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("dev")
public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<Integer, Employee> storage = new ConcurrentHashMap<>();

    @Override
    public void save(Employee employee) {
        storage.put(employee.getId(), employee);
    }

    @Override
    public Optional<Employee> findById(int id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(storage.values());
    }

    // SPRING CONCEPT: lifecycle callbacks
    @PostConstruct
    public void init() {
        System.out.println("[LIFECYCLE] InMemoryEmployeeRepository initialized (profile = dev)");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("[LIFECYCLE] InMemoryEmployeeRepository destroyed - " + storage.size() + " employees discarded");
    }
}
