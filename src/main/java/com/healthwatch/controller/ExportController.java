package com.healthwatch.controller;

import com.healthwatch.entity.Patient;
import com.healthwatch.entity.VitalReading;
import com.healthwatch.repository.PatientRepository;
import com.healthwatch.repository.VitalReadingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportController {

    private final PatientRepository patientRepository;
    private final VitalReadingRepository vitalReadingRepository;

    @GetMapping("/patient/{id}/csv")
    public ResponseEntity<byte[]> exportPatientVitalsCsv(@PathVariable Long id) {
        Patient patient = patientRepository.findById(id).orElseThrow();
        List<VitalReading> vitals = vitalReadingRepository.findByPatientIdOrderByTimestampDesc(id);

        StringBuilder csv = new StringBuilder();
        csv.append("Timestamp,Heart Rate(bpm),Systolic BP(mmHg),Diastolic BP(mmHg),SpO2(%),Temperature(C)\n");
        
        for (VitalReading vr : vitals) {
            csv.append(vr.getTimestamp()).append(",")
               .append(vr.getHeartRate()).append(",")
               .append(vr.getSystolicBp()).append(",")
               .append(vr.getDiastolicBp()).append(",")
               .append(vr.getSpo2()).append(",")
               .append(vr.getTemperature()).append("\n");
        }

        byte[] output = csv.toString().getBytes();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"patient_" + patient.getPatientCode() + "_vitals.csv\"")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(output.length)
                .body(output);
    }
}
