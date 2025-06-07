package com.neofoc.app.modules.labotron;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Cacheable
@Table(name="instrument" )
@Data
public class Instrument implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long       id ;
	protected String     code ;
	protected String     name ;
	protected Boolean    connected ;
	protected String     driverClassName ;
	protected Boolean    onHold ;
	protected String     comPort ;
	protected Integer    comBaudRate ;
	protected String     comParity ;
	protected Integer    comDataBits ;
	protected Integer    comStopBits ;
	protected Boolean    isEmul ;

	@OrderBy("lisTestLabel ASC")
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, mappedBy = "instrument")
//	@JoinColumn(name = "instrument_id")
	//@NotFound(action = NotFoundAction.IGNORE)
	protected Set<TestLabelMap> testLabelMaps = new HashSet<>();

}
