package com.neofoc.app.modules.labotron;

import com.foc.annotations.model.FocData;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Cacheable
@Table(name = "lab_message")
@Data
@FocData
public class LabMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Instrument instrument;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private LabSample labSample;

    @Column(nullable = false, length = 250)
    private String message;

    @Column(nullable = false)
    private Integer status;
}