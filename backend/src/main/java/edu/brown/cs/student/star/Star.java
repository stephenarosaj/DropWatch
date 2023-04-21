package edu.brown.cs.student.star;

/** A wrapper for the info in the stardata.csv rows, which represents a Star. */
public class Star {
  private Integer starID;
  private String properName;
  private Double x;
  private Double y;
  private Double z;

  /**
   * Constructs a Star.
   *
   * @param inputStarID String representing StarID
   * @param inputProperName String representing the star's name
   * @param inputX Double representing the star's x coordinate
   * @param inputY Double representing the star's y coordinate
   * @param inputZ Double representing the star's z coordinate
   */
  public Star(
      Integer inputStarID, String inputProperName, Double inputX, Double inputY, Double inputZ) {
    starID = inputStarID;
    properName = inputProperName;
    x = inputX;
    y = inputY;
    z = inputZ;
  }

  /**
   * Accessor for the Star's star ID.
   *
   * @return Integer star ID.
   */
  public int getStarID() {
    return starID;
  }

  /**
   * Accessor for the Star's proper name.
   *
   * @return String representing proper name.
   */
  public String getProperName() {
    return properName;
  }

  /**
   * Accessor for the Star's Z coordinate.
   *
   * @return Double representing Star's x coordinate.
   */
  public Double getZ() {
    return z;
  }
}
