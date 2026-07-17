package com.healthwatch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VitalReadingDto {
    private Long patientId;
    private Integer heartRate;
    private Integer systolicBp;
    private Integer diastolicBp;
    private Integer bloodSugar;
    private Integer spo2;
    private Double temperature;
    private String timestamp;
}
