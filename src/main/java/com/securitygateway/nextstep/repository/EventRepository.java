package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {}
