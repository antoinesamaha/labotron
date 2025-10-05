package com.neofoc.app.driver;

import com.foc.list.FocList;
import com.neofoc.app.modules.labotron.Instrument;
import com.neofoc.app.modules.labotron.LabMessage;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.L3Message;

import java.util.Properties;

// Created on May 7, 2006
public interface IDriver {
    void init(FocInstrument instrument, Properties props) throws Exception;

    void dispose();

    boolean isBusy();

    boolean reserve();

    void release();

    void connect() throws Exception;

    boolean isConnected();

    void disconnect();

    void send(L3Message message) throws Exception;

    void addListener(MessageListener driverListener);

    void removeListener(MessageListener driverListener);

    void completeListOfAvailableTests(FocList testList) throws Exception;

    boolean isResendAllPendingTests();

    void makeSpecialCommentTreatmentBeforeSendingToLIS(L3Message message);

    boolean isInquiryBased();
}
