package com.training.empmanager.config;


import com.training.empmanager.audit.AuditLogger;
import com.training.empmanager.service.EmployeeValidator;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan("com.training.empmanager")
@PropertySource("classpath:application.properties")
public class AppConfig {
    @Bean
    public EmployeeValidator employeeValidator() {
        return new EmployeeValidator();
    }
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AuditLogger auditLogger() {
        return new AuditLogger();
    }
}
