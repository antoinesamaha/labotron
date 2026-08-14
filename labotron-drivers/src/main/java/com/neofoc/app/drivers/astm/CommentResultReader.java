package com.neofoc.app.drivers.astm;

public class CommentResultReader extends FrameReader{

	private char alarmCode = 0;
	private static final int FLD_COMMENT_TEXT  = 3;
	private static final char[] alarmArrayCode = {	0,
												'A',
												'Q',
												'V',
												'T',
												'Z',
												'P',
												'I',
												'J',
												'K',
												'W',
												'F',
												'H',
												'U',
												'S',
												'Y',
												'B',
												'G',
												'N',
												'L',
												'E',
												'R',
												'D',
												'&',
												'C',
												'M',
												'$',
												'$',
												'@',
												'#',
												'#',
												'#',
												'#',
												'#',
												'#',
												'+',
												'+',
												'%',
												'O',
												'X',
												0,
												0,
												'*',
												'!',
												'=',
												'=',
												'>',
												0,
												0,
												0,
												0,
												':',
												';',
												'/',
												0,
												0,
												0,
												0,
												0,
												0,
												0,
												0,
												'g',
												'a',
												'b',
												'p',
												'q',
												'h',
												'k',
												'm',
												'f',
												'd',
												'e',
												0,
												'y',
												't',
												's',
												'c',
												0,
												0,
												0,
												0,
												0,
												0,
												0,
												0,
												'u',
												'n',
												0,
												0,
												0,
												0,
												0,
												'z',
												']',
												'[',
												'[',
												'}',
												'v',
												'w',
												'<',
												'$',//Should be the yen symbol
												0,
												0,
												0,
												0,
												0  };
	
	public CommentResultReader(){
		super('|', '^');
	}

	public void readToken(String token, int fieldPos, int compPos) {
		com.foc.Globals.logDetail(" fieldPos:"+fieldPos+" compPos:"+compPos+" token:"+token);

		if(fieldPos == FLD_COMMENT_TEXT){
			try{
				int alarmIndex = Integer.parseInt(token.trim());
				if (alarmIndex >= 0 && alarmIndex < alarmArrayCode.length) {
					alarmCode = alarmArrayCode[alarmIndex];
				}
			}catch(NumberFormatException e){
				// Comment field contains text rather than a numeric alarm code index — ignore
			}
		}
	}
	
	public char getCommentOnResult() {
		return alarmCode;
	}

}
