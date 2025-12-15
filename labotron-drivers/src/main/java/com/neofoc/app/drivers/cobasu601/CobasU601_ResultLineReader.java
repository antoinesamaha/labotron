package com.neofoc.app.drivers.cobasu601;

import com.foc.util.ASCII;
import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.ResultLineReader;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;

public class CobasU601_ResultLineReader extends ResultLineReader {

    private int posForTestCode = 0;

    private String machineTestCode = null;

    public CobasU601_ResultLineReader(AstmDriver driver, int posForTestCode) {
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
                        getTest().setValue(dbl.doubleValue());
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
                    //If the Unit is empty we need to set it hard coded
//					if(unit == null || unit.trim().compareTo("") == 0){
                    String machineCode = getMachineTestCode();
                    if (machineCode != null) {
                        if (machineCode.equals("1") && (unit == null || unit.trim().compareTo("") == 0)) {//1-ERY
                            unit = " /mm" + ASCII.CUBE;
                        } else if (machineCode.equals("2") && (unit == null || unit.trim().compareTo("") == 0)) {//2-LEU
                            unit = " /mm" + ASCII.CUBE;
                        } else if (machineCode.equals("3")//3-NIT
                                || machineCode.equals("4")//4-KET
                                || machineCode.equals("5")//5-GLU
                                || machineCode.equals("6")//6-PRO
                                || machineCode.equals("7")//7-UBG
                                || machineCode.equals("8"))//8-BIL
                        {
                            unit = "";
//							}else if(machineCode.equals("4")){//KET
//								//unit = "mmol/L";
//								unit = "";
//							}else if(machineCode.equals("5")){//GLU
//								//unit = "mmol/L";
//								unit = "";
//							}else if(machineCode.equals("6")){//PRO
//								//unit = "g/L";
//								unit = "";
//							}else if(machineCode.equals("7")){//UBG
//								//unit = "umol/L";
//								unit = "";
//							}else if(machineCode.equals("8")){//BIL
//								//unit = "umol/L";
//								unit = "";
                        }
                    }
//					}
                    //-------------------------------------------------
                    getTest().setUnitLabel(unit);

                    if (unit != null && !unit.isEmpty() && getTest().getValueNotes() != null && !getTest().getValueNotes().isEmpty() && !getTest().getValueNotes().contains(token)) {
                        getTest().setValueNotes(getTest().getValueNotes() + "      (" + token + unit + ")");//The token has the last space
                        getTest().setResultOk(true);
                    }
                }
            }
        }
    }
}
