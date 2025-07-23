package com.neofoc.app.modules.labotron;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Cacheable
@Table(name = "test_label_map")
@Data
public class TestLabelMap {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 30)
	private String lisTestLabel;

	@Column(nullable = false, length = 15)
	private String instrumentTestCode;

	@Column(nullable = false, length = 25)
	private String descrip;

	@ManyToOne(optional = false)
	@JoinColumn(nullable = false)
	private Instrument instrument;

	@Column(nullable = false)
	private Integer dayPriority;

	@Column(nullable = false)
	private Integer nightPriority;

	@Column(nullable = false)
	private Integer holidayPriority;

	@Column(nullable = false)
	private Integer calculated;

	@Column(nullable = false)
	private Integer onHold;

	@ManyToOne(optional = false)
	@JoinColumn(nullable = false)
	private TestGroup testGroup;

}