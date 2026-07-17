package com.healthwatch.ai;

import com.healthwatch.entity.Patient;
import com.healthwatch.entity.VitalReading;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Primary
@Slf4j
public class MockAiAnalysisService implements AiAnalysisService {

    @Override
    public String analyzeReport(String extractedText, Patient patient, Optional<VitalReading> latestVitals) {
        log.info("MockAiAnalysisService: Analyzing report for patient {}", patient.getFullName());
        
        try {
            Thread.sleep(1500); // Simulate AI processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        StringBuilder html = new StringBuilder();
        html.append("<h4>AI Health Summary</h4>");
        html.append("<div class='alert alert-warning'><strong>Health Risk Assessment:</strong> 🟠 High Risk</div>");
        
        html.append("<h5>Key Findings</h5>");
        html.append("<ul>");
        html.append("<li><strong class='text-danger'>Hemoglobin:</strong> 11.5 g/dL (Low). This indicates mild anemia.</li>");
        html.append("<li><strong class='text-danger'>WBC Count:</strong> 11,500 /µL (High). This suggests a possible infection or inflammation.</li>");
        html.append("<li><strong class='text-danger'>Total Cholesterol:</strong> 240 mg/dL (High). Above recommended range.</li>");
        html.append("<li><strong class='text-danger'>LDL Cholesterol:</strong> 160 mg/dL (High). High levels can increase risk of heart disease.</li>");
        html.append("</ul>");

        html.append("<h5>Live Vitals Correlation</h5>");
        if (latestVitals.isPresent()) {
            VitalReading vr = latestVitals.get();
            html.append("<p>Patient's current Heart Rate is <strong>").append(vr.getHeartRate()).append(" bpm</strong> and Blood Pressure is <strong>")
                .append(vr.getSystolicBp()).append("/").append(vr.getDiastolicBp()).append(" mmHg</strong>.</p>");
            if (vr.getSystolicBp() > 130) {
                html.append("<p class='text-warning'>The elevated blood pressure combined with high cholesterol is a risk factor.</p>");
            }
        } else {
            html.append("<p>No live vitals available for correlation.</p>");
        }

        html.append("<h5>Recommendations & Lifestyle Suggestions</h5>");
        html.append("<ul>");
        html.append("<li>Adopt a heart-healthy diet low in saturated fats to help manage cholesterol levels.</li>");
        html.append("<li>Increase intake of iron-rich foods to address low hemoglobin.</li>");
        html.append("<li>Stay hydrated and monitor temperature for signs of worsening infection.</li>");
        html.append("</ul>");

        html.append("<hr>");
        html.append("<div class='text-muted small'><em>Disclaimer: This AI summary is for educational purposes only and is not a medical diagnosis. Please consult a healthcare professional.</em></div>");

        return html.toString();
    }
}
