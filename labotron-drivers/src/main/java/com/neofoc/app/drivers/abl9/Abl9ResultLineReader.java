package com.neofoc.app.drivers.abl9;

import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.ResultLineReader;

public class Abl9ResultLineReader extends ResultLineReader {

    // R|1|^^^T|37.0|Cel|||F||ANONYMOUS|20260627082914|20260627082914|

    public Abl9ResultLineReader(AstmDriver driver){
        super(driver);
    }

}
