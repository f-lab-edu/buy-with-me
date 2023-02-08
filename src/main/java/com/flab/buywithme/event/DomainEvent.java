package com.flab.buywithme.event;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class DomainEvent<T> {

    private DomainEventType domainEventType;
    private T source;
}
