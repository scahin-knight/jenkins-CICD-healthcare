package com.healthwatch.service;

import com.healthwatch.entity.Alert;
import com.healthwatch.entity.AlertSeverity;
import com.healthwatch.entity.Patient;
import com.healthwatch.entity.VitalReading;
import com.healthwatch.repository.AlertRepository;
import com.healthwatch.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;
    private final PatientRepository patientRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${healthwatch.thresholds.hr.warning-low:60}")
    private int hrWarningLow;
    @Value("${healthwatch.thresholds.hr.warning-high:100}")
    private int hrWarningHigh;
    @Value("${healthwatch.thresholds.hr.critical-low:40}")
    private int hrCriticalLow;
    @Value("${healthwatch.thresholds.hr.critical-high:120}")
    private int hrCriticalHigh;

    @Value("${healthwatch.thresholds.bp-sys.warning-low:90}")
    private int sysBpWarningLow;
    @Value("${healthwatch.thresholds.bp-sys.warning-high:130}")
    private int sysBpWarningHigh;
    @Value("${healthwatch.thresholds.bp-sys.critical-low:70}")
    private int sysBpCriticalLow;
    @Value("${healthwatch.thresholds.bp-sys.critical-high:160}")
    private int sysBpCriticalHigh;

    @Value("${healthwatch.thresholds.spo2.warning:95}")
    private int spo2Warning;
    @Value("${healthwatch.thresholds.spo2.critical:90}")
    private int spo2Critical;

    @Transactional
    public void evaluateVitals(VitalReading reading, Patient patient) {
        evaluateSingleVital(patient, "Heart Rate", reading.getHeartRate(), hrWarningLow, hrWarningHigh, hrCriticalLow, hrCriticalHigh, "bpm");
        evaluateSingleVital(patient, "Systolic BP", reading.getSystolicBp(), sysBpWarningLow, sysBpWarningHigh, sysBpCriticalLow, sysBpCriticalHigh, "mmHg");
        
        // SpO2 logic is slightly different (only low matters usually)
        if (reading.getSpo2() <= spo2Critical) {
            createAndSendAlert(patient, "SpO2", reading.getSpo2() + "%", AlertSeverity.CRITICAL, "Critical Low SpO2");
        } else if (reading.getSpo2() <= spo2Warning) {
            createAndSendAlert(patient, "SpO2", reading.getSpo2() + "%", AlertSeverity.WARNING, "Low SpO2");
        }
    }

    private void evaluateSingleVital(Patient patient, String vitalName, int value, int wLow, int wHigh, int cLow, int cHigh, String unit) {
        if (value <= cLow) {
            createAndSendAlert(patient, vitalName, value + " " + unit, AlertSeverity.CRITICAL, "Critical Low " + vitalName);
        } else if (value >= cHigh) {
            createAndSendAlert(patient, vitalName, value + " " + unit, AlertSeverity.CRITICAL, "Critical High " + vitalName);
        } else if (value <= wLow) {
            createAndSendAlert(patient, vitalName, value + " " + unit, AlertSeverity.WARNING, "Warning Low " + vitalName);
        } else if (value >= wHigh) {
            createAndSendAlert(patient, vitalName, value + " " + unit, AlertSeverity.WARNING, "Warning High " + vitalName);
        }
    }

    private void createAndSendAlert(Patient patient, String vital, String readingStr, AlertSeverity severity, String message) {
        Alert alert = Alert.builder()
                .patient(patient)
                .vital(vital)
                .reading(readingStr)
                .severity(severity)
                .message(message)
                .acknowledged(false)
                .build();
        
        alertRepository.save(alert);
        
        // Update patient status if severity is higher
        if (severity == AlertSeverity.CRITICAL || (severity == AlertSeverity.WARNING && !"Critical".equals(patient.getAdmissionStatus()))) {
            patient.setAdmissionStatus(severity == AlertSeverity.CRITICAL ? "Critical" : "Warning");
            patientRepository.save(patient);
        }

        Map<String, Object> alertMsg = new HashMap<>();
        alertMsg.put("id", alert.getId());
        alertMsg.put("patientId", patient.getId());
        alertMsg.put("patientName", patient.getFullName());
        alertMsg.put("vital", vital);
        alertMsg.put("reading", readingStr);
        alertMsg.put("severity", severity.name());
        alertMsg.put("message", message);
        alertMsg.put("timestamp", alert.getTimestamp().toString());

        messagingTemplate.convertAndSend("/topic/alerts", alertMsg);
    }
}
