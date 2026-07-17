package com.healthwatch.controller;

import com.healthwatch.entity.VitalReading;
import com.healthwatch.repository.VitalReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsRestController {

    private final VitalReadingRepository vitalReadingRepository;

    @GetMapping("/vitals/{patientId}")
    public ResponseEntity<List<VitalReading>> getPatientVitals(@PathVariable Long patientId) {
        // In a real app, you'd add pagination and time filters
        List<VitalReading> vitals = vitalReadingRepository.findByPatientIdOrderByTimestampDesc(patientId);
        // Limit to last 50 for the chart
        if (vitals.size() > 50) {
            vitals = vitals.subList(0, 50);
        }
        return ResponseEntity.ok(vitals);
    }
}
