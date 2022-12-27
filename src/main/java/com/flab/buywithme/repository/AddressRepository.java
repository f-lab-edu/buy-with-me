package com.flab.buywithme.repository;

import com.flab.buywithme.domain.Address;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByDepth1AndDepth2AndDepth3(String depth1, String depth2, String depth3);
}
