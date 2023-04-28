package edu.brown.cs.student.api.exceptions;

/** Exception thrown when a request to an API / external URL fails. */
public class APIRequestException extends Exception {
  private int responseCode;

  /**
   * Creates new APIRequestException.
   *
   * @param message Message to be sent.
   * @param responseCode Response code that the request failed with.
   */
  public APIRequestException(String message) {
    super(message);
    this.responseCode = responseCode;
  }

  /**
   * Accessor for responseCode.
   *
   * @return Response code integer.
   */
  public int getResponseCode() {
    return this.responseCode;
  }
}
