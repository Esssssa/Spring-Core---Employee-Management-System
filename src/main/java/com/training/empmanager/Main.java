package com.training.empmanager;

import com.training.empmanager.audit.AuditLogger;
import com.training.empmanager.config.AppConfig;
import com.training.empmanager.config.CompanyProperties;
import com.training.empmanager.model.Employee;
import com.training.empmanager.notify.Notifier;
import com.training.empmanager.service.EmployeeService;
import com.training.empmanager.service.InvalidEmployeeException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        String profile = "dev";
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles(profile);
        context.register(AppConfig.class);
        context.refresh();

        EmployeeService service = context.getBean(EmployeeService.class);

        section("1. Add valid employees (validation -> save -> audit -> 3 notifiers)");
        service.addEmployee(new Employee(1, "Ahmed Hassan", "Engineering", 15000));
        service.addEmployee(new Employee(2, "Sara Ali", "HR", 12000));

        section("2. Add invalid employees (validation fails, handled gracefully)");
        tryAdd(service, new Employee(3, "   ", "Sales", 9000));
        tryAdd(service, new Employee(4, "Omar Hassan", "Sales", -500));

        section("3a. Raise within the limit (10%)");
        service.giveRaise(1, 10);

        section("3b. Raise above the limit (35%)");
        try {
            service.giveRaise(2, 35);
        } catch (IllegalArgumentException e) {
            System.out.println("   Raise rejected: " + e.getMessage());
        }

        section("4. Prototype vs singleton");
        AuditLogger logger1 = context.getBean(AuditLogger.class);
        AuditLogger logger2 = context.getBean(AuditLogger.class);
        AuditLogger logger3 = context.getBean(AuditLogger.class);
        System.out.println("   AuditLogger instances: #" + logger1.getInstanceNumber()
                + ", #" + logger2.getInstanceNumber() + ", #" + logger3.getInstanceNumber());
        System.out.println("   logger1 == logger2 ? " + (logger1 == logger2) + "   (prototype -> new object every time)");
        EmployeeService service2 = context.getBean(EmployeeService.class);
        System.out.println("   service == service2 ? " + (service == service2) + "    (singleton -> same object)");

        section("5. @Primary: asking for ONE Notifier");
        Notifier defaultNotifier = context.getBean(Notifier.class);
        System.out.println("   context.getBean(Notifier.class) -> " + defaultNotifier.getClass().getSimpleName());

        section("6. All employees");
        service.getAllEmployees().forEach(e -> System.out.println("   " + e));

        section("7. Injected @Value properties");
        System.out.println(context.getBean(CompanyProperties.class));

        section("8. Closing the context (@PreDestroy callbacks)");
        context.close();
    }

    private static void tryAdd(EmployeeService service, Employee employee) {
        try {
            service.addEmployee(employee);
        } catch (InvalidEmployeeException e) {
            System.out.println("   Could not add employee: " + e.getMessage());
        }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
