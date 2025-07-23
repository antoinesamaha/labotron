package com.neofoc.app.modules.labotron;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
//@Cacheable
@Table(name = "instrument")
@Data
public class Instrument implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

//	@Column(nullable = false)
//	private Integer lkUserRef;

	@Column(nullable = false, length = 10, unique = true)
	private String code;

	@Column(nullable = false, length = 30)
	private String name;

//	@Column(nullable = false)
//	private Integer connected;

	@Column(nullable = false, length = 120)
	private String driverClassName;

//	@Column(nullable = false, length = 250)
//	private String propertiesFilePath;

//	@Column(nullable = false)
//	private Integer modeO;

	@Column(nullable = false)
	private Boolean waitForResultConfirmation;

	@Column(nullable = false)
	private Boolean onHold;

	@Column(nullable = false)
	private Integer dlyDbSamples2Send;

	@Column(nullable = false)
	private Integer dlySampleDispRefresh;

	@Column(nullable = false)
	private Integer dlyForReserveRetry;

	@Column(nullable = false)
	private Integer dlyForRetryLater;

	@Column(nullable = false)
	private Integer dlyForTimeOut;

	@Column(nullable = false, length = 10)
	private String comPort;

//	@Column(nullable = false)
//	private Integer comBaudeRate;

//	@Column(nullable = false)
//	private Integer comParity;

//	@Column(nullable = false)
//	private Integer comDataBits;

//	@Column(nullable = false)
//	private Integer comStopBit;

//	@Column(nullable = false)
//	private Integer isEmul;

//	@Column(nullable = false, length = 30)
//	private String relatedInstr;

//	@Column(nullable = false)
//	private Integer port;

}