package com.neofoc.app.driver;

import java.util.HashMap;
import java.util.Iterator;

public class DriverFactory {
	private HashMap<String, Class> driverMap = null;
	
	private static DriverFactory driverFactory = null;
	
	public static DriverFactory getInstance(){
		if(driverFactory == null) driverFactory = new DriverFactory();
		return driverFactory;
	}
	
	public DriverFactory(){
		driverMap = new HashMap<String, Class>();
	}
	
	public void addDriver(String className, Class cls){
  	if(driverMap != null && cls != null){
  		driverMap.put(className, cls);
  	}
  }
	
	public Class getDriver(String code){
    return driverMap != null && code != null? driverMap.get(code) : null;
    //return driverMap.get("b01.l3.drivers.kermit.Vitros250Driver");
  }
	
  public Iterator<String> newClassNameIterator(){
  	return driverMap.keySet().iterator();
  }
}
