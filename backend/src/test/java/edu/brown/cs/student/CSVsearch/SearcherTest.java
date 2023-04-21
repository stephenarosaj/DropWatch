package edu.brown.cs.student.CSVsearch;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import edu.brown.cs.student.CSVparser.Parser;
import edu.brown.cs.student.api.exceptions.BadColumnException;
import edu.brown.cs.student.rowCreation.FactoryFailureException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests the Searcher class. */
public class SearcherTest {

  /**
   * Tests a value that appears once in the StarCSV, which has headers, and no column identifier.
   */
  @Test
  public void TestValueInCSV() throws IOException, FactoryFailureException {
    // Set up searcher and parser
    Reader file = new FileReader("data/stars/stardata.csv");
    Searcher searcher = new Searcher("Earline_16");
    Parser parser = new Parser<List<String>>(file, true, searcher);

    // Parser is using Searcher's customCreate method, which generates the list of targetRows within
    // searcher.
    parser.parse();

    // Set up expected result.
    List<List<String>> result =
        List.of(List.of("113405", "Earline_16", "133.61702", "-34.3452", "188.56515"));
    assertTrue(searcher.getTargetRows().equals(result));
  }

  /** Tests a value that is not in the StarCSV, which has headers, and no column identifier. */
  @Test
  public void TestValueNotInCSV() throws IOException, FactoryFailureException {
    // Set up searcher and parser
    Reader file = new FileReader("data/stars/stardata.csv");
    Searcher searcher = new Searcher("Blonky Star");
    Parser parser = new Parser<List<String>>(file, true, searcher);

    // Parser is using Searcher's customCreate method, which generates the list of targetRows within
    // searcher.
    parser.parse();

    // Should be an empty list.
    assertTrue(searcher.getTargetRows().equals(new ArrayList<>()));
  }

  /** Tests a value that appears in the StarCSV, but is in the wrong column (given string name). */
  @Test
  public void TestWrongColumnString()
      throws IOException, BadColumnException, FactoryFailureException {
    // Set up searcher and parser
    Reader file = new FileReader("data/stars/stardata.csv");
    Searcher searcher = new Searcher("Seaborn");
    Parser parser = new Parser<List<String>>(file, true, searcher);

    // Deliberately set wrong column. Seaborn is a "ProperName".
    searcher.setCol("StarID", parser.getHeaders());

    // Parser is using Searcher's customCreate method, which generates the list of targetRows within
    // searcher.
    parser.parse();

    // Should be an empty list.
    assertTrue(searcher.getTargetRows().equals(new ArrayList<>()));
  }

  /** Tests a value that appears in the StarCSV, given the right column name. */
  @Test
  public void TestRightColumnString()
      throws IOException, BadColumnException, FactoryFailureException {
    // Set up searcher and parser
    Reader file = new FileReader("data/stars/stardata.csv");
    Searcher searcher = new Searcher("Alejandro");
    Parser parser = new Parser<List<String>>(file, true, searcher);

    // Set right column.
    searcher.setCol("ProperName", parser.getHeaders());

    // Parser is using Searcher's customCreate method, which generates the list of targetRows within
    // searcher.
    parser.parse();

    // Set up expected result.
    List<List<String>> result =
        List.of(List.of("623", "Alejandro", "61.06079", "2.03192", "82.64463"));

    // Should be an empty list.
    assertTrue(searcher.getTargetRows().equals(result));
  }

  /** Tests a value that appears in the StarCSV, given the right column index. */
  @Test
  public void TestRightColumnNumber()
      throws IOException, BadColumnException, FactoryFailureException {
    // Set up searcher and parser
    Reader file = new FileReader("data/stars/stardata.csv");
    Searcher searcher = new Searcher("Alejandro");
    Parser parser = new Parser<List<String>>(file, true, searcher);

    // Set right column.
    searcher.setCol("1", parser.getHeaders());

    // Parser is using Searcher's customCreate method, which generates the list of targetRows within
    // searcher.
    parser.parse();

    // Set up expected result.
    List<List<String>> result =
        List.of(List.of("623", "Alejandro", "61.06079", "2.03192", "82.64463"));

    // Should be an empty list.
    assertTrue(searcher.getTargetRows().equals(result));
  }

  /**
   * Tests values that appear multiple times in the pets.csv, both in the same and different cols.
   */
  @Test
  public void TestMultipleRowsNoColumn() throws IOException, FactoryFailureException {
    Reader fileReader = new FileReader("data/pets.csv");

    Searcher fidoSearcher = new Searcher("fido");
    Parser<List<String>> fidoParser = new Parser<>(fileReader, true, fidoSearcher);
    fidoParser.parse();

    // Set up expected result.
    List<List<String>> fidoResult =
        List.of(
            List.of("henry", "spot", "fido"),
            List.of("camilla", "fido", "sniffy"),
            List.of("francis", "fido", "hachi"));
    assertTrue(fidoSearcher.getTargetRows().equals(fidoResult));

    // Search for another value that shows up in multiple rows.
    fileReader = new FileReader("data/pets.csv");
    Searcher spotSearcher = new Searcher("spot");
    Parser<List<String>> spotParser = new Parser<>(fileReader, true, spotSearcher);
    spotParser.parse();

    List<List<String>> spotResult =
        List.of(List.of("henry", "spot", "fido"), List.of("richard", "old yeller", "spot"));
    assertTrue(spotSearcher.getTargetRows().equals(spotResult));
  }

  /** Tests values that appear multiple times in the pets.csv in the same column. */
  @Test
  public void TestMultipleRowsColumn()
      throws IOException, BadColumnException, FactoryFailureException {
    Reader fileReader = new FileReader("data/pets.csv");

    Searcher fidoSearcher = new Searcher("fido");
    Parser<List<String>> fidoParser = new Parser<>(fileReader, true, fidoSearcher);

    // Restrict columns.
    fidoSearcher.setCol("dog_1", fidoParser.getHeaders());

    fidoParser.parse();

    // Set up expected result.
    List<List<String>> fidoResult =
        List.of(List.of("camilla", "fido", "sniffy"), List.of("francis", "fido", "hachi"));

    assertTrue(fidoSearcher.getTargetRows().equals(fidoResult));
  }

  /**
   * Tests a FactoryFailureException is thrown when the column is out of bounds of the row passed
   * in.
   */
  @Test
  public void TestFactoryFailureException() throws BadColumnException {
    Searcher searcher = new Searcher("bleh");
    searcher.setCol("1", new String[] {"bloop", "bleep"});

    assertThrows(
        FactoryFailureException.class,
        () -> {
          searcher.create(List.of("one"));
        });
  }

  /** Tests the setCol method properly sets the column. */
  @Test
  public void TestSetCol() throws BadColumnException {
    Searcher searcher = new Searcher("testSearcher");

    searcher.setCol("0", new String[] {"bloop", "bleep"});
    assertEquals(0, (int) searcher.getColIdx());

    searcher.setCol("bleep", new String[] {"bloop", "bleep"});
    assertEquals(1, (int) searcher.getColIdx());
  }
}
