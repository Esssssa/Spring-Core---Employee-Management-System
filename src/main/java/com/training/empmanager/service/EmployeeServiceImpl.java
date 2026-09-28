package com.training.empmanager.service;

import com.training.empmanager.audit.AuditLogger;
import com.training.empmanager.model.Employee;
import com.training.empmanager.notify.NotificationManager;
import com.training.empmanager.repository.EmployeeRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeValidator validator;
    private final NotificationManager notificationManager;

    private ObjectProvider<AuditLogger> auditLoggerProvider;

    @Value("${raise.max-percentage}")
    private double maxRaisePercentage;

    @Value("${company.currency}")
    private String currency;


    public EmployeeServiceImpl(EmployeeRepository repository,
                               EmployeeValidator validator,
                               NotificationManager notificationManager) {
        this.repository = repository;
        this.validator = validator;
        this.notificationManager = notificationManager;
    }

    // SPRING CONCEPT: setter injection.
    // SPRING CONCEPT: ObjectProvider solves the "prototype inside a singleton" problem:
    // we keep the PROVIDER and ask it for a fresh AuditLogger every time we need one.
    @Autowired
    public void setAuditLoggerProvider(ObjectProvider<AuditLogger> auditLoggerProvider) {
        this.auditLoggerProvider = auditLoggerProvider;
    }

    @Override
    public void addEmployee(Employee employee) {
        validator.validate(employee);
        repository.save(employee);
        auditLoggerProvider.getObject().log("Added employee " + employee.getName());
        notificationManager.notifyAllChannels("Welcome aboard, " + employee.getName()
                + " (" + employee.getDepartment() + ")!");
    }

    @Override
    public Employee getEmployeeById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new InvalidEmployeeException("No employee with id " + id));
    }

    @Override
    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    @Override
    public void giveRaise(int id, double percentage) {
        if (percentage <= 0) {
            throw new IllegalArgumentException("Raise percentage must be positive");
        }
        if (percentage > maxRaisePercentage) {        // the limit comes from application.properties
            notificationManager.notifyUrgent("Rejected raise of " + percentage + "% for employee " + id);
            throw new IllegalArgumentException("Raise of " + percentage + "% exceeds the maximum of "
                    + maxRaisePercentage + "%");
        }

        Employee employee = getEmployeeById(id);
        validator.validate(employee);                 // validate BEFORE giving the raise

        double oldSalary = employee.getSalary();
        employee.setSalary(oldSalary * (1 + percentage / 100));
        repository.save(employee);

        auditLoggerProvider.getObject().log("Raise " + percentage + "% for " + employee.getName());
        notificationManager.notifyAllChannels(String.format("Congratulations %s! Salary %.2f -> %.2f %s",
                employee.getName(), oldSalary, employee.getSalary(), currency));
    }

    @PostConstruct
    public void init() {
        System.out.println("[LIFECYCLE] EmployeeServiceImpl initialized, using "
                + repository.getClass().getSimpleName());
    }

    @PreDestroy
    public void destroy() {
        System.out.println("[LIFECYCLE] EmployeeServiceImpl destroyed");
    }
}