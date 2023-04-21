package edu.brown.cs.student.api.exceptions;

/** Exception to be thrown when moshi fails to deserialize a buffer. */
public class DeserializeException extends Exception {

  /**
   * Creates new DeserializeException.
   *
   * @param message Message with more information.
   */
  public DeserializeException(String message) {
    super(message);
  }
}
