package com.training.empmanager.notify;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationManager {
    private final List<Notifier> notifiers;
    private final Notifier urgentNotifier;

    @Value("${notification.retry-count}")
    private int retryCount;

    public NotificationManager(List<Notifier> notifiers,
                               @Qualifier("smsNotifier") Notifier urgentNotifier) {
        this.notifiers = notifiers;
        this.urgentNotifier = urgentNotifier;
    }

    public void notifyAllChannels(String message) {
        for (Notifier notifer : notifiers) {
            sendWithRetry(notifer, message);
        }
    }

    public void notifyUrgent(String message) {
        sendWithRetry(urgentNotifier, "URGENT: " + message);
    }

    private void sendWithRetry(Notifier notifier, String message) {
        for (int attempt = 1; attempt <= retryCount; attempt++) {
            try {
                notifier.send(message);
                return;
            } catch (RuntimeException e) {
                System.out.println("   attempt " + attempt + "/" + retryCount + " failed: " + e.getMessage());
            }
        }
    }
}
