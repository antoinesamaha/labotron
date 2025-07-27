/*
 * Created on Jun 1, 2006
 */
package com.neofoc.app.exceptions;

/**
 * @author 01Barmaja
 */
public class L3AnalyzerErrorReturnException extends L3Exception {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3256442525320491062L;

  /**
   * @param message
   * @param cause
   */
  public L3AnalyzerErrorReturnException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }
  /**
   * @param cause
   */
  public L3AnalyzerErrorReturnException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }
  /**
   * 
   */
  public L3AnalyzerErrorReturnException() {
    super();
    // TODO Auto-generated constructor stub
  }
  /**
   * @param message
   */
  public L3AnalyzerErrorReturnException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }
}
