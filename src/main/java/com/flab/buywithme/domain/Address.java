package com.flab.buywithme.domain;

import com.google.common.annotations.VisibleForTesting;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UniqueAddress", columnNames = {"depth1", "depth2", "depth3"})})
@EqualsAndHashCode(of = "id")
@Getter
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    private String depth1;
    private String depth2;
    private String depth3;

    public Address(String depth1, String depth2, String depth3) {
        this.depth1 = depth1;
        this.depth2 = depth2;
        this.depth3 = depth3;
    }

    @VisibleForTesting
    public void setId(Long id) {
        this.id = id;
    }
}
