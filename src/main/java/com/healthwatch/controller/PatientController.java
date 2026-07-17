package com.healthwatch.controller;

import com.healthwatch.entity.Patient;
import com.healthwatch.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final com.healthwatch.repository.MedicalReportRepository reportRepository;

    @GetMapping
    public String listPatients(Model model) {
        model.addAttribute("patients", patientService.getAllPatients());
        return "patients/list";
    }

    @GetMapping("/new")
    public String showAddPatientForm(Model model) {
        Patient patient = new Patient();
        // Generate a random patient code for simplicity
        patient.setPatientCode("P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        model.addAttribute("patient", patient);
        return "patients/add";
    }

    @PostMapping
    public String addPatient(@ModelAttribute("patient") Patient patient) {
        patient.setAdmissionStatus("Stable"); // Default status
        patient.setAdmissionDate(java.time.LocalDate.now());
        patientService.savePatient(patient);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public String getPatientDetails(@PathVariable Long id, Model model) {
        Patient patient = patientService.getPatientById(id).orElseThrow();
        model.addAttribute("patient", patient);
        model.addAttribute("reports", reportRepository.findByPatientIdOrderByUploadedAtDesc(id));
        return "patients/details";
    }
}
