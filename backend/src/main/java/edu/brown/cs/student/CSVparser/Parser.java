package edu.brown.cs.student.CSVparser;

import edu.brown.cs.student.rowCreation.CreatorFromRow;
import edu.brown.cs.student.rowCreation.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A generic class which allows the user to input some sort of Reader object, apply a custom creator
 * to the rows of a CSV to create an object, and get a list of those objects. With no custom creator
 * it simply returns a 2D arraylist representing the CSV.
 *
 * @param <T> The class type which the Parser will be returning a list of; i.e. Parser(Int) will
 *     return a List of integers.
 */
public class Parser<T> {
  private BufferedReader file;
  private CreatorFromRow<T> creator;
  private boolean alreadyParsed;
  private List<T> parsedData;
  String[] headers;

  /**
   * Constructs a parser object and sets up the headers, if there are any.
   *
   * @param userInputFile The Reader object representing the CSV file.
   * @param removeHeaders A boolean representing whether the CSV should remove headers before
   *     parsing.
   * @param customCreator A CreatorFromRow object that specifies what to create from the CSV rows.
   */
  public Parser(Reader userInputFile, boolean removeHeaders, CreatorFromRow customCreator)
      throws IOException {

    file = new BufferedReader(userInputFile);
    creator = customCreator;
    alreadyParsed = false;
    parsedData = new ArrayList<>();
    headers = new String[0];

    // read the first line of the data, throws IOException if unable to
    if (removeHeaders) {
      String firstLine = file.readLine();
      if (firstLine != null) {
        headers = firstLine.split(",");
      }
    }
  }

  public Parser(Reader userInputFile, boolean removeHeaders) throws IOException {
    this(userInputFile, removeHeaders, new DefaultRowCreator());
  }

  /**
   * Constructs a parser for users who don't provide a custom row creation and passes in a
   * DefaultRowCreator.
   *
   * @param userInputFile A Reader object that reprsents a CSV.
   */
  public Parser(Reader userInputFile) throws IOException {
    this(userInputFile, false, new DefaultRowCreator());
  }

  /**
   * Accessor for CSV headers.
   *
   * @return A String array reprsenting the headers of a CSV file. Empty if the CSV has no headers.
   */
  public String[] getHeaders() {
    return headers;
  }

  /**
   * Parses through each line of the CSV, applies a creation method to it, and returns a list of the
   * objects created from each line. Catches FactoryFailureExceptions and prints out an error
   * message specifying which rows failed; it attempts to continue, in the case that only one row is
   * malformed. Catches IOExceptions and quits out of the program with an error message.
   *
   * @return A List of Objects that is created from each row of the CSV file. If the default creator
   *     was used, a 2D ArrayList of strings representing the CSV content is returned.
   */
  public List<T> parse() throws IOException, FactoryFailureException {
    if (!alreadyParsed) {
      String currLine = file.readLine();
      while (currLine != null) {

        // create the line with the custom creator, catch FactoryFailureException
        T createdLine = creator.create(Arrays.asList(currLine.split(",")));
        parsedData.add(createdLine);
        currLine = file.readLine();
      }
      alreadyParsed = true;
    }
    return parsedData;
  }
}
