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
        // 1.If updating an existing Shuttle
        if (shuttle.getId() != null) {
            return shuttleRepository.findById(shuttle.getId()).map(existing -> {
                // Update details
                existing.setBusName(shuttle.getBusName());
                existing.setBusNumber(shuttle.getBusNumber());
                existing.setRoute(shuttle.getRoute());
                existing.setMorningStartTime(shuttle.getMorningStartTime());
                existing.setEveningDepartureTime(shuttle.getEveningDepartureTime());
                existing.setPhoneNumber(shuttle.getPhoneNumber());
                existing.setAdditionalDetails(shuttle.getAdditionalDetails());

                // Only replace old images if new ones are provided
                if (files != null && !files.isEmpty() && !files.get(0).isEmpty()) {
                    existing.getImages().clear();
                    // Flush to ensure old images are removed from the DB
                    shuttleRepository.saveAndFlush(existing);

                    for (MultipartFile file : files) {
                        ShuttleImage img = new ShuttleImage();
                        try {
                            img.setImageData(file.getBytes());
                            img.setFileType(file.getContentType());
                            img.setShuttle(existing);
                            existing.getImages().add(img);
                        } catch (IOException e) {
                            throw new RuntimeException("Error processing file", e);
                        }
                    }
                }
                // If no new images are sent, existing.getImages() will remain unchanged
                return shuttleRepository.save(existing);
            }).orElseGet(() -> shuttleRepository.save(shuttle)); // If ID exists but record not found, save as new
        }

        // 2. If creating a New Shuttle
        if (files != null && !files.isEmpty() && !files.get(0).isEmpty()) {
            List<ShuttleImage> newImages = new ArrayList<>();
            for (MultipartFile file : files) {
                ShuttleImage img = new ShuttleImage();
                img.setImageData(file.getBytes());
                img.setFileType(file.getContentType());
                img.setShuttle(shuttle);
                newImages.add(img);
            }
            shuttle.setImages(newImages);
        }
        return shuttleRepository.save(shuttle);
    }
}