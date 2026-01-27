package com.neofoc.app.modules.labotron;


import com.foc.admin.FocGroup;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Cacheable
@Table(name = "app_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer allowSampleModification;

    @Column(nullable = false)
    private Integer allowResultConfirmation;

    @Column(nullable = false)
    private Integer allowSendingToInstrument;

    @Column(nullable = false)
    private Integer allowConfiguration;

    @Column(nullable = false)
    private Integer allowConnection;

    @Column(nullable = false)
    private Integer allowMonitoring;

    @Column(nullable = false)
    private Integer allowOnHoldModification;

    @Column(nullable = false)
    private Integer focGroupId;
}