package com.healthwatch.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotRestController {

    @PostMapping("/ask")
    public Map<String, String> askQuestion(@RequestBody Map<String, String> payload) {
        String question = payload.get("question");
        
        // This is a Mock response. In a real scenario, this would call the AiAnalysisService
        String answer = "As an AI Assistant, I can provide general information. Regarding your question about '" + question + "': "
                + "This could be related to your recent lab results or vitals. Please consult with a healthcare professional for a specific medical diagnosis. "
                + "If you are asking about cholesterol, high cholesterol means you have too many lipids in your blood, which can increase risk of heart disease.";
                
        return Map.of("answer", answer);
    }
}
