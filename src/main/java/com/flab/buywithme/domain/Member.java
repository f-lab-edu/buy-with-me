package com.flab.buywithme.domain;

import static javax.persistence.FetchType.LAZY;

import com.google.common.annotations.VisibleForTesting;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@EqualsAndHashCode(of = "id")
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "address_id")
    private Address address;

    private String name;
    private String phoneNo;

    @Column(unique = true)
    private String loginId;

    private String password;

    public Member(Address address, String name, String phoneNo, String loginId,
            String password) {
        this.address = address;
        this.name = name;
        this.phoneNo = phoneNo;
        this.loginId = loginId;
        this.password = password;
    }

    @VisibleForTesting
    public void setId(Long id) {
        this.id = id;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
