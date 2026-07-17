package com.healthwatch.ai;

import com.healthwatch.entity.Patient;
import com.healthwatch.entity.VitalReading;

import java.util.Optional;

public interface AiAnalysisService {
    /**
     * Analyzes OCR extracted text from a medical report and correlates it with current vitals.
     * @param extractedText Text extracted by the OCR service
     * @param patient The patient who owns the report
     * @param latestVitals The latest vital readings of the patient, if any
     * @return HTML formatted AI summary
     */
    String analyzeReport(String extractedText, Patient patient, Optional<VitalReading> latestVitals);
}
