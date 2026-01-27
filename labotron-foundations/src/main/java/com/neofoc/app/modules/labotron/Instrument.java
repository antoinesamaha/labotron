package com.neofoc.app.modules.labotron;

import com.foc.annotations.model.FocData;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Cacheable
@Table(name = "instrument")
@Data
@FocData
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

	@Column(nullable = false)
	private Boolean connected;

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
	private Boolean started;

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

	@OneToMany(mappedBy = "instrument", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<TestLabelMap> testLabMaps = new HashSet<>();
}