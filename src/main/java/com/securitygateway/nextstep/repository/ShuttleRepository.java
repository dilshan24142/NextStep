package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.Shuttle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShuttleRepository extends JpaRepository<Shuttle, Long> {
    List<Shuttle> findByRouteContainingIgnoreCase(String route);
}