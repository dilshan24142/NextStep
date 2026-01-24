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
        // 1. පවතින Shuttle එකක් Update කරන්නේ නම්
        if (shuttle.getId() != null) {
            return shuttleRepository.findById(shuttle.getId()).map(existing -> {
                // විස්තර update කිරීම
                existing.setBusName(shuttle.getBusName());
                existing.setBusNumber(shuttle.getBusNumber());
                existing.setRoute(shuttle.getRoute());
                existing.setMorningStartTime(shuttle.getMorningStartTime());
                existing.setEveningDepartureTime(shuttle.getEveningDepartureTime());
                existing.setPhoneNumber(shuttle.getPhoneNumber());
                existing.setAdditionalDetails(shuttle.getAdditionalDetails());

                // අලුතින් පින්තූර එවා ඇත්නම් පමණක් පරණ ඒවා මකා අලුත් ඒවා ඇතුළත් කිරීම
                if (files != null && !files.isEmpty() && !files.get(0).isEmpty()) {
                    existing.getImages().clear();
                    // පරණ පින්තූර DB එකෙන් ඉවත් කිරීම තහවුරු කිරීමට flush කිරීම
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
                // පින්තූර එවන්නේ නැත්නම් existing.getImages() වල පරණ ඒවා එලෙසම පවතී.
                return shuttleRepository.save(existing);
            }).orElseGet(() -> shuttleRepository.save(shuttle)); // ID එක තිබුණත් record එක නැත්නම් අලුතින් save කරයි.
        }

        // 2. අලුතින්ම Shuttle එකක් සාදන්නේ නම් (Create New)
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