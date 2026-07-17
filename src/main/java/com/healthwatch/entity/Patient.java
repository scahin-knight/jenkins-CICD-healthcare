package com.healthwatch.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String patientCode;

    @Column(nullable = false)
    private String fullName;

    private Integer age;
    private String gender;
    private String bloodGroup;
    private String phoneNumber;
    private String emergencyContact;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    private String roomNumber;
    private LocalDate admissionDate;
    private String admissionStatus;

    @Column(columnDefinition = "TEXT")
    private String medicalHistory;
    
    @Column(columnDefinition = "TEXT")
    private String allergies;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
