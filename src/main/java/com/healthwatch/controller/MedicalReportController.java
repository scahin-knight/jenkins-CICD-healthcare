package com.healthwatch.controller;

import com.healthwatch.entity.MedicalReport;
import com.healthwatch.entity.Patient;
import com.healthwatch.entity.User;
import com.healthwatch.entity.VitalReading;
import com.healthwatch.ocr.OcrService;
import com.healthwatch.ai.AiAnalysisService;
import com.healthwatch.repository.MedicalReportRepository;
import com.healthwatch.repository.UserRepository;
import com.healthwatch.repository.VitalReadingRepository;
import com.healthwatch.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class MedicalReportController {

    private final MedicalReportRepository reportRepository;
    private final PatientService patientService;
    private final OcrService ocrService;
    private final AiAnalysisService aiAnalysisService;
    private final VitalReadingRepository vitalReadingRepository;
    private final UserRepository userRepository;

    // A temporary upload directory (in a real app, use S3 or a secure location)
    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public String uploadReport(@RequestParam("file") MultipartFile file, 
                               @RequestParam("patientId") Long patientId,
                               Authentication authentication) {
        
        if (file.isEmpty()) {
            return "redirect:/patients/" + patientId + "?error=File is empty";
        }

        try {
            // Ensure upload dir exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save file locally
            String storedFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), filePath);

            Patient patient = patientService.getPatientById(patientId).orElseThrow();
            
            // Execute OCR
            String extractedText = ocrService.extractText(file);
            
            // Get latest vitals for correlation
            Optional<VitalReading> latestVitals = vitalReadingRepository.findFirstByPatientIdOrderByTimestampDesc(patientId);
            
            // Execute AI Analysis
            String aiSummary = aiAnalysisService.analyzeReport(extractedText, patient, latestVitals);
            
            User uploader = userRepository.findByUsername(authentication.getName()).orElse(null);

            // Save Report Entity
            MedicalReport report = MedicalReport.builder()
                    .patient(patient)
                    .originalFileName(file.getOriginalFilename())
                    .storedFileName(storedFileName)
                    .fileType(file.getContentType())
                    .extractedText(extractedText)
                    .aiAnalysis(aiSummary)
                    .uploadedBy(uploader)
                    .build();
                    
            reportRepository.save(report);

        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/patients/" + patientId + "?error=Upload failed";
        }

        return "redirect:/patients/" + patientId + "?success=Report uploaded and analyzed successfully";
    }
}
