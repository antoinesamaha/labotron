package com.neofoc.app.impl;

import com.neofoc.app.SimSocket;

public class ABL9Simulator extends AbstractSimulator {

    String[] ENQUIRY_FRAMES = {
            "Q=Q|1|PID201910236^ACN2233445609||||||||||D"
    };

    //[SOH]
    String[] RESULT_FRAMES = {
    "H|\\^&|||ABL9^402843|||||NC2L||1|20260627193917",
    "P|1||201910236||Ahmad Baydoun^Khadijeh||19380804|F|||||87|years",
    "O|1||Sample #^28258|||||||ANONYMOUS|||||Arterial^|||||||||F",
    "R|1|^^^T|37.0|Cel|||F||ANONYMOUS|20260627082914|20260627082914",
    "R|2|^^^FIO2^I|28|%||N||F",
    "R|3|^^^Age^I|87|years||N||F",
    "R|4|^^^pH^M|7.53||N||F",
    "R|5|^^^pCO2^M|30.1|mmHg||N||F",
    "R|6|^^^Hct^M|15|%||N||F",
    "R|7|^^^pO2^M|149|mmHg||N||F",
    "R|8|^^^K+^M|3.59|mmol/L||N||F",
    "R|9|^^^Na+^M|141|mmol/L||N||F",
    "R|10|^^^Ca++^M|1.09|mmol/L||N||F",
    "R|11|^^^Cl-^M|115|mmol/L||N||F",
    "R|12|^^^pH(T)^C|7.53||N||F",
    "R|13|^^^pCO2(T)^C|30.1|mmHg||N||F",
    "R|14|^^^HCO3-^C|24.9|mmol/L||N||F",
    "R|15|^^^ABE^C|1.6|mmol/L||N||F",
    "R|16|^^^SBE^C|2.2|mmol/L||N||F",
    "R|17|^^^SBC^C|25.9|mmol/L||N||F",
    "R|18|^^^tCO2(P)^C|25.9|mmol/L||N||F",
    "R|19|^^^tCO2(B)^C|24.5|mmol/L||N||F",
    "R|20|^^^tHb^C|4.8|g/dL||N||F",
    "R|21|^^^sO2^C|99.3|%||N||F",
    "R|22|^^^pO2(T)^C|149|mmHg||N||F",
    "R|23|^^^pO2(A)^C|.....|mmHg||N||F",
    "C|1|I|1009^1009: Unable to calculate - missing Baro|I",
    "R|24|^^^pO2(A),T^C|.....|mmHg||N||F",
    "C|1|I|1009^1009: Unable to calculate - missing Baro|I",
    "R|25|^^^AaDpO2^C|.....|mmHg||N||F",
    "C|1|I|1009^1009: Unable to calculate - missing Baro|I",
    "R|26|^^^AaDpO2,T^C|.....|mmHg||N||F",
    "C|1|I|1009^1009: Unable to calculate - missing Baro|I",
    "R|27|^^^a/ApO2^C|.....|%||N||F",
    "C|1|I|1009^1009: Unable to calculate - missing Baro|I",
    "R|28|^^^a/ApO2,T^C|.....|%||N||F",
    "C|1|I|1009^1009: Unable to calculate - missing Baro|I",
    "R|29|^^^tO2^C|3.1|mmol/L||N||F",
    "R|30|^^^RI^C|.....|%||N||F",
    "C|1|I|1009^1009: Unable to calculate - missing Baro|I",
    "R|31|^^^RI,T^C|.....|%||N||F",
    "C|1|I|1009^1009: Unable to calculate - missing Baro|I",
    "R|32|^^^Ca(7.4)^C|1.17|mmol/L||N||F",
    "R|33|^^^Anion gap (K+)^C|4.9|mmol/L||N||F",
    "R|34|^^^Anion gap^C|1.3|mmol/L||N||F",
    "R|35|^^^cH+^C|29.7|nmol/L||N||F",
    "R|36|^^^cH+(T)^C|29.7|nmol/L||N||F",
    "R|37|^^^pO2(a)/FIO2^C|532|mmHg||N||F",
    "R|38|^^^pO2(a,T)/FIO2^C|532|mmHg||N||F",
    "R|39|^^^cBase(B,ox)^C|1.6|mmol/L||N||F",
    "R|40|^^^cBase(Ecf,ox)^C|2.2|mmol/L||N||F",
    "L|1|N"
    };
    //[EOT]

    public void simulate() {
        socket = new SimSocket(this, 10020);
        socket.open();
        sendFrames(ENQUIRY_FRAMES);
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        socket.close();
    }

    public void sendFrames(String[] frames) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(SOH);

        for (String frame : frames) {
            buffer.append(frame);
            buffer.append(CR);
        }

        buffer.append(EOT);

        socket.send(buffer.toString());
    }

}
