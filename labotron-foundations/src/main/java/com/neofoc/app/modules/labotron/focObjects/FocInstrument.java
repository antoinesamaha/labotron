package com.neofoc.app.modules.labotron.focObjects;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.IExitListener;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.desc.FocObjectGeneral;
import com.foc.list.FocLinkForeignKey;
import com.foc.list.FocList;
import com.neofoc.app.connection.basicsocket.BServiceInterface;
import com.neofoc.app.driver.DriverFactory;
import com.neofoc.app.driver.IDriver;
import com.neofoc.app.driver.MessageListener;
import com.neofoc.app.exceptions.L3Exception;
import com.neofoc.app.modules.labotron.TestLabelMap;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import javax.comm.SerialPort;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

public class FocInstrument extends FocObjectGeneral implements Runnable, MessageListener,
        IExitListener {
    private IDriver driver = null;
    private Thread senderThread = null;
    private Properties properties = null;
    private boolean autoRefresh = false;
//    private L3SampleTestJoinFilte sampleTestFilterReadyToSend = null;
//    private L3SampleTestJoinFilter sampleTestFilterPendingTestsToBeResentWithNewTests = null;
//    private Application application = null;
    private FocList supportedTestList = null;

    private boolean logBufferDetails = false;

    private int DELAY_POLLING_FOR_DB_SAMPLES_TO_SEND = 10000;
    private int DELAY_FOR_SAMPLE_DISPAY_LIST_AUTOMATIC_REFRESH = 10000;
    private int DELAY_TO_TRY_AGAIN_THE_DRIVER_RESERVE = 2000;
    private int DELAY_TO_TRY_LATER = 2000;
    private int DELAY_DRIVER_TIME_OUT_FOR_RESPONSE = 30000;

    private void initFromProperties(Properties props) throws Exception {
        setCode(props.getProperty("instrument.code"));
        setName(props.getProperty("instrument.name"));
        setDriverClassName(props.getProperty("instrument.driver"));
    }

    public void resetDefaults() {
//        setPropertyMultiChoice(InstrumentDesc.FLD_MODE,
//                InstrumentDesc.INSTRUMENT_MODE_AUTOMATIC);
//        setPropertyInteger(
//                InstrumentDesc.FLD_DELAY_POLLING_FOR_DB_SAMPLES_TO_SEND,
//                DELAY_POLLING_FOR_DB_SAMPLES_TO_SEND);
//        setPropertyInteger(
//                InstrumentDesc.FLD_DELAY_FOR_SAMPLE_DISPAY_LIST_AUTOMATIC_REFRESH,
//                DELAY_FOR_SAMPLE_DISPAY_LIST_AUTOMATIC_REFRESH);
//        setPropertyInteger(
//                InstrumentDesc.FLD_DELAY_TO_TRY_AGAIN_THE_DRIVER_RESERVE,
//                DELAY_TO_TRY_AGAIN_THE_DRIVER_RESERVE);
//        setPropertyInteger(InstrumentDesc.FLD_DELAY_TO_TRY_LATER,
//                DELAY_TO_TRY_LATER);
//        setPropertyInteger(
//                InstrumentDesc.FLD_DELAY_DRIVER_TIME_OUT_FOR_RESPONSE,
//                DELAY_DRIVER_TIME_OUT_FOR_RESPONSE);
    }

    private void initialiseFocProperties() {
//        resetDefaults();
//        setPropertyMultiChoice(InstrumentDesc.FLD_SERIAL_BAUDE_RATE, 9600);
//        setPropertyMultiChoice(InstrumentDesc.FLD_SERIAL_PARITY,
//                SerialPort.PARITY_NONE);
//        setPropertyMultiChoice(InstrumentDesc.FLD_SERIAL_DATA_BITS,
//                SerialPort.DATABITS_8);
//        setPropertyMultiChoice(InstrumentDesc.FLD_SERIAL_STOP_BIT,
//                SerialPort.STOPBITS_1);
    }

    public FocInstrument(Properties props) throws Exception {
        this(new FocConstructor(Globals.getApp().getFocDescByName("Instrument")));
        newFocProperties();
        initialiseFocProperties();
        initFromProperties(props);
    }

    public FocInstrument(FocConstructor constr) {
        super(constr);
        newFocProperties();
        initialiseFocProperties();
        forceControler(true);
    }

    public FocInstrument(File file) throws Exception {
        this(new FocConstructor(Globals.getApp().getFocDescByName("Instrument")));
        Properties props = new Properties();
        FileInputStream in = new FileInputStream(file);
        props.load(in);
        in.close();

        initFromProperties(props);
    }

    public void dispose() {
        // try{
        // disconnect();
        // }catch(Exception e){
        // logException(e);
        // }

        if (driver != null) {
            driver.dispose();
            driver = null;
            properties = null;
        }
        senderThread = null;

        if (supportedTestList != null) {
            supportedTestList.dispose();
            supportedTestList = null;
        }

//        if (service != null) {
//            service.dispose();
//            service = null;
//        }
        super.dispose();
    }

    private boolean isResendAllPendingTests() {
        boolean resend = false;
        try {
            resend = (getDriver() != null) ? getDriver()
                    .isResendAllPendingTests() : false;
        } catch (Exception e) {
            Globals.logException(e);
        }
        return resend;
    }

    public boolean isOnHold() {
        return getPropertyBoolean("on_hold");
    }

    public void setAutoRefresh(boolean auto) {
        autoRefresh = auto;
    }

    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    public Properties getProperties() throws Exception {
        if (properties == null) {
            Properties tempProperties = new Properties();

//            if (getPropertiesFilePath() != null
//                    && getPropertiesFilePath().compareTo("") != 0) {
//                FileInputStream in = new FileInputStream(
//                        getPropertiesFilePath());
//                tempProperties.load(in);
//                in.close();
//            }
            tempProperties.put("serialPort.name",
                    getPropertyString("com_port"));

//            FMultipleChoice multiProp = (FMultipleChoice) getFocProperty(InstrumentDesc.FLD_SERIAL_BAUDE_RATE);
//            tempProperties.put("serialPort.baudrate", multiProp.getString());
//
//            multiProp = (FMultipleChoice) getFocProperty(InstrumentDesc.FLD_SERIAL_DATA_BITS);
//            tempProperties.put("serialPort.databits", multiProp.getString());
//
//            multiProp = (FMultipleChoice) getFocProperty(InstrumentDesc.FLD_SERIAL_PARITY);
//            tempProperties.put("serialPort.parity", multiProp.getString());
//
//            multiProp = (FMultipleChoice) getFocProperty(InstrumentDesc.FLD_SERIAL_STOP_BIT);
//            tempProperties.put("serialPort.stopbit", multiProp.getString());

            // We assign the properties only if everything went fine without
            // exception.
            properties = tempProperties;
            FocList supportedTestList = getSupportedTestList();
            for (int i = 0; i < supportedTestList.size(); i++) {
                properties.put(
                        "test."
                                + ((FocTestLabelMap) supportedTestList
                                .getFocObject(i)).getLisTestLabel(),
                        ((FocTestLabelMap) supportedTestList.getFocObject(i))
                                .getInstrumentTestCode());
            }

            String str = properties.getProperty("log.bufferDetails");
            if (str != null && str.compareTo("1") == 0) {
                logBufferDetails = true;
            }

//            if (getPropertyBoolean(InstrumentDesc.FLD_IS_EMULATOR)) {
//                String relatedInstr = (String) getPropertyString(InstrumentDesc.FLD_RELATED_INSTRUMENT);
//                if (relatedInstr != null
//                        && relatedInstr.trim().compareTo("") != 0) {
//                    tempProperties.put("relatedInstrument.code", relatedInstr);
//                }
//            }
        }
        return properties;
    }

    public boolean isLogBufferDetails() {
        return logBufferDetails;
    }

    public boolean sendWithDriverReservation(L3Message message)
            throws Exception {
        boolean error = true;
        IDriver driver = getDriver();
        if (!driver.reserve()) {
            try {
                send(message);
                driver.release();
                error = false;
            } catch (Exception e) {
                driver.release();
                throw e;
            }
        }
        return error;
    }

    private void send(L3Message message) throws Exception {
//        logString("Message before send:" + message.toStringBuffer());
//        driver.send(message);
//        Iterator sampleIterator = message.sampleIterator();
//        while (sampleIterator.hasNext()) {
//            ((FocLabSample) sampleIterator.next())
//                    .updateStatusForTests(FocLabTestDesc.TEST_STATUS_ANALYSING);
//        }
    }

    public void addMessageListener(MessageListener listener) {
        if (driver != null) {
            driver.addListener(listener);
        }
    }

    public void removeMessageListener(MessageListener listener) {
        if (driver != null) {
            driver.removeListener(listener);
        }
    }

    public boolean isEmulator() {
        return false;
    }

    public void logException(Exception e) {
        Globals.logString(getName() + "->Exception");
        Globals.logException(e);
    }

    public synchronized void logString(String str) {
        Globals.logString(getName() + "->" + str);
    }

    public synchronized void logString(StringBuffer str) {
        if (ConfigInfo.isLogDetails() || !isEmulator()) {
            Globals.logString(getName() + "->" + str);
        }
    }

    private boolean runContent() {
//        int size = 0;
//        boolean driverSaidToTryLater = false;
//        logString("POLLING runContent.Before IsConnected");
//        if (isConnected()) {
//            logString("POLLING runContent.Inside IsConnected");
//            L3Message messageReadyToSend = null;
//            driverSaidToTryLater = false;
//            L3Message message;
//            size = 1;
//            while (size > 0 && !driverSaidToTryLater) {
//                logString("POLLING runContent.Inside while 1");
//                if (messageReadyToSend != null
//                        && messageReadyToSend.getNumberOfSamples() > 0) {
//                    int i = 0;
//                    while (i < messageReadyToSend.getNumberOfSamples()
//                            && !driverSaidToTryLater) {
//                        logString("POLLING runContent.Inside while 2");
//
//                        Globals.logDetail(getName()
//                                + " trying to reserve driver");
//                        if (!driver.reserve()) {
//                            message = new L3Message();
//                            L3Sample sample = messageReadyToSend.getSample(i);
//                            // sample.updateStatus(L3SampleDesc.SAMPLE_STATUS_SENDING_TO_INSTRUMENT);
//                            message.addSample(sample);// we have to test the
//                            // status of the sample;
//                            // if it is blocked we
//                            // dont add
//                            try {
//                                send(message);
//                                i++;
//                            } catch (L3TryLaterException e) {
//                                logString("L3TryLaterException Driver suspended comunication.");
//                                driverSaidToTryLater = true;
//                            } catch (L3InstrumentDoesNotRespondTryLaterException e) {
//                                logString("L3InstrumentDoesNotRespondTryLaterException Driver not responding");
//                                logString(e.getMessage());
//                                driverSaidToTryLater = true;
//                            } catch (Exception e) {
//                                i++;
//                                logException(e);
//                                sample.updateBlockedForTests(true);
//                                sample.updateStatusForTests(L3TestDesc.TEST_STATUS_RESULT_AVAILABLE);
//                                sample.updateNotificationMessageForTests("WHEN SEND TO INST:"
//                                        + e.getMessage());
//                            }
//                            driver.release();
//                        } else {
//                            try {
//                                Thread.sleep(getPropertyInteger(InstrumentDesc.FLD_DELAY_TO_TRY_AGAIN_THE_DRIVER_RESERVE));
//                            } catch (InterruptedException e) {
//                                Globals.logException(e);
//                            }
//                        }
//                    }
//                }
//
//                if (messageReadyToSend != null) {
//                    messageReadyToSend.dispose();
//                    messageReadyToSend = null;
//                }
//                if (!driverSaidToTryLater) {
//                    sampleTestFilterReadyToSend.setActive(true);
//                    messageReadyToSend = sampleTestFilterReadyToSend
//                            .convertToMessage();
//                    size = messageReadyToSend != null ? messageReadyToSend
//                            .getNumberOfSamples() : 0;
//                    // Here we are adding to the samples to send, the already
//                    // sent tests but do not have results yet
//                    if (size > 0 && isResendAllPendingTests()) {
//                        for (int i = 0; i < messageReadyToSend
//                                .getNumberOfSamples(); i++) {
//                            L3Sample sample = messageReadyToSend.getSample(i);
//                            L3SampleTestJoinFilter pendingList = getSampleTestList_PendingTestsToBeResentWithNewTests();
//                            pendingList.setSampleID(sample.getId());
//                            pendingList.setActive(true);
//                            pendingList.addAllTestsToSameSample(sample);
//                        }
//                    }
//                }
//            }
//        }
//        return driverSaidToTryLater;
        return false;
    }

    public void sendASampleAnsweringInquiry(String rackNumber, String tubePosition, String sampleId) {
//        logString("Instrument : sendASampleAnsweringInquiry 1 sample : "+sampleId);
//        L3SampleTestJoinFilter filter = getSampleListToSendAfterEnquiry(sampleId);
//        filter.setActive(true);
//
//        logString("Instrument : sendASampleAnsweringInquiry 2 ");
//
//        L3Message messageReadyToSend = filter.convertToMessage();
//        int size = messageReadyToSend != null ? messageReadyToSend.getNumberOfSamples() : 0;
//
//        logString("Instrument : sendASampleAnsweringInquiry 3 size : " +size);
//
//        if (size > 0) {
//            logString("Instrument : sendASampleAnsweringInquiry 4 nbrSamples : " +messageReadyToSend.getNumberOfSamples());
//            for (int i = 0; i < messageReadyToSend.getNumberOfSamples(); i++) {
//                L3Sample sample = messageReadyToSend.getSample(i);
//                sample.setRackNumber(rackNumber);
//                sample.setTubePosition(tubePosition);
//
//                L3SampleTestJoinFilter pendingList = getSampleTestList_PendingTestsToBeResentWithNewTests();
//                pendingList.setSampleID(sample.getId());
//                pendingList.setActive(true);
//                pendingList.addAllTestsToSameSample(sample);
//
//                logString("Instrument : sendASampleAnsweringInquiry 5 ");
//
//                if (!driver.reserve()) {
//                    L3Message message = new L3Message();
//                    // sample.updateStatus(L3SampleDesc.SAMPLE_STATUS_SENDING_TO_INSTRUMENT);
//                    message.addSample(sample);// we have to test the
//                    // status of the sample;
//                    // if it is blocked we
//                    // dont add
//                    try {
//                        logString("Instrument : sendASampleAnsweringInquiry 6 - Sending ");
//                        send(message);
//                        logString("Instrument : sendASampleAnsweringInquiry 7 - Send done");
//                        i++;
//                    } catch (L3TryLaterException e) {
//                        logString("L3TryLaterException Driver suspended comunication.");
//                        //driverSaidToTryLater = true;
//                    } catch (L3InstrumentDoesNotRespondTryLaterException e) {
//                        logString("L3InstrumentDoesNotRespondTryLaterException Driver not responding");
//                        logString(e.getMessage());
//                        //driverSaidToTryLater = true;
//                    } catch (Exception e) {
//                        i++;
//                        logException(e);
//                        sample.updateBlockedForTests(true);
//                        sample.updateStatusForTests(L3TestDesc.TEST_STATUS_RESULT_AVAILABLE);
//                        sample.updateNotificationMessageForTests("WHEN SEND TO INST:"
//                                + e.getMessage());
//                    }
//                    driver.release();
//                }
//            }
//        }
//
//        if (filter != null) {
//            filter.dispose();
//            filter = null;
//        }
    }


    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
//        int delay = getPropertyInteger(InstrumentDesc.FLD_DELAY_POLLING_FOR_DB_SAMPLES_TO_SEND);
//        if (sampleTestFilterReadyToSend == null) {
//            sampleTestFilterReadyToSend = getSampleListToSend();
//        }
//        boolean driverSaidToTryLater = false;
//
//        while (true) {
//            try {
//                logString("POLLING Before RunContect");
//                driverSaidToTryLater = runContent();
//
//                int theDelay = delay;
//                if (driverSaidToTryLater) {
//                    theDelay = Math.max(theDelay, getPropertyInteger(InstrumentDesc.FLD_DELAY_TO_TRY_LATER));
//                }
//
//                try {
//                    DriverSerialPort driverSerial = (DriverSerialPort) getDriver();
//                    if (driverSerial.shouldResetConnection()) {
//                        Globals.logString("!!! Resetting the Connection ...");
//                        Globals.logString("		Switch OFF ...");
//                        switchOff();
//                        Globals.logString("		Done Off");
//                        Globals.logString("		Switch On  ...");
//                        switchOn();
//                        Globals.logString("		Done On");
//                        Globals.logString("Done Reset");
//                    }
//                } catch (Exception e) {
//                    Globals.logException(e);
//                }
//
//                logString("POLLING Before Sleep " + theDelay);
//                Thread.sleep(theDelay);
//            } catch (Exception e) {
//                Globals.logException(e);
//            }
//        }
    }

    /*
     * (non-Javadoc)
     *
     * @see b01.l3.MessageListener#messageReceived(b01.l3.data.L3Message)
     */
    public void messageReceived(L3Message message) {
//        try {
//            getDriver().makeSpecialCommentTreatmentBeforeSendingToLIS(message);
//        } catch (Exception e) {
//            Globals.logException(e);
//        }
//        if(Globals.getDBManager() != null){
//            //This is the normal case only while testing the DBManager can be null so we just print a log
//            message.upgradeMessageSamples_ToResultsAvailable(this);
//        }else{
//            //We print a log only in testing. Testing is the only case where DBManager == null
//            StringBuffer stringBuffer = message.toStringBuffer();
//            String str = stringBuffer.toString();
//            Globals.logString(str);
//        }
    }

    // ooooooooooooooooooooooooooooooooooo
    // oooooooooooooooooooooooooooooooooo
    // GET SET
    // oooooooooooooooooooooooooooooooooo
    // oooooooooooooooooooooooooooooooooo

    public String getCode() {
        return getPropertyString("code");
    }

    public void setCode(String cd) {
        setPropertyString("code", cd);
    }

    public String getName() {
        return getPropertyString("name");
    }

    public void setName(String name) {
        setPropertyString("name", name);
    }

    public void setDriverClassName(String clName) {
        setPropertyString("driver_class_name", clName);
    }

    public boolean isConnected() {
        return getPropertyBoolean("connected");
    }

    public void setConnected(boolean connected) {
        setPropertyBoolean("connected", connected);
    }

    public int getMode() {
        return getPropertyInteger("mode");
    }

    public void setMode(int mode) {
        setPropertyInteger("mode", mode);
    }

    public boolean isWaitForResultConfirmation() {
        return getPropertyBoolean("wait_for_result_confirmation");
    }

    public void setWaitForResultConfirmation(boolean wait) {
        setPropertyBoolean("wait_for_result_confirmation", wait);
    }

    public void updateConnected(boolean connected) {
//        FocDesc focDesc = getThisFocDesc();
//        if (focDesc != null /* && isConnected() != connected */) {
//            setConnected(connected);
//            SQLUpdate sqlUpdate = new SQLUpdate(focDesc, this);
//            sqlUpdate.addQueryField(InstrumentDesc.FLD_CONNECTED);
//            sqlUpdate.execute();
//        }
    }

    public void refreshConnected() {
//        FocDesc focDesc = getThisFocDesc();
//        if (focDesc != null /* && isConnected() != connected */) {
//            // setConnected(connected);
//            SQLFilter filter = new SQLFilter(this,
//                    SQLFilter.FILTER_ON_IDENTIFIER);
//            SQLSelect sqlSelect = new SQLSelect(this, Instrument.getFocDesc(),
//                    filter);
//            sqlSelect.addQueryField(FField.REF_FIELD_ID);
//            sqlSelect.addQueryField(InstrumentDesc.FLD_CONNECTED);
//            sqlSelect.execute();
//            adjustColor(this, InstrumentDesc.FLD_LAUNCHED,
//                    InstrumentDesc.FLD_CONNECTED, InstrumentDesc.FLD_ON_HOLD);
//        }
    }

    public void refreshLaunched() {
//        boolean launched = false;
//        try {
//            launched = !getService().ping();
//        } catch (Exception e) {
//            Globals.logString("Normal exception in refreshLaunched");
//            launched = false;
//        }
//        setPropertyBoolean(InstrumentDesc.FLD_LAUNCHED, launched);
//        if (!launched) {
//            setPropertyBoolean(InstrumentDesc.FLD_CONNECTED, false);
//            updateConnected(false);
//            adjustColor(this, InstrumentDesc.FLD_LAUNCHED,
//                    InstrumentDesc.FLD_CONNECTED, InstrumentDesc.FLD_ON_HOLD);
//        }
    }

    public IDriver getDriver() throws Exception {
        if (driver == null) {
            String driverClassName = getPropertyString("driver_class_name");

            // Direct instantiation using reflection
            try {
                // Load the class using the class name
                Class<?> driverClass = Class.forName(driverClassName);

                // Create a new instance of the class
                IDriver tempDriver = (IDriver) driverClass.getDeclaredConstructor().newInstance();

                if (tempDriver != null) {
                    tempDriver.init(this, getProperties());
                    driver = tempDriver;
                }
            } catch (ClassNotFoundException e) {
                throw new Exception("Driver class not found: " + driverClassName, e);
            } catch (Exception e) {
                throw new Exception("Failed to instantiate driver: " + driverClassName, e);
            }
        }
        return driver;
    }

//    public L3SampleTestJoinFilter getSampleListToSend() {
//        L3SampleTestJoinFilter filter = L3SampleTestJoinDesc
//                .newListWithFilter();
//        filter.setInstrumentStatus(this, L3TestDesc.TEST_STATUS_AVAILABLE_IN_L3);
//        return filter;
//        return null;
//    }


//    public L3SampleTestJoinFilter getSampleListToSendAfterEnquiry(String sampleId) {
//        L3SampleTestJoinFilter filter = L3SampleTestJoinDesc
//                .newListWithFilter();
//        filter.setInstrumentEquals(this);
//        filter.setBlockedEquals(false);
//        filter.setSampleID(sampleId);
//        return filter;
//    }
//
//    public L3SampleTestJoinFilter getSampleTestList_PendingTestsToBeResentWithNewTests() {
//        if (sampleTestFilterPendingTestsToBeResentWithNewTests == null) {
//            sampleTestFilterPendingTestsToBeResentWithNewTests = L3SampleTestJoinDesc
//                    .newListWithFilter();
//            sampleTestFilterPendingTestsToBeResentWithNewTests
//                    .setInstrumentStatus(this, L3TestDesc.TEST_STATUS_ANALYSING);
//        }
//        return sampleTestFilterPendingTestsToBeResentWithNewTests;
//    }

    public FocList getSupportedTestList() {
        if (supportedTestList == null) {
            FocDesc slaveDesc = Globals.getApp().getFocDescByName("test_label_map");
            int fieldId = slaveDesc.getFieldIDByName("instrument");
            FocLinkForeignKey link = new FocLinkForeignKey(
                    Globals.getApp().getFocDescByName("test_label_map"), fieldId, true);
            supportedTestList = new FocList(this, link, null);
            supportedTestList.setFatherSubject(this);
        }
        supportedTestList.loadIfNotLoadedFromDB();

        return supportedTestList;
    }

    // For archive
    /*
     * public void setCommitedSamplesAsUnused(int status){ perjeSamples(); }
     */

    public void replyToExit() {
        try {
//            switchOff();
            forceControler(true);
            validate(false);
        } catch (Exception e) {
            Globals.logException(e);
        }
    }



    // -----------------------------------------------------------------
    // -----------------------------------------------------------------
    // -----------------------------------------------------------------

//    private SimpleMessageListenerContainer container;
//    private final ConnectionFactory connectionFactory;
//
//    public void switchOn() throws Exception {
//
//    }
}

