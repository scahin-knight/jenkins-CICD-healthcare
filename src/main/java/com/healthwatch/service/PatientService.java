package com.healthwatch.service;

import com.healthwatch.entity.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientService {
    List<Patient> getAllPatients();
    Optional<Patient> getPatientById(Long id);
    Patient savePatient(Patient patient);
    void deletePatient(Long id);
    long countPatients();
    long countByStatus(String status);
}
