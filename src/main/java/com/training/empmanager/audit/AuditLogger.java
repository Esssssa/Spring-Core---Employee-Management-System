package com.training.empmanager.audit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class AuditLogger {
    private static final AtomicInteger INSTANCE_COUNTER = new AtomicInteger();
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final int instanceNumber;

    public AuditLogger() {
        this.instanceNumber = INSTANCE_COUNTER.incrementAndGet();
        System.out.println("   (new AuditLogger instance #" + instanceNumber + " created)");
    }

    public void log(String action) {
        System.out.println("   [AUDIT #" + instanceNumber + "] " + LocalDateTime.now().format(FORMAT) + " - " + action);
    }

    public int getInstanceNumber() {
        return instanceNumber;
    }
}

