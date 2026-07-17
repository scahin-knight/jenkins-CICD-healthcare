package com.healthwatch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatbotController {

    @GetMapping("/ai-assistant")
    public String showChatbot() {
        return "chatbot/index";
    }
}
