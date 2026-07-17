package com.healthwatch.scheduler;

import com.healthwatch.dto.VitalReadingDto;
import com.healthwatch.entity.Patient;
import com.healthwatch.entity.VitalReading;
import com.healthwatch.repository.PatientRepository;
import com.healthwatch.repository.VitalReadingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class VitalSimulationService {

    private final PatientRepository patientRepository;
    private final VitalReadingRepository vitalReadingRepository;
    private final com.healthwatch.service.AlertService alertService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Random random = new Random();

    @Scheduled(fixedRate = 3000)
    public void generateVitals() {
        List<Patient> patients = patientRepository.findAll();
        
        for (Patient patient : patients) {
            // Generate basic normal vitals
            int hr = 70 + random.nextInt(20);        // 70 - 89
            int sysBp = 110 + random.nextInt(20);    // 110 - 129
            int diaBp = 70 + random.nextInt(15);     // 70 - 84
            int spo2 = 96 + random.nextInt(5);       // 96 - 100
            double temp = 36.5 + (random.nextDouble() * 0.7); // 36.5 - 37.2

            // 10% chance for anomaly (Warning or Critical)
            if (random.nextInt(100) < 10) {
                hr = 50 + random.nextInt(70);        // 50 - 119
                sysBp = 90 + random.nextInt(60);     // 90 - 149
                spo2 = 88 + random.nextInt(12);      // 88 - 99
                temp = 35.5 + (random.nextDouble() * 4.0); // 35.5 - 39.5
            }

            VitalReading reading = VitalReading.builder()
                    .patient(patient)
                    .heartRate(hr)
                    .systolicBp(sysBp)
                    .diastolicBp(diaBp)
                    .spo2(spo2)
                    .temperature(Math.round(temp * 10.0) / 10.0)
                    .timestamp(LocalDateTime.now())
                    .build();

            // vitalReadingRepository.save(reading); 
            // Optional: for true production history, we'd save it, but 3 seconds per patient generates a lot of data. 
            // We will save it for historical analytics purposes.
            vitalReadingRepository.save(reading);
            alertService.evaluateVitals(reading, patient);

            VitalReadingDto dto = VitalReadingDto.builder()
                    .patientId(patient.getId())
                    .heartRate(reading.getHeartRate())
                    .systolicBp(reading.getSystolicBp())
                    .diastolicBp(reading.getDiastolicBp())
                    .spo2(reading.getSpo2())
                    .temperature(reading.getTemperature())
                    .timestamp(reading.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_TIME))
                    .build();

            // Broadcast to specific patient topic
            messagingTemplate.convertAndSend("/topic/vitals/" + patient.getId(), dto);
            // Broadcast to dashboard (all patients)
            messagingTemplate.convertAndSend("/topic/dashboard", dto);
        }
    }
}
