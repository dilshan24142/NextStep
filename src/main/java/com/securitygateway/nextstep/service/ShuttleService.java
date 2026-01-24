package com.securitygateway.nextstep.service;

import com.securitygateway.nextstep.model.Shuttle;
import com.securitygateway.nextstep.model.ShuttleImage;
import com.securitygateway.nextstep.repository.ShuttleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShuttleService {

    private final ShuttleRepository shuttleRepository;

    @Transactional
    public Shuttle saveShuttle(Shuttle shuttle, List<MultipartFile> files) throws IOException {
        if (shuttle.getId() != null) {
            shuttleRepository.findById(shuttle.getId()).ifPresent(existing -> {
                if (files != null && !files.isEmpty() && !files.get(0).isEmpty()) {
                    existing.getImages().clear();
                    shuttleRepository.saveAndFlush(existing);
                }
            });
        }

        if (files != null && !files.isEmpty() && !files.get(0).isEmpty()) {
            List<ShuttleImage> newImages = new ArrayList<>();
            for (MultipartFile file : files) {
                ShuttleImage img = new ShuttleImage();
                img.setImageData(file.getBytes());
                img.setShuttle(shuttle);
                newImages.add(img);
            }
            shuttle.setImages(newImages);
        }
        return shuttleRepository.save(shuttle);
    }
}