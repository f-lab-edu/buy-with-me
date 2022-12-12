package com.flab.buywithme.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id @GeneratedValue
    @Column(name = "address_id")
    private Long id;

    @Column(length = 20, nullable = false)
    private String depth1;

    @Column(length = 20, nullable = false)
    private String depth2;

    @Column(length = 20, nullable = false)
    private String depth3;
}
