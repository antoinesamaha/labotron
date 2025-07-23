package com.neofoc.app.modules.labotron;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
//@Cacheable
@Table(name = "lab_sample")
@Data
public class LabSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 15, unique = true)
    private String sampleId;

    @Column(nullable = false, length = 15)
    private String patientId;

    @Column(nullable = false, length = 10)
    private String origin;

    @Column(nullable = false, length = 30)
    private String lastName;

    @Column(nullable = false, length = 30)
    private String firstName;

    @Column(nullable = false, length = 1)
    private String middleInitial;

    @Column(nullable = false, length = 1)
    private String sexe;

    @Column(nullable = false)
    private Integer liquideType;

    @Column(nullable = false)
    private Integer resultConfirmed;

    @Column(nullable = false)
    private Integer okToBeSent;

    @Column(nullable = false)
    private LocalDateTime entryDate;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private LocalDateTime dateOfBirth;
}
