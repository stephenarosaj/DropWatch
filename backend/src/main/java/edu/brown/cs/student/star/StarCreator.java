package edu.brown.cs.student.star;

import edu.brown.cs.student.rowCreation.CreatorFromRow;
import edu.brown.cs.student.rowCreation.FactoryFailureException;
import java.util.ArrayList;
import java.util.List;

/** Helper class that creates Stars in the parser. */
public class StarCreator implements CreatorFromRow<Star> {

  /** Constructs a StarCreator. */
  public StarCreator() {}

  /**
   * A method inherited from the CreatorFromRow interface. It creates a Star with the info given in
   * the stardata.csv row.
   *
   * @param row A row of strings representing star data.
   * @return A Star Object that wraps the star data.
   * @throws FactoryFailureException If a null row is passed in.
   */
  @Override
  public Star create(List<String> row) throws FactoryFailureException {
    if (row == null) {
      throw new FactoryFailureException(
          "Attempted to pass null row to star creation", new ArrayList<>());
    }
    int starID = Integer.valueOf(row.get(0));
    Double x = Double.valueOf(row.get(2));
    Double y = Double.valueOf(row.get(3));
    Double z = Double.valueOf(row.get(4));
    return new Star(starID, row.get(1), x, y, z);
  }
}
