package com.neofoc.app.drivers.astm;

import com.foc.Globals;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;

public class ResultLineReader extends FrameReader {
    private AstmDriver driver = null;
    private FocLabSample sample = null;
    private FocLabTest test = null;
    private char resultType = 'P';
    private boolean doRead = true;

    private int FLD_SEQUENCE_NUMBER = 1;
    private int FLD_UNIVERSAL_TEST_ID = 2;
    protected int FLD_DATA_VALUE = 3;
    private int FLD_UNITS = 4;
    protected int FLD_ALARM_CODE = 6;

    //FLD_UNIVERSAL_TEST_ID
    private int CMP_ASSAY_NUMBER = 3;
    //private static final int CMP_RESULT_TYPE        = 7;

    public ResultLineReader(AstmDriver driver) {
        super('|', '^');
        this.driver = driver;
    }

    public void dispose() {
        driver = null;
        test = null;
        sample = null;
        super.dispose();
    }

    public AstmDriver getDriver() {
        return driver;
    }

    public FocLabSample getSample() {
        return sample;
    }

    public void setSample(FocLabSample sample) {
        this.sample = sample;
        doRead = true;
    }

    public void setTest(FocLabTest test) {
        this.test = test;
        doRead = true;
    }

    public FocLabTest getTest() {
        return test;
    }

    public boolean isDoRead() {
        return doRead;
    }

    public void setDoRead(boolean doRead) {
        this.doRead = doRead;
    }

    public void readToken(String token, int fieldPos, int compPos) {
        com.foc.Globals.logDetail(" fieldPos:" + fieldPos + " compPos:" + compPos + " token:" + token);

        if (fieldPos == FLD_UNIVERSAL_TEST_ID && compPos == driver.getAstmParams().getResultFrame_ComponentPositionForResultType()) {
            resultType = token.charAt(0);
            doRead = doRead && resultType == 'F';
        } else if (fieldPos == FLD_UNIVERSAL_TEST_ID && compPos == CMP_ASSAY_NUMBER) {
            if (doRead) {
// WE STOPED READING THE TEST CODE AT THE FIRST SLASH, BECAUSE ALEGRIA HAS TEST RNP/Sm
// IF LATER WE WANT TO ADD THIS SECTION AGAIN WE NEED TO MAKE EXCEPTION OF ALEGRIA
// -----------------------------------------------------------------------------------
//				if(token.length() > 0){
//					int indexOfSlash = token.indexOf('/');
//					if(indexOfSlash > 0){
//						token = token.substring(0, indexOfSlash);
//					}
//				}
// -----------------------------------------------------------------------------------

                if (driver.getAstmParams().isIgnoreLastTestCodeDigit()) {
                    int codeLength = token.length();
                    token = token.substring(0, codeLength - 1);
                    token = token.concat("0");
                }

                String lisCode = driver.testMaps_getLisCode(token);
                doRead = lisCode != null;
                if (doRead) {
                    if (!driver.getAstmParams().isCheckResultFrameTestCodeWithOrderFrameTestCode()) {
                        String testLabelToFind = lisCode.trim();
                        test = sample.findTest(testLabelToFind);
                        if (test == null) {
                            test = sample.addTest();
                        }
                        test.setLabel(lisCode.trim());
                    } else {
                        doRead = lisCode.trim().compareTo(test.getLabel()) == 0;
                    }
                }
                Globals.logDetail(" ASTM result list read -> token = " + token + " lisCode = " + lisCode + " doRead = " + doRead);
            }
        } else if (fieldPos == FLD_DATA_VALUE) {
            if (doRead) {
                Double dbl = null;
                try {
                    test.setAlarm(FocLabTest.TEST_RESULT_EMPTY_ALARM);
                    int indexOfOperator = token.indexOf('>');
                    if (indexOfOperator < 0) {
                        indexOfOperator = token.indexOf('<');
                    }
                    if (indexOfOperator >= 0) {// token.charAt(0) == '>' || token.charAt(0) == '<'){
                        if (!driver.getAstmParams().doTreatHigherLessAlarmSeparately()) {    //modular
                            test.setNotificationMessage("" + token.charAt(0));
                        } else if (token.charAt(indexOfOperator) == '>') {
                            test.setAlarm(FocLabTest.TEST_RESULT_GREATER_THAN);
                        } else if (token.charAt(indexOfOperator) == '<') {
                            test.setAlarm(FocLabTest.TEST_RESULT_LESS_THAN);
                        }
                        token = token.substring(indexOfOperator + 1);
                    }
                    dbl = Double.valueOf(token);
                    test.setResultOk(true);
                    test.setValue(dbl.doubleValue());
                } catch (Exception e) {
                    test.setResultOk(false);
                    test.setValue(0);
                    String message = "Could not parse Double:" + token;
                    test.setNotificationMessage(message);
                    Globals.logString(" HANDLED EXCEPTION " + message);
                    Globals.logException(e);
                }
            }
        } else if (fieldPos == FLD_UNITS) {
            if (doRead) {
                test.setUnitLabel(token.toString());
            }
        }
    }
}