package edu.brown.cs.student.rowCreation;

import java.util.ArrayList;
import java.util.List;

/** An Exception that is thrown whenever the create method of the CreatorFromRow fails. */
public class FactoryFailureException extends Exception {
  final List<String> row;

  /**
   * Constructs a FactoryFailureException.
   *
   * @param message Message to be printed out on the command line.
   * @param row A row of Strings.
   */
  public FactoryFailureException(String message, List<String> row) {
    super(message);
    this.row = new ArrayList<>(row);
  }
}
