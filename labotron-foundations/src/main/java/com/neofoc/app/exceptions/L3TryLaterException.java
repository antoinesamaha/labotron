/*
 * Created on Jun 1, 2006
 */
package com.neofoc.app.exceptions;

/**
 * @author 01Barmaja
 */
public class L3TryLaterException extends L3Exception {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3256442525320491062L;

  /**
   * @param message
   * @param cause
   */
  public L3TryLaterException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }
  /**
   * @param cause
   */
  public L3TryLaterException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }
  /**
   * 
   */
  public L3TryLaterException() {
    super();
    // TODO Auto-generated constructor stub
  }
  /**
   * @param message
   */
  public L3TryLaterException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }
}
