package com.healthwatch.ocr;

import org.springframework.web.multipart.MultipartFile;

public interface OcrService {
    /**
     * Extracts text from a medical report file (PDF, JPG, PNG).
     * @param file The uploaded file
     * @return Extracted raw text
     */
    String extractText(MultipartFile file);
}
