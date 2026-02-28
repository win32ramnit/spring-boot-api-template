package com.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class AppException extends Exception {
  private static final long serialVersionUID = -7556992120838314140L;
  private final String messageCode;
  private final String messageDesc;
  private final String moreInfo;

  public AppException(final String messageCode, final String messageDesc, final String moreInfo) {
    this.messageCode = messageCode;
    this.messageDesc = messageDesc;
    this.moreInfo = moreInfo;
  }
}
