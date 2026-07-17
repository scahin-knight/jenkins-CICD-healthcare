package com.healthwatch.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    private String vital;        // HR, BP, SPO2, etc.
    private String reading;      // The actual reading string
    
    @Enumerated(EnumType.STRING)
    private AlertSeverity severity;
    
    private String message;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
    
    private boolean acknowledged;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acknowledged_by")
    private User acknowledgedBy;
    
    private LocalDateTime acknowledgedAt;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
