package edu.brown.cs.student.CSVparser;

import edu.brown.cs.student.rowCreation.CreatorFromRow;
import edu.brown.cs.student.rowCreation.FactoryFailureException;
import java.util.ArrayList;
import java.util.List;

/** A CreatorFromRow class which creates a Sentence from each row of a CSV. */
public class SentenceCreator implements CreatorFromRow<String> {

  /** Creates a SentenceCreator object. */
  public SentenceCreator() {}

  /**
   * The create method that is called in the parser, which creates sentences from each CSV row.
   *
   * @param row A list of Strings representing the row of a CSV.
   * @return A String that is a sentence from the contents of a CSV row.
   * @throws FactoryFailureException
   */
  @Override
  public String create(List<String> row) throws FactoryFailureException {
    if (row == null) {
      throw new FactoryFailureException("Cannot pass in null row", new ArrayList<>());
    }
    String finalSentence = "";
    for (int i = 0; i < row.size(); i++) {
      if (i != 0) {
        finalSentence += " ";
      }
      finalSentence += row.get(i);
    }
    return finalSentence;
  }
}
