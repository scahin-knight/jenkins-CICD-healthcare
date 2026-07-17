package com.healthwatch.ocr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Primary
@Slf4j
public class MockOcrService implements OcrService {

    @Override
    public String extractText(MultipartFile file) {
        log.info("MockOcrService: Extracting text from file {}", file.getOriginalFilename());
        // Simulating OCR delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        return """
               COMPLETE BLOOD COUNT (CBC)
               --------------------------------------
               Test                  Result     Unit     Ref. Range
               Hemoglobin            11.5       g/dL     13.0 - 17.0
               RBC Count             4.2        mill/µL  4.5 - 5.5
               WBC Count             11500      /µL      4000 - 10000
               Platelet Count        150000     /µL      150000 - 450000
               
               LIPID PROFILE
               --------------------------------------
               Total Cholesterol     240        mg/dL    < 200
               LDL Cholesterol       160        mg/dL    < 100
               HDL Cholesterol       35         mg/dL    > 40
               Triglycerides         210        mg/dL    < 150
               """;
    }
}
