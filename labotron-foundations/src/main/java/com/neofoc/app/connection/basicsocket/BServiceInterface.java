package com.neofoc.app.connection.basicsocket;

public interface BServiceInterface {
	boolean isOn();
	boolean switchOn();
	boolean switchOff();
	String  getLaunchCommand();
	boolean exit();
	void    refreshLaunchStatus();
	void    refreshSwitchStatus();
	String  getName();
}
