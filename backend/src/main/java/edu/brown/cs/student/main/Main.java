package edu.brown.cs.student.main;

import edu.brown.cs.student.CSVparser.Parser;
import edu.brown.cs.student.CSVsearch.Searcher;
import edu.brown.cs.student.api.exceptions.BadColumnException;
import edu.brown.cs.student.rowCreation.FactoryFailureException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Scanner;

/** The Main class of our project. This is where execution begins. */
public final class Main {
  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private Main(String[] args) {}

  /**
   * The run method, which runs the program. It asks the user for input, checks its validity, and
   * creates a file, parser, and searcher.
   */
  private void run() {
    String[] args = this.getUserInput();

    // create reader from passed in file
    Reader file = this.getFileReader(args[0]);
    // create Searcher for the string
    Searcher searcher = new Searcher(args[1]);

    // set up parser depending on whether CSV has headers or not
    Parser<List<String>> targetRowParser = null;
    try {
      targetRowParser = this.getParser(file, args[2], searcher);
    } catch (IOException e) {
      System.err.println("Failed to read header line.");
    }

    // set up searcher if column was provided
    if (args.length == 4) {
      try {
        searcher.setCol(args[3], targetRowParser.getHeaders());
      } catch (BadColumnException e) {
        System.out.print(e.getMessage());
        System.exit(1);
      }
    }

    try {
      targetRowParser.parse();
    } catch (IOException e) {
      System.err.println("Error occurred while reading lines during parsing");
      System.exit(1);
    } catch (FactoryFailureException e) {
      System.err.println("Factory failure occurred while parsing");
      System.exit(1);
    }
    this.printAppropriateMessage(args, searcher.getTargetRows());
  }

  /**
   * Gives the user input format guidelines, asks for user input, and splits it. This method checks
   * if the user has given enough arguments and exits the program with an error message if they have
   * not.
   *
   * @return An array of strings (String[]) representing the user input arguments.
   */
  private String[] getUserInput() {

    Scanner scanner = new Scanner(new InputStreamReader(System.in));
    System.out.println(
        "Input: [Absolute File Path],[String to Search],[CSV Has Headers],"
            + "[Col Idx or Name (Optional)]");
    String[] args = scanner.nextLine().split(",");

    if (args.length < 3) {
      System.err.println(
          "You must enter at least: [file path],[string to search],"
              + "[boolean representing if the CSV "
              + "has headers]");
      System.exit(1);
    }
    return args;
  }

  /**
   * Creates a new FileReader object if there is a valid file name given. If there is not, it
   * returns an error message and exits from the program.
   *
   * @param fileName A string representing the absolute file path.
   * @return A FileReader that contains the contents of the file.
   */
  private Reader getFileReader(String fileName) {
    try {
      return new FileReader(fileName);
    } catch (FileNotFoundException e) {
      System.err.println("Couldn't find file " + fileName + ".");
      System.exit(1);
      return null;
    }
  }

  /**
   * Returns the correct type of parser depending on whether the user indicated the CSV has headers
   * or not.
   *
   * @param file The FileReader object created from the user-inputted file.
   * @param hasHeadersString A string representing whether the CSV has headers (true/false).
   * @param searcher The searcher whose CreatorFromRow will be used.
   * @return A Parser set up with the Searcher's create method and CSV headers.
   */
  private Parser<List<String>> getParser(Reader file, String hasHeadersString, Searcher searcher)
      throws IOException {
    if (hasHeadersString.equals("true")) {
      return new Parser<>(file, true, searcher);
    }
    if (hasHeadersString.equals("false")) {
      return new Parser<>(file, false, searcher);
    }
    // if it has reached ths point, the string is not true/false
    System.out.println("You must say 'true' if the CSV has headers and 'false' if not.");
    System.exit(0);
    return null;
  }

  /**
   * Prints the appropriate message to the user indicating whether the program has found their value
   * and if so, the rows containing it.
   *
   * @param args String array representing the user inputted arguments.
   * @param finalRows A list of lists representing the CSV rows containing the target value.
   */
  private void printAppropriateMessage(String[] args, List<List<String>> finalRows) {
    if (finalRows.size() == 0) {
      if (args.length < 4) {
        System.out.println("'" + args[1] + "' was not found in the CSV.");
      } else {
        System.out.println("'" + args[1] + "' was not found in the CSV at column " + args[3] + ".");
      }
    } else {
      System.out.println("'" + args[1] + "' was found in the following row(s):");
      for (int i = 0; i < finalRows.size(); i++) {
        System.out.println(finalRows.get(i));
      }
    }
  }
}
