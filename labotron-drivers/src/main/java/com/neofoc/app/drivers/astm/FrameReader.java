package com.neofoc.app.drivers.astm;

import com.foc.Globals;

import java.util.StringTokenizer;

public abstract class FrameReader {

	public abstract void readToken(String token, int fieldPos, int compPos);// This function can be called from the
																			// readLine function

	private char fieldSeparator[] = new char[2];
	private String tokenDelimiters = null;

	public FrameReader(char firstDelimiter, char secondDelimiter) {
		this.fieldSeparator[0] = firstDelimiter;
		this.fieldSeparator[1] = secondDelimiter;
	}

	public void dispose() {
	}

	private String getTokenDelimiters() {
		if (tokenDelimiters == null) {
			tokenDelimiters = String.valueOf(fieldSeparator[0]) + String.valueOf(fieldSeparator[1]);
		}
		return tokenDelimiters;
	}

	public void scanTokens(StringBuffer buff) {
		
		Globals.logString("Scanning tokens");
		
		StringTokenizer tok = new StringTokenizer(buff.toString(), getTokenDelimiters(), true);
		int fieldpos = 0;
		int componentpos = 0;

		while (tok.hasMoreTokens()) {
			String str = tok.nextToken();
			if (str.compareTo(String.valueOf("|")) == 0) {
				fieldpos++;
				componentpos = 0;
			} else if (str.compareTo(String.valueOf("^")) == 0) {
				componentpos++;
			} else {
				readToken(str, fieldpos, componentpos);
			}
		}
	}
}