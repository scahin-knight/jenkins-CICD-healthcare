package com.healthwatch.repository;

import com.healthwatch.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByPatientIdOrderByTimestampDesc(Long patientId);
    List<Alert> findByAcknowledgedFalseOrderByTimestampDesc();
}
