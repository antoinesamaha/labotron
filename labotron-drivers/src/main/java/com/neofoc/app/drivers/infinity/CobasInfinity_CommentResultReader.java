package com.neofoc.app.drivers.infinity;

import com.neofoc.app.drivers.astm.CommentResultReader;

public class CobasInfinity_CommentResultReader extends CommentResultReader {
	
	private static final int FLD_COMMENT_TEXT  = 3;
	
	private String comment = null;
	
	public void readToken(String token, int fieldPos, int compPos) {
		com.foc.Globals.logDetail(" fieldPos:"+fieldPos+" compPos:"+compPos+" token:"+token);
		
		if(fieldPos == FLD_COMMENT_TEXT){
			try{
				comment = token.trim();
			}catch(Exception e){
				com.foc.Globals.logException(e);
			}
		}
	}

	public String getComment() {
		return comment;
	}

}
