package com.training.empmanager.notify;

import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class PushNotifier implements Notifier{
    @Override
    public void send(String message) {
        System.out.println("Email: " + message);
    }
}
