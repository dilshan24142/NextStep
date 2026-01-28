package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.LostFoundItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostFoundRepository extends JpaRepository<LostFoundItem, Long> {
    Page<LostFoundItem> findAll(Pageable pageable);
}

