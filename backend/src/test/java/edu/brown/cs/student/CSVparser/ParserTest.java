package edu.brown.cs.student.CSVparser;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.internal.junit.ArrayAsserts.assertArrayEquals;

import edu.brown.cs.student.rowCreation.FactoryFailureException;
import edu.brown.cs.student.star.Star;
import edu.brown.cs.student.star.StarCreator;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/** A test class for the Parser. */
public class ParserTest {

  /** Tests parsing a basic StringReader, with no headers, that uses the default row creator. */
  @Test
  public void TestDefaultStringReaderNoHeaders() throws IOException, FactoryFailureException {
    Reader stringCSV =
        new StringReader("marilyn,monroe,actress,blonde\n" + "frank,sinatra,singer,brun");
    Parser<List<String>> stringParser = new Parser<>(stringCSV, false);

    List<List<String>> expected = new ArrayList<>();
    List<String> line1 = new ArrayList<>(Arrays.asList("marilyn", "monroe", "actress", "blonde"));
    List<String> line2 = new ArrayList<>(Arrays.asList("frank", "sinatra", "singer", "brun"));
    expected.add(line1);
    expected.add(line2);

    assertTrue(stringParser.parse().equals(expected));
  }

  /**
   * Tests a basic FileReader for a CSV file that has headers, and the default row creator.
   *
   * @throws FileNotFoundException If the file can't be found.
   */
  @Test
  public void TestDefaultFileReaderHeaders() throws IOException, FactoryFailureException {
    Reader fileCSV = new FileReader("data/cataloniacrash.csv");
    Parser<List<String>> fileParser = new Parser<>(fileCSV, true);

    List<List<String>> expected =
        List.of(
            new ArrayList<>(Arrays.asList("Sunday", "13664")),
            new ArrayList<>(Arrays.asList("Monday", "17279")),
            new ArrayList<>(Arrays.asList("Tuesday", "17337")),
            new ArrayList<>(Arrays.asList("Wednesday", "17394")),
            new ArrayList<>(Arrays.asList("Thursday", "17954")),
            new ArrayList<>(Arrays.asList("Friday", "19147")),
            new ArrayList<>(Arrays.asList("Saturday", "15714")));

    String[] headers = {"Day of Week", "Number of Crashes"};
    assertTrue(fileParser.parse().equals(expected));
    assertArrayEquals(fileParser.getHeaders(), headers);
  }

  /**
   * Tests a StringReader with no headers, and a custom row creator that creates a sentence from the
   * rows of a CSV.
   */
  @Test
  public void TestCustomCreateNoHeaders() throws IOException, FactoryFailureException {
    Reader stringCSV =
        new StringReader(
            "I,am,an,egg\n" + "I,like,to,be,hard-boiled\n" + "Or,cooked,sunny side up");

    Parser<List<String>> parser = new Parser<>(stringCSV, false, new SentenceCreator());

    List<String> expected =
        List.of("I am an egg", "I like to be hard-boiled", "Or cooked sunny side up");

    assertEquals(parser.parse(), expected);
  }

  /**
   * Tests the parsing of the stardata.csv, and creates a Star object from each one.
   *
   * @throws FileNotFoundException
   */
  @Test
  public void TestCustomCreateHeaders() throws IOException, FactoryFailureException {
    Reader fileCSV = new FileReader("data/stars/stardata.csv");

    Parser<Star> parser = new Parser<>(fileCSV, true, new StarCreator());

    List<Star> stars = parser.parse();

    // Set up expected results.
    String[] headers = {"StarID", "ProperName", "X", "Y", "Z"};

    assertArrayEquals(headers, parser.getHeaders());

    assertEquals(stars.get(84403).getStarID(), 84403);
    assertEquals(stars.get(84403).getProperName(), "Xavier_12");
    assertEquals(stars.get(84403).getZ(), -168.12689);
  }
}
