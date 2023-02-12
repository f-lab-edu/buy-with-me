package com.flab.buywithme.event;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Async
@Component
public class DomainEventListener {

    @Autowired
    private List<DomainEventHandler> handlers;

    @EventListener
    public void handleDomainEvent(DomainEvent event) {
        for (DomainEventHandler handler : handlers) {
            if (handler.canHandle(event)) {
                handler.handle(event);
            }
        }
    }
}
