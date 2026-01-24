package com.securitygateway.nextstep.controller;

import com.securitygateway.nextstep.model.Shuttle;
import com.securitygateway.nextstep.payload.responses.GeneralAPIResponse;
import com.securitygateway.nextstep.service.ShuttleService;
import com.securitygateway.nextstep.repository.ShuttleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173") // මෙය අනිවාර්යයෙන්ම තිබිය යුතුයි
@RestController
@RequestMapping("/api/v1/shuttle")
@RequiredArgsConstructor
public class ShuttleController {

    private final ShuttleService shuttleService;
    private final ShuttleRepository shuttleRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addShuttle(@ModelAttribute Shuttle shuttle,
                                        @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        try {
            shuttleService.saveShuttle(shuttle, files);
            return ResponseEntity.ok(GeneralAPIResponse.builder()
                    .message("Shuttle saved successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Shuttle> getAllShuttles() {
        return shuttleRepository.findAll();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateShuttle(@PathVariable Long id,
                                           @ModelAttribute Shuttle shuttle,
                                           @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        try {
            shuttle.setId(id); // URL එකේ තියෙන ID එක Model එකට set කරන්න
            shuttleService.saveShuttle(shuttle, files);
            return ResponseEntity.ok(GeneralAPIResponse.builder()
                    .message("Shuttle updated successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteShuttle(@PathVariable Long id) {
        shuttleRepository.deleteById(id);
        return ResponseEntity.ok(GeneralAPIResponse.builder().message("Deleted successfully").build());
    }
}