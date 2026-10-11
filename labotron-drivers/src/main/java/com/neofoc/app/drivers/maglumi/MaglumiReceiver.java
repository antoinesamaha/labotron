package com.neofoc.app.drivers.maglumi;

import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.drivers.astm.AstmReceiver;

/**
 * Per the Maglumi specification (B.6) the LIS must also ACK the EOT,
 * otherwise the Maglumi considers the LIS disconnected. The base receiver does not answer EOT.
 */
public class MaglumiReceiver extends AstmReceiver {

    public MaglumiReceiver(AstmDriver driver) {
        super(driver);
    }

    @Override
    protected int concatenateResultFrame(AstmFrame frame) {
        StringBuffer dataWithFrame = frame.getDataWithFrame();
        if (dataWithFrame != null && dataWithFrame.length() == 1 && dataWithFrame.charAt(0) == AstmFrame.EOT) {
            // ACK first: parsing and dispatching the results can take longer than the Maglumi waits
            try {
                driver.send(String.valueOf(AstmFrame.ACK));
            } catch (Exception e) {
                driver.getInstrument().logException(e);
            }
        }
        return super.concatenateResultFrame(frame);
    }
}
