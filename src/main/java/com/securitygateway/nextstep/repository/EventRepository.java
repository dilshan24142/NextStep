package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatus(Event.Status status);

    List<Event> findByCreatedBy(String email);
}
