package com.demo.exception;

public class UnauthorizedSessionException extends Exception {
  private static final long serialVersionUID = 1L;
  private String messageDesc;

  public UnauthorizedSessionException(String messageDesc) {
    super();
    this.messageDesc = messageDesc;
  }

  public static UnauthorizedSessionException getUnauthorizedSessionException() {
    return new UnauthorizedSessionException("Request not valid. Authorization failed.");
  }

  public String getMessageDesc() {
    return messageDesc;
  }

  public void setMessageDesc(String messageDesc) {
    this.messageDesc = messageDesc;
  }
}
