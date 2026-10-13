package com.hydroshield.controller;

import com.hydroshield.dto.PublicWaterTestDTO;
import com.hydroshield.entity.HomeWaterTest;
import com.hydroshield.entity.User;
import com.hydroshield.repository.HomeWaterTestRepository;
import com.hydroshield.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/public-tests")
@CrossOrigin(origins = "*")
public class PublicTestController {

    @Autowired
    private HomeWaterTestRepository homeWaterTestRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> recordHomeTest(@RequestBody PublicWaterTestDTO dto) {
        Optional<User> userOpt = userRepository.findById(dto.getUserId() != null ? dto.getUserId() : 1L);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        LocalDateTime testDate = dto.getTestDate() != null ? LocalDateTime.parse(dto.getTestDate()) : LocalDateTime.now();

        HomeWaterTest test = HomeWaterTest.builder()
                .user(userOpt.get())
                .testDate(testDate)
                .waterSource(dto.getWaterSource() != null ? dto.getWaterSource() : "Borewell")
                .ph(dto.getPh())
                .turbidity(dto.getTurbidity())
                .tds(dto.getTds())
                .optionalLead(dto.getOptionalLead())
                .optionalCadmium(dto.getOptionalCadmium())
                .optionalIron(dto.getOptionalIron())
                .optionalArsenic(dto.getOptionalArsenic())
                .build();

        HomeWaterTest saved = homeWaterTestRepository.save(test);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<HomeWaterTest>> getUserHomeTests(@PathVariable Long userId) {
        return ResponseEntity.ok(homeWaterTestRepository.findByUserIdOrderByTestDateDesc(userId));
    }
}
