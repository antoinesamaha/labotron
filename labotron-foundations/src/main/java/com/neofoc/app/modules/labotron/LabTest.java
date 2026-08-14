package com.neofoc.app.modules.labotron;

import com.foc.annotations.model.FocData;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Cacheable
@Table(name = "lab_test",
		uniqueConstraints = {
				@UniqueConstraint(name = "uq_labtest_sample_label", columnNames = {"lab_sample_id", "label"})
		},
		indexes = {
				@Index(name = "idx_labtest_status", columnList = "status")
		}
)
@Data
@FocData
public class LabTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 15)
    private String label;

    @Column(nullable = false, length = 25)
    private String descrip;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false, length = 40)
    private String notes;

    @Column(nullable = false)
    private Integer resultOk;

    @Column(nullable = false, length = 10)
    private String unitLabel;

    @Column(nullable = false, length = 100)
    private String message;

    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false)
    private Boolean blocked;

//    @Column(nullable = false)
//    private Integer iSuggRef;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Instrument dispatchInstrument;

    @ManyToOne(optional = true)
    @JoinColumn(nullable = true)
    private Instrument actualInstrument;

//    @Column(nullable = false)
//    private Integer iRecRef;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private LabSample labSample;

    @Column(nullable = false)
    private Integer alarm;

    @Column(nullable = false, length = 1)
    private String priority;

    @Column(nullable = false)
    private Integer verificationPending;
}