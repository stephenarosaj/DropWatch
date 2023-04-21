package edu.brown.cs.student.CSVsearch;

import edu.brown.cs.student.api.exceptions.BadColumnException;
import edu.brown.cs.student.rowCreation.CreatorFromRow;
import edu.brown.cs.student.rowCreation.FactoryFailureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class creates a Searcher that searches for a string within a CSV file. It implements
 * CreatorFromRow, which allows it to simulatenously search as the parser is parsing through the CSV
 * file.
 */
public class Searcher implements CreatorFromRow<List<String>> {
  private String toSearch;
  private Integer colIdx;
  private List<List<String>> targetRows;

  /**
   * Constructs a Searcher object.
   *
   * @param userInput String reprsenting the value to be searched for.
   */
  public Searcher(String userInput) {
    toSearch = userInput;
    colIdx = -1;
    targetRows = new ArrayList<>();
  }

  /**
   * Sets the column to search in (called in main if the user has provided a column arg). It tries
   * to create an integer, which works if the user has passed in an integer; if not, it catches the
   * exception and instead tries to find the index of the column name. If the col cannot be found,
   * it prints an error and exits the program.
   *
   * @param colName A String representing the name of the column to search in.
   * @param headers An array of Strings representing the header of the CSV file.
   * @return The column integer corresponding to the String idx/name passed in.
   */
  public void setCol(String colName, String[] headers) throws BadColumnException {
    try {
      Integer intForm = Integer.valueOf(colName);
      colIdx = intForm;
      if (colIdx < 0 || colIdx > headers.length - 1) {
        throw new BadColumnException(
            "column " + colName + " out of bounds, must be between 0 and " + (headers.length - 1));
      }
    } catch (NumberFormatException e) {
      for (int i = 0; i < headers.length; i++) {
        if (colName.equals(headers[i])) {

          colIdx = i;
        }
      }
      if (colIdx < 0) {
        throw new BadColumnException(
            "could not find col '"
                + colName
                + "', must be one of the"
                + " following: "
                + Arrays.toString(headers));
      }
    }
  }

  /**
   * An accessor for the colIdx.
   *
   * @return An integer representing the colIdx to search in. If none has been specified, it returns
   *     -1.
   */
  public Integer getColIdx() {
    return colIdx;
  }

  /**
   * An accessor for the target rows.
   *
   * @return A 2D ArrayList representing the rows of the CSV where the target value was found.
   */
  public List<List<String>> getTargetRows() {
    return targetRows;
  }

  /**
   * This method comes from the CreatorFromRow interface and creates an object from the list of
   * strings representing the CSV. In this case, it adds the target rows to Searcher's own list.
   *
   * @param row A List of Strings representing the row of a CSV file.
   * @return A List of Strings representing the row of a CSV file if it contains the target value,
   *     and an empty List if not.
   * @throws FactoryFailureException If the column given is out of bounds of the row.
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    if (colIdx >= 0) {
      if (colIdx >= row.size()) {
        throw new FactoryFailureException("Column given was out of bounds of row", row);
      }
      if (toSearch.equals(row.get(colIdx))) {
        targetRows.add(row);
        return row;
      }
      return new ArrayList<>();
    }

    for (int c = 0; c < row.size(); c++) {
      if (toSearch.equals(row.get(c))) {
        targetRows.add(row);
        return row;
      }
    }
    return new ArrayList<>();
  }
}
