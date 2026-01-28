package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.LostFoundComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LostFoundCommentRepository extends JpaRepository<LostFoundComment, Long> {
}
