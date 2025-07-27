/*
 * Created on Jun 1, 2006
 */
package com.neofoc.app.exceptions;

/**
 * @author 01Barmaja
 */
public class L3InstrumentDoesNotRespondTryLaterException extends L3Exception {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3256442525320491062L;

  /**
   * @param message
   * @param cause
   */
  public L3InstrumentDoesNotRespondTryLaterException(String message, Throwable cause) {
    super(message, cause);
  }
  /**
   * @param cause
   */
  public L3InstrumentDoesNotRespondTryLaterException(Throwable cause) {
    super(cause);
  }
  /**
   * 
   */
  public L3InstrumentDoesNotRespondTryLaterException() {
    super();
  }
  /**
   * @param message
   */
  public L3InstrumentDoesNotRespondTryLaterException(String message) {
    super(message);
  }
}
