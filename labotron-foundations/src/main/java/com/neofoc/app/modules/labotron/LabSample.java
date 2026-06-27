package com.neofoc.app.modules.labotron;

import com.foc.annotations.model.FocData;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
//@Cacheable
@Table(name = "lab_sample",
		indexes = {
				@Index(name = "idx_labsample_patientid", columnList = "patient_id"),
				@Index(name = "idx_labsample_entrydt", columnList = "entry_date_time")
		}
)
@Data
@FocData
public class LabSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime entryDateTime;

    @Column(nullable = true)
    private LocalDateTime collectionDateTime;

    @Column(nullable = false, length = 15, unique = true)
    private String sampleId;

    @Column(nullable = true, length = 15)
    private String patientId;

    @Column(nullable = true, length = 10)
    private String origin;

    @Column(nullable = true, length = 30)
    private String lastName;

    @Column(nullable = true, length = 30)
    private String firstName;

    @Column(nullable = true, length = 30)
    private String middleName;

    @Column(nullable = true, length = 1)
    private String sex;

    @Column(nullable = true)
    private Integer liquidType;

    @Column(nullable = true)
    private Boolean resultConfirmed;

    @Column(nullable = true)
    private Boolean okToBeSent;

    @Column(nullable = true)
    private Integer age;

    @Column(nullable = true)
    private LocalDate dateOfBirth;

    @OneToMany(mappedBy = "labSample", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<LabTest> tests = new HashSet<>();

    @OneToMany(mappedBy = "labSample", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<LabMessage> labMessages = new HashSet<>();
}
