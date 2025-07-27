package com.neofoc.app.drivers.astm;

public class InformationInquiryReader extends FrameReader{

	private String rackNumber     = "";
	private String tubePosition   = "";
	private String sampleId       = "";
	private String sampleIdAttrib = "";
	
	private static final int FLD_SAMPLE_LOCATION_AND_ID = 2;
	
	private static final int CMP_RACK_NBR            = 0;
	private static final int CMP_TUBE_POS            = 1;
	private static final int CMP_SAMPLE_ID           = 2;
	private static final int CMP_SAMPLE_ID_ATTRIBUTE = 3;
	
	public InformationInquiryReader(){
		super('|', '^');
	}

	//Q|1|000001^01^[.][.][.][.][.][.][.][.]4813192^B||^^^050^APTT-PSL|0|2023013112561
	
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
	
}
