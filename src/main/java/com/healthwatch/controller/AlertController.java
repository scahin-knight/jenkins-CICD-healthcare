package com.healthwatch.controller;

import com.healthwatch.entity.Alert;
import com.healthwatch.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertRepository alertRepository;

    @GetMapping
    public String listAlerts(Model model) {
        model.addAttribute("alerts", alertRepository.findByAcknowledgedFalseOrderByTimestampDesc());
        return "alerts/list";
    }
    
    @PostMapping("/{id}/acknowledge")
    public String acknowledgeAlert(@PathVariable Long id) {
        Alert alert = alertRepository.findById(id).orElseThrow();
        alert.setAcknowledged(true);
        alert.setAcknowledgedAt(java.time.LocalDateTime.now());
        alertRepository.save(alert);
        return "redirect:/alerts";
    }
}
