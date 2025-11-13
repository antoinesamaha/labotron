package com.neofoc.app.drivers.yumizenP8000;


import com.neofoc.app.drivers.astm.FrameReader;

//Patient line reader
public class MSHLineReader extends FrameReader {
	
	private String controlId = null;
	
	public MSHLineReader(){
		super('|', '^');
	}

	/*
	MSH|^~\&|YP8K|^^|^^|^^|20160705100955||OUL^R22^OUL_R22|YP8K20160705100955|P|2.5|||||||
	 */
	public void readToken(String token, int fieldPos, int compPos) {
		com.foc.Globals.logDetail(" fieldPos:"+fieldPos+" compPos:"+compPos+" token:"+token);
		
		if(fieldPos == 9 && compPos==0) controlId = new String(token);
	}

	public String getControlId() {
		return controlId != null ? controlId : "";
	}
}
