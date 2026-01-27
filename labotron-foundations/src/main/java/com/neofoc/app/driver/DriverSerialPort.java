package com.neofoc.app.driver;

import com.foc.Globals;
import com.foc.list.FocList;
import com.neofoc.app.connection.L3SerialPort;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.FocTestLabelMap;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

/**
 * @author 01Barmaja
 */
public abstract class DriverSerialPort extends Driver {
	protected abstract void synchronizeSequenceID() throws Exception;
	
	protected L3SerialPort l3SerialPort    = null;
	protected L3SerialPort l3SerialPortBis = null;
	private boolean unitTesting = false;
	protected HashMap<String, String> mapLis2Inst = null;
	protected HashMap<String, String> mapInst2Lis = null;

	public void init(FocInstrument instrument, Properties props) throws Exception {
		super.init(instrument, props);
	}

	public void dispose() {
		if (l3SerialPort != null) {
			l3SerialPort.dispose();
			l3SerialPort = null;
		}
		if(l3SerialPortBis != null) {
			l3SerialPortBis.dispose();
			l3SerialPortBis = null;
		}
		if (mapLis2Inst != null) {
			mapLis2Inst.clear();
			mapLis2Inst = null;
		}
		if (mapInst2Lis != null) {
			mapInst2Lis.clear();
			mapInst2Lis = null;
		}
	}
	
	public boolean isTCPIPDriver(){
		return false;
	}

	protected void testMaps_fillFromProperties(Properties props) {
		mapLis2Inst = new HashMap<String, String>();
		mapInst2Lis = new HashMap<String, String>();
		Enumeration enumer = props.keys();
		while (enumer != null && enumer.hasMoreElements()) {
			String key = (String) enumer.nextElement();
			if (key.contains("test.")) {
				mapLis2Inst.put(key.substring(5), (String) props.get(key));
				mapInst2Lis.put((String) props.get(key), key.substring(5));
			}
		}

		FocInstrument instr = getInstrument();
		if (instr != null) {
			FocList listOfTests = instr.getSupportedTestList();
			for (int i = 0; i < listOfTests.size(); i++) {
				FocTestLabelMap testMap = (FocTestLabelMap) listOfTests.getFocObject(i);
				if (testMap.getCalculated() == 1) {
					mapLis2Inst.remove(testMap.getLisTestLabel());
				}
			}
		}
	}

	protected void testMaps_removeCalculatedTest(String lisTest) {
		mapLis2Inst.remove(lisTest);
	}

	public String testMaps_getLisCode(String instrumentCode) {
		return (mapInst2Lis != null) ? mapInst2Lis.get(instrumentCode) : null;
	}

	public String testMaps_getInstCode(String lisCode) {
		return (mapLis2Inst != null) ? mapLis2Inst.get(lisCode) : null;
	}

	public void newSerialPort(Properties props) throws Exception {
		l3SerialPort = new L3SerialPort();
		l3SerialPort.setParametersFromProperties(props);
	}

	public void newSerialPortBis(Properties props) throws Exception {
		l3SerialPortBis = new L3SerialPort();
		l3SerialPortBis.setParametersFromProperties(props);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see b01.l3.Driver#connect()
	 */
	public void connect() throws Exception {
		if (l3SerialPort != null) {
			if (getDriverReceiver() != null) {
				l3SerialPort.addListener(getDriverReceiver());
			}
			l3SerialPort.openConnection();
			synchronizeSequenceID();
		}
	}

	public CompletableFuture<Void> connectAsync() {
		if (l3SerialPort != null) {
			if (getDriverReceiver() != null) {
				l3SerialPort.addListener(getDriverReceiver());
			}
			return l3SerialPort.openConnectionAsync()
				.thenRun(() -> {
					try {
						synchronizeSequenceID();
					} catch (Exception e) {
						throw new RuntimeException("Failed to synchronize sequence ID", e);
					}
				});
		}
		return CompletableFuture.completedFuture(null);
	}

	public boolean isConnected() {
		return l3SerialPort != null && l3SerialPort.isConnected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see b01.l3.Driver#disconnect()
	 */
	public void disconnect() {
		if (l3SerialPort != null) {
			if (getDriverReceiver() != null) {
				l3SerialPort.removeListener(getDriverReceiver());
			}
			l3SerialPort.closeConnection();
		}
	}

	public boolean isBusy() {
		boolean busy = super.isBusy();
		if (busy) {
			if (l3SerialPort.isWaitingForNextResultFrameTimedOut()) {
				resetBusy();
			}
		}

		return super.isBusy();
	}

	public L3SerialPort getL3SerialPort() {
		return l3SerialPort;
	}
	
	public L3SerialPort getL3SerialPortBis() {
		return l3SerialPortBis;
	}

	private void uselessWait() {
		try {
			Thread.sleep(200);
		} catch (Exception e) {
			Globals.logException(e);
		}
	}

	public void send(String strOut) throws Exception {
		uselessWait();
		l3SerialPort.send(strOut);
	}

	public L3Frame sendAndWaitForResponse(String strOut, long timeOutMilliseconds) throws Exception {
		uselessWait();
		L3Frame frame = l3SerialPort.sendAndWaitForResponse(strOut, timeOutMilliseconds);
		return frame;
	}

	public boolean shouldResetConnection() {
		return l3SerialPort != null ? l3SerialPort.shouldResetConnection() : false;
	}

	public boolean isUnitTesting() {
		return unitTesting;
	}

	public void setUnitTesting(boolean unitTesting) {
		this.unitTesting = unitTesting;
	}
}
