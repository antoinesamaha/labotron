package com.neofoc.app.drivers.astm;

import com.neofoc.app.modules.labotron.focObjects.FocLabTest;

public class CommentLineReader extends FrameReader {

	private static final int POS_COMMENT_TEXT  = 3;

	private static final int COMP_COMMENT1    = 0;
	private static final int COMP_COMMENT2    = 1;
	private static final int COMP_COMMENT3    = 2;
	
	private String comment3 = ""; 
	private String comment2 = ""; 
	private String comment1 = ""; 
	
	public CommentLineReader(){
		super('|', '^');
	}

	public void setTest(FocLabTest test){
	}
	
	public void readToken(String token, int fieldPos, int compPos) {
		com.foc.Globals.logDetail(" fieldPos:"+fieldPos+" compPos:"+compPos+" token:"+token);
		
		if(fieldPos == POS_COMMENT_TEXT){
			if(compPos == COMP_COMMENT1){
				comment1 = token.trim();
			}else if(compPos == COMP_COMMENT2){
				comment2 = token.trim();
			}else if(compPos == COMP_COMMENT3){
				comment3 = token.trim();
			}
		}
	}
	
	public String getComment1() {
		return comment1;
	}
	
	public String getComment2() {
		return comment2;
	}
	
	public String getComment3() {
		return comment3;
	}
}
