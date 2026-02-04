package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.model.Stall;
import com.securitygateway.nextstep.repository.StallRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StallService {

    private final StallRepository stallRepository;

    public StallService(StallRepository stallRepository) {
        this.stallRepository = stallRepository;
    }

    public List<Stall> getAllStalls() {
        return stallRepository.findAll();
    }
}
