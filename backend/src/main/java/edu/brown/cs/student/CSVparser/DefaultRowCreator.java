package edu.brown.cs.student.CSVparser;

import edu.brown.cs.student.rowCreation.CreatorFromRow;
import edu.brown.cs.student.rowCreation.FactoryFailureException;
import java.util.ArrayList;
import java.util.List;

/**
 * The default implementation of a CreatorFromRow for the Parser class, which is instantiated if the
 * user gives no custom CreatorFromRow.
 */
public class DefaultRowCreator implements CreatorFromRow<List<String>> {

  /**
   * This method simply returns the list of strings created by the parse.
   *
   * @param row A list of strings representing the row of a CSV.
   * @return The row passed in.
   * @throws FactoryFailureException Thrown if a null row is passed in.
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    if (row == null) {
      throw (new FactoryFailureException("Attempted to pass in a null row", new ArrayList<>()));
    }
    return row;
  }
}
