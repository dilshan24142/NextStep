package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.LostFoundItem1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostFoundRepository1 extends JpaRepository<LostFoundItem1, Long> {
    Page<LostFoundItem1> findAll(Pageable pageable);
}

