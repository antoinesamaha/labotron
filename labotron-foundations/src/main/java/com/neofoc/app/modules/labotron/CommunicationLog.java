package com.neofoc.app.modules.labotron;

import com.foc.annotations.model.FocData;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "communication_log")
@Data
@FocData
public class CommunicationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 36)
    private String uuid;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private Boolean received;

    @Column(nullable = false)
    private LocalDateTime receptionDateTime;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Instrument instrument;

    @Column(nullable = false, length = 200)
    private Integer communicationPoint;
    /*
1: Received LIS 2 Connector
2: Sent Connector 2 Driver
3: Received Connector 2 Driver
4: Sent Driver Instrument
5- Received Instrument Driver
6- Sent Driver Connector
7- Received Driver Connector
8- Sent Connector 2 LIS
     */

    @Column(nullable = false, length = 50)
    private String direction; // Received, Sent

    @Column(nullable = false, length = 50)
    private String sender;

    @Column(nullable = false, length = 50)
    private String receiver;

    @Column(nullable = false, length = 50)
    private String sampleId;

    @Lob
    @Column(nullable = false, length = 10000)
    private String jsonContent;

//    private boolean hasError;

}