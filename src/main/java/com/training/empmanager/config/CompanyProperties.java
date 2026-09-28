package com.training.empmanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class CompanyProperties {

    @Value("${company.name}")
    private String companyName;

    @Value("${company.currency}")
    private String currency;

    @Value("${notification.retry-count}")
    private int retryCount;

    @Value("${raise.max-percentage}")
    private double maxRaisePercentage;

    @Override
    public String toString() {
        return String.format(
                "   company.name             = %s%n" +
                        "   company.currency         = %s%n" +
                        "   notification.retry-count = %d%n" +
                        "   raise.max-percentage     = %f",
                companyName,
                currency,
                retryCount,
                maxRaisePercentage
        );
    }
}


