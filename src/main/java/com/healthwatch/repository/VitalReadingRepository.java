package com.healthwatch.repository;

import com.healthwatch.entity.VitalReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VitalReadingRepository extends JpaRepository<VitalReading, Long> {
    List<VitalReading> findByPatientIdOrderByTimestampDesc(Long patientId);
    Optional<VitalReading> findFirstByPatientIdOrderByTimestampDesc(Long patientId);
}
