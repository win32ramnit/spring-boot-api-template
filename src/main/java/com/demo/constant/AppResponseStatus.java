package com.demo.constant;

/**
 * Enum representing the application response statuses.
 */
public enum AppResponseStatus {

  // @formatter:off
  /**
   * Indicates a successful response.
   */
  OK("Operation completed successfully."),

  /**
   * Indicates a bad request due to client error.
   */
  BAD_REQUEST("The request was invalid."),

  /**
   * Indicates an exception occurred during processing.
   */
  EXCEPTION("An error occurred during processing."),

  /**
   * Indicates an internal server error.
   */
  INTERNAL_SERVER_ERROR("An unexpected internal error occurred."),

  /**
   * Indicates that the input was invalid.
   */
  INVALID("The provided input is invalid.");

  // @formatter:on
  private final String message;

  AppResponseStatus(String message) {
    this.message = message;
  }

  /**
   * Returns a user-friendly message associated with the status.
   *
   * @return the status message
   */
  public String getMessage() {
    return message;
  }
}
