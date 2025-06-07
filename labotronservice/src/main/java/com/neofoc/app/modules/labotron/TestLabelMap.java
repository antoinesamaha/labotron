package com.neofoc.app.modules.labotron;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Cacheable
@Table(name="test_label_map" )
@Data
public class TestLabelMap implements Serializable {

	//--- ENTITY PRIMARY KEY
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long       id ;
	protected String     lisTestLabel ;
	protected String     instrumentTestCode ;
	protected String     descrip ;
	protected Integer    dayPriority ;
	protected Integer    nightPriority ;
	protected Integer    holidayPriority ;
	protected Boolean    calculated ;
	protected Boolean    onHold ;

	@ManyToOne
	protected TestGroup  testGroup ;

	@ManyToOne
	protected Instrument instrument ;
}
