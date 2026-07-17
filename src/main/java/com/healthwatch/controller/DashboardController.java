package com.healthwatch.controller;

import com.healthwatch.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final PatientService patientService;

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("totalPatients", patientService.countPatients());
        model.addAttribute("stablePatients", patientService.countByStatus("Stable"));
        model.addAttribute("warningPatients", patientService.countByStatus("Warning"));
        model.addAttribute("criticalPatients", patientService.countByStatus("Critical"));
        model.addAttribute("patients", patientService.getAllPatients());
        
        return "dashboard";
    }
}
