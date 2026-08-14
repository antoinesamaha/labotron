package com.neofoc.app.drivers.gempremier3500;

import com.neofoc.app.drivers.astm.FrameReader;
import com.neofoc.app.drivers.astm.InformationInquiryReader;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Gem3500_InformationInquiryReader extends InformationInquiryReader {

	private String rackNumber     = "";
	private String tubePosition   = "";
	private String sampleId       = "";
	private String sampleIdAttrib = "";

	private boolean demographicsOnly = false;

	private int FLD_SAMPLE_LOCATION_AND_ID = 2;
	private int FLD_DEMOGRAPHICS_ONLY      = 10;

	private int CMP_RACK_NBR            = 99;// Not used in Gem3500, but kept for compatibility with the base class
	private int CMP_TUBE_POS            = 99;// Not used in Gem3500, but kept for compatibility with the base class
	private int CMP_SAMPLE_ID           = 0;
	private int CMP_SAMPLE_ID_ATTRIBUTE = 99;// Not used in Gem3500, but kept for compatibility with the base class

	//The defaut astm one: Q|1|000001^01^[.][.][.][.][.][.][.][.]4813192^B||^^^050^APTT-PSL|0|2023013112561
	//The gem 3500 one: Q|1|4813192||||||||||D
	
	public void readToken(String token, int fieldPos, int compPos) {
		com.foc.Globals.logDetail(" fieldPos:"+fieldPos+" compPos:"+compPos+" token:"+token);
		
		if(fieldPos == FLD_SAMPLE_LOCATION_AND_ID){
			if(compPos == CMP_RACK_NBR) {
				rackNumber = token;
				com.foc.Globals.logDetail("    rackNumber = "+token);
			}else if(compPos == CMP_TUBE_POS) {
				tubePosition = token;
				com.foc.Globals.logDetail("    tubePosition = "+token);
			}else if(compPos == CMP_SAMPLE_ID) {
				sampleId = token;
				com.foc.Globals.logDetail("    sampleId = "+token);
			}else if(compPos == CMP_SAMPLE_ID_ATTRIBUTE) {
				sampleIdAttrib = token;
				com.foc.Globals.logDetail("    sampleIdAttrib = "+token);
			}
		}

		if(fieldPos == FLD_DEMOGRAPHICS_ONLY){
			if(token.equalsIgnoreCase("D")){
				demographicsOnly = true;
				com.foc.Globals.logDetail("    demographicsOnly = true");
			}else{
				demographicsOnly = false;
				com.foc.Globals.logDetail("    demographicsOnly = false");
			}
		}
	}
	
	public void clear() {
		rackNumber     = "";
		tubePosition   = "";
		sampleId       = "";
		sampleIdAttrib = "";
	}

	public String getRackNumber() {
		return rackNumber;
	}

	public String getTubePosition() {
		return tubePosition;
	}

	public String getSampleId() {
		return sampleId;
	}

	public String getSampleIdAttrib() {
		return sampleIdAttrib;
	}

	public boolean isDemographicsOnly() {
		return demographicsOnly;
	}

}
