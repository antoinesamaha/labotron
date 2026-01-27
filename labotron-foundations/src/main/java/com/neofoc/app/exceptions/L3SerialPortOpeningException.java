/*
 * Created on Jun 1, 2006
 */
package com.neofoc.app.exceptions;

/**
 * @author 01Barmaja
 */
public class L3SerialPortOpeningException extends L3Exception {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3977858462653101112L;
  
  /**
   * @param message
   * @param cause
   */
  public L3SerialPortOpeningException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }
  /**
   * @param cause
   */
  public L3SerialPortOpeningException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }
  /**
   * 
   */
  public L3SerialPortOpeningException() {
    super();
    // TODO Auto-generated constructor stub
  }
  /**
   * @param message
   */
  public L3SerialPortOpeningException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }
}
