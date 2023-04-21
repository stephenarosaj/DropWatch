package edu.brown.cs.student.api.weatherHelpers;

/** Class that wraps the latitude and longitude coordinates of a weather request. */
public class Coordinates {
  private double lat;
  private double lon;
  private static double dist;

  /**
   * Constructs new coordinates.
   *
   * @param lat Latitude of coordinates.
   * @param lon Longtiude of coordinates.
   * @param dist Max distance between two coordinates for them to be considered "equal".
   */
  public Coordinates(double lat, double lon, double dist) {
    this.lat = lat;
    this.lon = lon;
    this.dist = dist;
  }

  /**
   * Accessor for longitude.
   *
   * @return Longitude double.
   */
  public double getLon() {
    return this.lon;
  }

  /**
   * Accessor for latitude.
   *
   * @return Latitude double.
   */
  public double getLat() {
    return this.lat;
  }

  /**
   * Determines whether two coordinates are equal by doing the Pythagorean theorem and comparing
   * their distance against a max distance.
   *
   * @param newLocation Coordinates to be compared with.
   * @return Boolean representing whether these coordinates are "equal" / close enough to be
   *     considered equal.
   */
  @Override
  public boolean equals(Object newLocation) {
    if (newLocation.getClass() != Coordinates.class) {
      return false;
    }
    Coordinates newKey = (Coordinates) newLocation;
    double distance =
        Math.sqrt(
            Math.pow(this.lat - newKey.getLat(), 2) + Math.pow(this.lon - newKey.getLon(), 2));
    return distance < dist;
  }

  /**
   * Returns the int to use when hashing this object as a key in a map. Since two Coordinates should
   * hash to the same thing when their lat/long is the same, it really returns the hashCode of a
   * string representing their lat/long.
   *
   * @return hashCode for this object.
   */
  @Override
  public int hashCode() {
    return (Double.toString(lat) + "," + Double.toString(lon)).hashCode();
  }
}
