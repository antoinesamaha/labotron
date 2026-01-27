package com.neofoc.app.drivers.yumizenP8000;

import com.foc.Globals;
//import b01.l3.Instrument;
//import b01.l3.data.L3Message;
//import b01.l3.data.L3Sample;
//import b01.l3.data.L3Test;
import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.drivers.astm.AstmFrameCreator;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;
import com.neofoc.app.modules.labotron.focObjects.L3Message;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class YumizenP8000FrameCreator extends AstmFrameCreator {

    public final static String YUMI_FRAME_HEADER = "MSH";
	
	/*
	 * 
CBC
DIF
RET
CBR
DIR
CBE
SLIDE
	 */

    public String getProfileForTestID(String testLabel) {
        String profile = "CBC";

//	  if(testLabel.startsWith("ERB")){
//		  profile = "ERB";
//	  }else 
        if (testLabel.startsWith("RET")
                || testLabel.equals("MFI")
                || testLabel.equals("IRF")
                || testLabel.equals("CRC")
                || testLabel.equals("MRV")
                || testLabel.equals("PIC")
        ) {
            profile = "RET";
        } else if (testLabel.contains("%") || testLabel.contains("#")) {
            profile = "DIF";
        }
        return profile;
    }

    /*
    MSH|^~\&|LIS|LIS|YP8K|YP8K|20160416090430||OML^O33^OML_O33|18698910009|P|
    2.5|||||||
    */
    public YumizenP8000Frame newHeaderFrame(FocInstrument instrument) {
        YumizenP8000Frame frame = new YumizenP8000Frame(instrument, AstmFrame.FRAME_TYPE_HEADER);

        frame.append2Data("MSH");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(AstmFrame.REPEAT_SUB_FIELD_DELIMITER);
        frame.append2Data(AstmFrame.REPEAT_DELIMITER);
        frame.append2Data(AstmFrame.ESCAPE_DELIMITER);

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("LIS");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("LIS");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("P8000");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("P8000");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);

        Date date = new Date(System.currentTimeMillis() + Calendar.getInstance().get(Calendar.DST_OFFSET));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        frame.append2Data(sdf.format(date));

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("OML");
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data("O33");
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data("OML_O33");

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);

        SimpleDateFormat dateFormat = new SimpleDateFormat("ddHHmmssSSS");
        frame.append2Data(dateFormat.format(date));

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("P");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("2.5");

        for (int i = 0; i < 8; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }

        return frame;
    }

    /*
    PID|||P0002^^^LIS^PI||DOE^JOHN^^||19601206|M|||Main
    Street^^Springfield^NY^65466^USA^ATC1||0033412364567|||||ABC123^^LIS|||||||||||||N|
    AL
    */
    public YumizenP8000Frame newPatientFrame(FocInstrument instrument, int sequence_num, String sampleId, String patientId, String firstName, String lastName, String middleName, LocalDate dob, int age, String sex) {
        YumizenP8000Frame frame = new YumizenP8000Frame(instrument, AstmFrame.FRAME_TYPE_PATIENT);
        frame.append2Data("PID");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);

        if (patientId == null) patientId = "";
        frame.append2Data(patientId);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data("LIS");
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data("PI");

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);

        frame.append2Data(lastName);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(firstName);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(middleName);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        frame.append2Data(sdf.format(dob));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);

        //Sex Field
        if (sex != null) {
            frame.append2Data(sex);
        }
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);

        //address
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);

        for (int i = 0; i < 20; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }
        frame.append2Data("N");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("AL");

        return frame;
    }

    /*
    PV1||N|EMERGENCY^ROOM1^BED1||||ATD^DR HOUSE||||||||||ADD^DR WILSON||
    ABC123^LIS|||||||||||||||||||||||||20160416090430|20160416090430


    PV1||N|WARD00001^^||||ATD^||||||||||ADD^||ABC123^LIS|||||||||||||||||||||||||20190703100011|20190703100011<CR>
    */
    public YumizenP8000Frame newVisitFrame(FocInstrument instrument, String sampleId, LocalDateTime entryDate) {
        YumizenP8000Frame frame = new YumizenP8000Frame(instrument, AstmFrame.FRAME_TYPE_PATIENT);
        frame.append2Data("PV1");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR, 2);
        frame.append2Data("N");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("WARD00001");
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER, 2);//EMERGENCY^ROOM1^BED1
        frame.append2Data(AstmFrame.FIELD_SEPERATOR, 4);
        frame.append2Data("ATD");
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);//ATD^DR HOUSE
        frame.append2Data(AstmFrame.FIELD_SEPERATOR, 10);
        frame.append2Data("ATD");
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);//ATD^DR WILSON
        frame.append2Data(AstmFrame.FIELD_SEPERATOR, 2);
        frame.append2Data("ABC123");
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data("LIS");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR, 25);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);//The 2 dates reception and discharge
        return frame;
    }

    /*
     SPM|1|201604163002||EDTA||||MAIN LAB|||||||||201604160904|201604160904|||||
     */
    public YumizenP8000Frame newSampleFrame(FocInstrument instrument, String sampleId, LocalDateTime entryDate) {
        YumizenP8000Frame frame = new YumizenP8000Frame(instrument, AstmFrame.FRAME_TYPE_PATIENT);
        frame.append2Data("SPM");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("1");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(sampleId);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("EDTA");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("MAIN LAB");
        for (int i = 0; i < 9; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        frame.append2Data(sdf.format(entryDate));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(sdf.format(entryDate));
        for (int i = 0; i < 5; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }
        return frame;
    }

    /*
    ORC|NW|L604163002|L604163002|L604163002|||||20160416090430||||||||||||
    */
    public YumizenP8000Frame newCommonOrderFrame(FocInstrument instrument, String sampleId, LocalDateTime entryDate) {
        YumizenP8000Frame frame = new YumizenP8000Frame(instrument, AstmFrame.FRAME_TYPE_PATIENT);
        frame.append2Data("ORC");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("NW");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(sampleId);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(sampleId);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(sampleId);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR, 5);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        Date date = new Date(System.currentTimeMillis() + Calendar.getInstance().get(Calendar.DST_OFFSET));
        frame.append2Data(sdf.format(date));

        frame.append2Data(AstmFrame.FIELD_SEPERATOR, 12);

        return frame;
    }

    /*
    TQ1|||||||20160416090430||S
     */
    public YumizenP8000Frame newTimingQuantityFrame(FocInstrument instrument, String sampleId, LocalDateTime entryDate, String urgent) {
        YumizenP8000Frame frame = new YumizenP8000Frame(instrument, AstmFrame.FRAME_TYPE_PATIENT);
        frame.append2Data("TQ1");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR, 7);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        Date date = new Date(System.currentTimeMillis() + Calendar.getInstance().get(Calendar.DST_OFFSET));
        frame.append2Data(sdf.format(date));

        frame.append2Data(AstmFrame.FIELD_SEPERATOR, 2);

        frame.append2Data(urgent);

        return frame;
    }

    /*
    OBR|1|L604163002|L604163002|CBC^CBC profile^YP8K||||||||||||DR HOUSE|||||||||P
    */
    public YumizenP8000Frame newObservationRequestFrame(FocInstrument instrument, String sampleId, String profile) {
        YumizenP8000Frame frame = new YumizenP8000Frame(instrument, AstmFrame.FRAME_TYPE_PATIENT);
        frame.append2Data("OBR");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("1");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(sampleId);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(sampleId);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);

        frame.append2Data(profile);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(profile + " profile");
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data("P8000");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR, 7);//21
        frame.append2Data("EDTA");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR, 14);
        frame.append2Data("P");
        return frame;
    }

    /*
  <VT>
  MSH|^~\&|LIS|LIS|YP8K|YP8K|20160416090430||OML^O33^OML_O33|18698910009|P|2.5|||||||
  <CR>
  PID|||P0002^^^LIS^PI||DOE^JOHN^^||19601206|M|||Main
  Street^^Springfield^NY^65466^USA^ATC1||0033412364567|||||ABC123^^LIS|||||||||||||N|AL<CR>
  PV1||N|EMERGENCY^ROOM1^BED1||||ATD^DR HOUSE||||||||||ADD^DR WILSON||
  ABC123^LIS|||||||||||||||||||||||||20160416090430|20160416090430<CR>
  SPM|1|201604163002||EDTA||||MAIN LAB|||||||||201604160904|201604160904|||||<CR>

  ORC|NW|L604163002|L604163002|L604163002|||||20160416090430||||||||||||<CR>
  TQ1|||||||20160416090430||S<CR>
  OBR|1|L604163002|L604163002|CBC^CBC profile^YP8K||||||||||||DR HOUSE|||||||||P<CR>

  OBX|1|CE|CLL||Clinical comment.||||||F|||20160728150751||<CR>

  ORC|NW|L604163002|L604163002|L604163002|||||20160416090430||||||||||||<CR>
  TQ1|||||||20160416090430||S<CR>
  OBR|1|L604163002|L604163002|DIF^DIF profile^YP8K||||||||||||DR HOUSE|||||||||P<CR>
  <FS>
  <CR>
     */
    public void buildFrameArray(AstmDriver driver, L3Message message, boolean fromDriver) throws Exception {
        // Header frame
        YumizenP8000Frame frame = newHeaderFrame(driver.getInstrument());
        driver.addFrame(frame);
        // -------------

        // Patient Frame
        int sampleSequence = 1;

        Iterator<FocLabSample> sIter = message.sampleIterator();
        while (sIter != null && sIter.hasNext()) {
            FocLabSample sam = sIter.next();
            if (sam != null) {
                frame = newPatientFrame(driver.getInstrument(), sampleSequence, sam.getSampleId(), sam.getPatientId(), sam.getFirstName(), sam.getLastName(), sam.getMiddleName(), sam.getDateOfBirth(), sam.getAge(), sam.getSex());
                driver.addFrame(frame);

                frame = newVisitFrame(driver.getInstrument(), sam.getSampleId(), sam.getEntryDateTime());
                driver.addFrame(frame);

                frame = newSampleFrame(driver.getInstrument(), sam.getSampleId(), sam.getEntryDateTime());
                driver.addFrame(frame);

                //Scan the tests and gather the equivalent Profiles in an Array
                ArrayList<String> profilesArray = new ArrayList<String>();
                boolean urgent = false;
                Iterator tIter = sam.testIterator();
                while (tIter != null && tIter.hasNext()) {
                    FocLabTest test = (FocLabTest) tIter.next();
                    if (test != null) {
                        urgent = urgent || test.getPriority().equals("S");
                        String instrCode = driver.testMaps_getInstCode(test.getLabel());
                        if (instrCode == null) {
                            Globals.logString("Could not find Instrument code for : " + test.getLabel());
                            instrCode = test.getLabel();
                        }
                        String profile = getProfileForTestID(instrCode);
                        if (!profilesArray.contains(profile)) {
                            profilesArray.add(profile);
                        }
                    }
                }

                for (int i = 0; i < profilesArray.size(); i++) {
                    String profile = profilesArray.get(i);

                    frame = newCommonOrderFrame(driver.getInstrument(), sam.getSampleId(), sam.getEntryDateTime());
                    driver.addFrame(frame);

                    frame = newTimingQuantityFrame(driver.getInstrument(), sam.getSampleId(), sam.getEntryDateTime(), urgent ? "S" : "R");
                    driver.addFrame(frame);

                    frame = newObservationRequestFrame(driver.getInstrument(), sam.getSampleId(), profile);
                    driver.addFrame(frame);
                }
            }
        }
    }
}
