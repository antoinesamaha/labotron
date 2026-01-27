/*
 * Created on Jun 1, 2006
 */
package com.neofoc.app.exceptions;

/**
 * @author 01Barmaja
 */
public class L3NullResponseException extends L3Exception {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3977858462653101112L;
  
  /**
   * @param message
   * @param cause
   */
  public L3NullResponseException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }
  /**
   * @param cause
   */
  public L3NullResponseException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }
  /**
   * 
   */
  public L3NullResponseException() {
    super();
    // TODO Auto-generated constructor stub
  }
  /**
   * @param message
   */
  public L3NullResponseException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }
}
