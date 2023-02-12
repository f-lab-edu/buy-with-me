package com.flab.buywithme.event;

public interface DomainEventHandler<T> {

    boolean canHandle(DomainEvent<T> event);

    void handle(DomainEvent<T> event);
}
