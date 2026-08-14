package com.neofoc.app.drivers.vitekbci;

import com.foc.util.ASCII;
import com.neofoc.app.drivers.astm.FrameReader;

import java.util.StringTokenizer;

//Patient line reader
public class VitekResultLineReader extends FrameReader {

    private String sampleId = "";
    private String firstName = "";
    private String lastName = "";
    private String testLabel = "";
    private String resultComment = "";
    private String resultValue = "";
    private String resultUnit = "";
    private boolean resultReady = false;

    public VitekResultLineReader() {
        super('|', '^');
    }

    public String getResultComment() {
        return resultComment;
    }

    public String getResultValue() {
        return resultValue;
    }

    public String getResultUnit() {
        return resultUnit;
    }

    public String getSampleID() {
        return sampleId != null ? sampleId : "";
    }

    public void reset() {
        sampleId = null;
        resetResult();
    }

    public void resetResult() {
        testLabel = null;
        resultUnit = null;
        resultValue = null;
        resultComment = null;
    }

    public boolean hasAResult() {
        return resultUnit != null && testLabel != null;
    }

    /*
        <RS>mtrsl|pi123456|pndupont|pb01/01/2013|psM|soC|si|ci001122334455|rtDD2|rnD-Dimer E
        <RS>xclusion|tt17:26|td08/01/2013|ql|qn16830.25 ng/ml|y3ng/ml|qd4|ncvalid|idVIDAS3PC01
    <RS>|snVN00000|m4vidas|
     */
    public void readToken(String token, int fieldPos, int compPos) {
        com.foc.Globals.logDetail(" token:" + token);
        if (token.startsWith("ci")) {
            if (token.length() > 2) sampleId = token.substring(2);
            else sampleId = "";
        } else if (token.startsWith("pn")) {
            if (token.length() > 2) {
                String fullName = token.substring(2);
                int comaIdx = fullName.indexOf(",");
                firstName = fullName.substring(0, comaIdx);
                lastName = fullName.substring(comaIdx + 1);
            } else {
                firstName = "";
                lastName = "";
            }
        } else if (token.startsWith("a1")) {
            if (token.length() > 2) testLabel = token.substring(2);
            else testLabel = "";
        } else if (token.startsWith("a3")) {
            if (token.length() > 2) resultValue = token.substring(2);
            else resultValue = "";
        } else if (token.startsWith("a4")) {
            if (token.length() > 2) resultComment = token.substring(2);
            else resultComment = "";
            resultReady = true;
//		} else if(token.startsWith("y3")) {
//			if(token.length()> 2) resultUnit = token.substring(2);
//			else resultUnit = "";
        }
    }

    @Override
    public void scanTokens(StringBuffer buff) {

    }

    public void scanTokens(VitekBCIReceiver receiver, StringBuffer buff) {
        String delimeters = "|" + String.valueOf(ASCII.RS);
        StringTokenizer tok = new StringTokenizer(buff.toString(), delimeters, true);

        String lastToken = "";

        while (tok.hasMoreTokens()) {
            String str = tok.nextToken();
            if (str.compareTo(String.valueOf("|")) == 0) {
                readToken(lastToken, 0, 0);
                if (resultReady) {
                    receiver.addResult(this);
                    resultReady = false;
                    testLabel = "";
                    resultValue = "";
                    resultComment = "";
                }
            } else if (str.compareTo(String.valueOf(ASCII.RS)) == 0) {
                lastToken += str;
            } else {
                lastToken = str;
            }
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTestLabel() {
        return testLabel;
    }
}
