package com.neofoc.app.modules.labotron;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Cacheable
@Table(name="labotron_sample" )
@Data
public class LabotronSample implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long       id ;

	protected String     name ;
	protected String     serviceHost ;
//	protected LocalDate  currentDateTime;
//	protected LocalDate  collectionDate;
	protected String     sampleId ;
	protected String     sampleType ;

//
//	-- l3.lissample definition
//
//	CREATE TABLE `lissample` (
//			`CURRENT_DATE_TIME` date NOT NULL,
//			`SAMPLE_ID` varchar(15) NOT NULL,
//  `SAMPLE_TYPE` varchar(10) NOT NULL,
//  `COLLECTION_DATE` date NOT NULL,
//			`DATE_OF_BIRTH` date NOT NULL,
//			`FIRST_NAME` varchar(30) NOT NULL,
//  `LAST_NAME` varchar(30) NOT NULL,
//  `MIDDLE_INITIAL` varchar(1) NOT NULL,
//  `AGE` int(11) NOT NULL,
//  `SEX` varchar(1) NOT NULL,
//  `PATIENT_ID` varchar(15) NOT NULL,
//  `ORIGIN` varchar(10) NOT NULL
//) ENGINE=InnoDB DEFAULT CHARSET=latin1;


}
