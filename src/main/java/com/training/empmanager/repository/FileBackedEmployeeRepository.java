package com.training.empmanager.repository;

import com.training.empmanager.model.Employee;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("prod")
public class FileBackedEmployeeRepository implements EmployeeRepository {

    private final Path file;

    public FileBackedEmployeeRepository(@Value("${repository.file}") String fileName) {
        this.file = Path.of(fileName);
    }

    @Override
    public void save(Employee employee) {
        List<Employee> all = findAll();
        all.removeIf(e -> e.getId() == employee.getId());
        all.add(employee);
        writeAll(all);
    }

    @Override
    public Optional<Employee> findById(int id) {
        return findAll().stream().filter(e -> e.getId() == id).findFirst();
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> result = new ArrayList<>();
        if (!Files.exists(file)) {
            return result;
        }
        try {
            for (String line : Files.readAllLines(file)) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                result.add(new Employee(Integer.parseInt(parts[0]), parts[1], parts[2], Double.parseDouble(parts[3])));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return result;
    }

    private void writeAll(List<Employee> employees) {
        List<String> lines = new ArrayList<>();
        for (Employee e : employees) {
            lines.add(e.getId() + "," + e.getName() + "," + e.getDepartment() + "," + e.getSalary());
        }
        try {
            Files.write(file, lines);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @PostConstruct
    public void init() {
        System.out.println("[LIFECYCLE] FileBackedEmployeeRepository initialized (profile = prod), file = " + file.toAbsolutePath());
    }

    @PreDestroy
    public void destroy() {
        System.out.println("[LIFECYCLE] FileBackedEmployeeRepository destroyed - data kept in " + file);
    }
}