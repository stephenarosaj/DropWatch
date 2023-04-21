package edu.brown.cs.student.api.exceptions;

/** Exception thrown when the user requests to search in an invalid column. */
public class BadColumnException extends Exception {
  /**
   * Constructs a BadColumnException.
   *
   * @param message Message to be printed out on the command line.
   */
  public BadColumnException(String message) {
    super(message);
  }
}
