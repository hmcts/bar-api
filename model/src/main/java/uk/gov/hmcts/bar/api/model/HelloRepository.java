package uk.gov.hmcts.bar.api.model;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HelloRepository extends JpaRepository<Hello,Integer>{
    Optional<Hello> findByHello(String hello);
}
