package com.neofoc.app.drivers.yumizenP8000;

import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.FrameReader;

//Patient line reader
public class OBXLineReader extends FrameReader {

    private AstmDriver driver = null;

    private String testCode = null;
    private String value = null;
    private String status = null;
    private String unitLabel = null;

    private String valueType = null;

    public OBXLineReader(AstmDriver driver) {
        super('|', '^');
        this.driver = driver;
    }

    @Override
    public void dispose() {
        super.dispose();
        driver = null;
    }

    public void reset() {
        testCode = null;
        value = null;
        status = null;
        unitLabel = null;

        valueType = null;
    }

    public boolean isImage() {
        return valueType != null && valueType.equals("ED");
    }

    /*
        OBX|1|NM|RDW-SD^RDW-SD||45.0|fl|||||F|||20160705100630||||Yumizen H2500-SPS
     */
    public void readToken(String token, int fieldPos, int compPos) {
        com.foc.Globals.logDetail(" fieldPos:" + fieldPos + " compPos:" + compPos + " token:" + token);

        if (fieldPos == 2 && compPos == 0) {
            valueType = new String(token);//To check if IMAGE
        }

        if (fieldPos == 11 && compPos == 0) status = new String(token);
        if (fieldPos == 3 && compPos == 0) {
            if (isImage()) {
                testCode = "";
            } else {
                testCode = new String(token);
                if (driver != null) {
                    testCode = driver.testMaps_getLisCode(testCode);
                }
            }
        }
        if (fieldPos == 5 && compPos == 0) value = new String(token);
        if (fieldPos == 6 && compPos == 0) unitLabel = new String(token);
    }

    public String getTestCode() {
        return testCode;
    }

    public String getValue() {
        return value;
    }

    public String getStatus() {
        return status;
    }

    public String getUnitLabel() {
        return unitLabel;
    }

}
