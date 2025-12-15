package com.neofoc.app.drivers.cobasu601701;

import com.foc.util.ASCII;
import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.ResultLineReader;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;

public class CobasU601701_ResultLineReader extends ResultLineReader {

    private int posForTestCode = 0;

    private String machineTestCode = null;

    public CobasU601701_ResultLineReader(AstmDriver driver, int posForTestCode) {
        super(driver);
        this.posForTestCode = posForTestCode;
    }

    public String getMachineTestCode() {
        return machineTestCode;
    }

    public void setMachineTestCode(String machineTestCode) {
        this.machineTestCode = machineTestCode;
    }

    public void readToken(String token, int fieldPos, int compPos) {
		/*
701 results		
|1^RBC|^<1.14 /HPF^<5.00 /uL^<1.14 /HPF^<5.00 /uL|
|3^WBC|^1.80 /HPF^7.92 /uL^1.80 /HPF^7.92 /uL|
|6^SEC|++^8 /HPF^40 /uL^~12.70 /HPF^~55.88 /uL|
|5^NEC|Absent^Absent^Absent^~0.20 /HPF^~0.88 /uL|
|21^MUC|Positive^Positive^Positive^>22.73 /HPF^>100.00 /uL|
|15^HYA|Absent^Absent^Absent^~0.20 /HPF^~0.88 /uL|
|8^CRY|Positive^Positive^Positive^>1.14 /HPF^>5.00 /uL|
|7^YEA|Absent^Absent^Absent^~0.10 /HPF^~0.44 /uL|
|19^SPRM|Absent^Absent^Absent^~0.00 /HPF^~0.00 /uL|
|14^BAC|+^30 /HPF^150 /uL^~21.40 /HPF^~94.16 /uL|
		 
		fieldPos:1 compPos:0 token:4
		fieldPos:2 compPos:0 token:4
		fieldPos:2 compPos:1 token:KET
		fieldPos:3 compPos:0 token:+++
		fieldPos:3 compPos:1 token:50 mg/dL
		fieldPos:3 compPos:2 token:+++ mmol/L
		fieldPos:5 compPos:0 token:St. Georges Hospital
		fieldPos:8 compPos:0 token:F
		fieldPos:10 compPos:0 token:parasito
		fieldPos:13 compPos:0 token:u60

20:44:55:621 :  fieldPos:2 compPos:0 token:1
20:44:55:621 :  fieldPos:2 compPos:1 token:ERY
20:44:55:621 :  fieldPos:3 compPos:0 token:++++
20:44:55:622 :  fieldPos:3 compPos:1 token:150 /uL
20:44:55:622 :  fieldPos:3 compPos:2 token:>250 /uL
20:44:55:622 :  fieldPos:5 compPos:0 token:St. Georges Hospital
20:44:55:622 :  fieldPos:8 compPos:0 token:F
20:44:55:622 :  fieldPos:10 compPos:0 token:parasito
20:44:55:622 :  fieldPos:13 compPos:0 token:u60
		*/
        com.foc.Globals.logDetail(" fieldPos:" + fieldPos + " compPos:" + compPos + " token:" + token);

        if (fieldPos == 2 && compPos == posForTestCode) {
            setMachineTestCode(token);
            String lisCode = getDriver().testMaps_getLisCode(token);
            setDoRead(lisCode != null);
            if (isDoRead()) {
                if (!getDriver().getAstmParams().isCheckResultFrameTestCodeWithOrderFrameTestCode()) {
                    String testLabelToFind = lisCode.trim();
                    FocLabTest test = getSample().findTest(testLabelToFind);
                    if (test == null) {
                        test = getSample().addTest();
                    }
                    test.setLabel(lisCode.trim());
                    setTest(test);
                } else {
                    setDoRead(lisCode.trim().compareTo(getTest().getLabel()) == 0);
                }
            }
        } else if (fieldPos == FLD_DATA_VALUE) {
            if (isDoRead()) {

                if (compPos == 0) {
                    //If both columns are equal we only need to put the column 1
                    //We will try to see if is a numerical value
                    //----------------------------------------------------------
                    Double dbl = null;
                    try {
                        dbl = Double.valueOf(token);
                        double value = dbl.doubleValue();
                        getTest().setValue(value);
                        getTest().setResultOk(true);
                    } catch (Exception e) {
                        getTest().setValue(FocLabTest.VALUE_NULL);
                        getTest().setValueNotes(token);
                        getTest().setResultOk(true);
                    }
                    //----------------------------------------------------------

                } else if (compPos == 2) {
                    String unit = "";
                    int spaceIndex = token.indexOf(' ');
                    if (spaceIndex > 0) {
                        unit = token.substring(spaceIndex + 1, token.length());
                        token = token.substring(0, spaceIndex) + " ";
                        if (unit.contains("uL")) {
                            String mm3 = "mm" + ASCII.CUBE;
                            unit = unit.replace("uL", mm3);
                        }
                    }
                    String machineCode = getMachineTestCode();
                    if (machineCode != null) {
                        if (machineCode.equals("ERY") && (unit == null || unit.trim().compareTo("") == 0)) {//1-ERY-601
                            unit = " /mm" + ASCII.CUBE;
                        } else if (machineCode.equals("LEU") && (unit == null || unit.trim().compareTo("") == 0)) {//2-LEU-601
                            unit = " /mm" + ASCII.CUBE;
                        } else if (machineCode.equals("NIT")//3-NIT-601
                                || machineCode.equals("KET")//4-KET-601
                                || machineCode.equals("GLU")//5-GLU-601
                                || machineCode.equals("PRO")//6-PRO-601
                                || machineCode.equals("UBG")//7-UBG-601
                                || machineCode.equals("BIL")//8-BIL-601
                                //701
                                || machineCode.equals("SEC")
                                || machineCode.equals("NEC")
                                || machineCode.equals("MUC")
                                || machineCode.equals("HYA")
                                || machineCode.equals("CRY")
                                || machineCode.equals("YEA")
                                || machineCode.equals("SPRM")
                                || machineCode.equals("BAC")) {
                            unit = "";
                        }
                    }
                    getTest().setUnitLabel(unit);

                    if (machineCode.equals("WBC") || machineCode.equals("RBC")) {
                        String numValueText = token.trim();

                        getTest().setAlarm(FocLabTest.TEST_RESULT_EMPTY_ALARM);
                        if (numValueText.charAt(0) == '>' || numValueText.charAt(0) == '<') {
                            if (numValueText.charAt(0) == '>') {
                                getTest().setAlarm(FocLabTest.TEST_RESULT_GREATER_THAN);
                            } else if (numValueText.charAt(0) == '<') {
                                getTest().setAlarm(FocLabTest.TEST_RESULT_LESS_THAN);
                            }
                            numValueText = numValueText.substring(1);
                            int indexOfDot = numValueText.indexOf(".");
                            if (indexOfDot > 0) {
                                numValueText = numValueText.substring(0, indexOfDot);
                            }
                        }

                        try {
                            double numValue = Double.valueOf(numValueText);
                            numValue = Math.floor(numValue);
                            numValueText = String.valueOf(numValue);
                            getTest().setValue(numValue);
                            getTest().setResultOk(true);
                            token = "";
                        } catch (Exception e) {
                            getTest().setValue(FocLabTest.VALUE_NULL);
                            getTest().setValueNotes(token);
                            getTest().setResultOk(true);
                        }
                    }

                    if (unit != null
                            && !unit.isEmpty()
                            && getTest().getValueNotes() != null
                            && !getTest().getValueNotes().isEmpty()
                            && !getTest().getValueNotes().contains(token)) {
                        getTest().setValueNotes(getTest().getValueNotes() + "      (" + token + unit + ")");//The token has the last space
                        getTest().setResultOk(true);
                    }
                }
            }
        }
    }
}
