package com.neofoc.app.drivers.yumizenP8000;

import com.neofoc.app.drivers.astm.FrameReader;

//Patient line reader
public class PIDLineReader extends FrameReader{
	
	private String patientId = null;
  private String lastName = null;
	private String firstName = null;
	private String midInitial = null;
	
	public PIDLineReader(){
		super('|', '^');
	}

	public void reset() {
		patientId = null;
		lastName = null;
		firstName = null;
		midInitial = null;		
	}
	
	/*
		PID|||P0002^^^LIS^PI||DOE^JOHN^^^^^||19601206|M|||Main
		Street^^Springfield^NY^65466^USA^ATC1|||||||ABC123|||||||||||||Y
	 */
	public void readToken(String token, int fieldPos, int compPos) {
		com.foc.Globals.logDetail(" fieldPos:"+fieldPos+" compPos:"+compPos+" token:"+token);
		
		if(fieldPos == 3 && compPos==0) patientId = new String(token);
		if(fieldPos == 5 && compPos==0) lastName = new String(token);
		if(fieldPos == 5 && compPos==1) firstName = new String(token);
//		if(fieldPos == 5 && compPos==2) midInitial = new String(token);
	}

	public String getFirstName() {
		return firstName != null ? firstName : "";
	}

	public String getLastName() {
		return lastName != null ? lastName : "";
	}

	public String getMidInitial() {
		return midInitial != null ? midInitial : "";
	}
}
