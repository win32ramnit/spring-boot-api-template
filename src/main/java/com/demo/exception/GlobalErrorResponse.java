package com.demo.exception;

import com.demo.response.ServiceResponse;
import lombok.Getter;

@Getter
public class GlobalErrorResponse extends ServiceResponse {
  private static final long serialVersionUID = -7556992120838314143L;
  private final String description;
  private final String moreInfo;
  private final String errorCode;



  public GlobalErrorResponse(String description, String moreInfo, String errorCode) {
    super();
    this.description = description;
    this.moreInfo = moreInfo;
    this.errorCode = errorCode;
  }
}
