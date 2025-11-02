package com.example.islab1.ws;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class PersonChangePublisher {
    private final SimpMessagingTemplate template;
    public PersonChangePublisher(SimpMessagingTemplate template) { this.template = template; }
    public void broadcastChange() {
        template.convertAndSend("/topic/persons", "changed");
    }
}
