package com.neofoc.app.modules.labotron;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Entity
@Cacheable
@Table(name="test_group" )
@Data
public class TestGroup implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long       id ;
	protected String     type ;
	protected String     name ;
	protected Boolean    deprecated ;

}
