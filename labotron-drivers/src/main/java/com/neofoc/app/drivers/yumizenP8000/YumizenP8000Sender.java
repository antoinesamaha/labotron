package com.neofoc.app.drivers.yumizenP8000;

import com.foc.util.ASCII;
import com.neofoc.app.connection.basicsocket.BClientKeepOpened;
import com.neofoc.app.connection.basicsocket.LogInterface;

public class YumizenP8000Sender {
	private YumizenP8000Driver driver  = null;	
	private BClientKeepOpened bClient = null;
	
	public YumizenP8000Sender(YumizenP8000Driver driver) {
		this.driver = driver;
	}
	
	public void dispose() {
		driver = null;
		if(bClient != null) {
			bClient.dispose();
			bClient = null;
		}
	}
	
	private void logString(String str) {
		if(driver != null && driver.getInstrument() != null) {
			driver.getInstrument().logString(str);
		}
	}

	private void logException(Exception e) {
		if(driver != null && driver.getInstrument() != null) {
			driver.getInstrument().logException(e);
		}
	}
	
	public void init() {
	//ATTENTION
//		bClient = new BClientKeepOpened("localhost", 10001);
		bClient = new BClientKeepOpened("192.168.170.63", 10001);
//		bClient = new BClientKeepOpened("192.168.0.1", 10001);

		bClient.setLogInterface(new LogInterface(){
			public void logException(Exception e) {
				YumizenP8000Sender.this.logException(e);
			}

			public void logString(String str) {
				YumizenP8000Sender.this.logString(str);
			}
		});
	}

	public void connect() {
		if(bClient != null) {
			logString("Connecting YumizenSender");
			bClient.open();
			logString("Connected YumizenSender");
		} else {
			logString("Could not connect Yumizen Sender: bClient is null");
		}
	}
	
	public void disconnect() {
		if(bClient != null) {
			logString("Closing YumizenSender");
			bClient.close();
			logString("Closed YumizenSender");
		} else {
			logString("Could not close Yumizen Sender: bClient is null");
		}
	}
	
	public boolean sendMessage(String messageToSend) {
		boolean error = false;

		connect();
		try {
			if (bClient != null) {
				String loggableMessage = ASCII.convertNonCharactersToDescriptions(messageToSend);
				logString("Sending using YumizenSender: " + loggableMessage);
				String answer = bClient.sendMessage(messageToSend);
				logString("YumizenSender Received: " + answer);
				if (answer == null || !answer.contains("MSA|AA")) {
					error = true;
				}
			} else {
				logString("Could not send using Yumizen Sender: bClient is null");
			}
		} catch (Exception e) {
			logString("Error sending using YumizenSender: " + e.getMessage());
			logException(e);
			error = true;
		} finally {
			disconnect();
		}
		return error;
	}
}
