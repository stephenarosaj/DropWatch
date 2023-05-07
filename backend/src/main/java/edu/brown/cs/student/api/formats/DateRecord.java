package edu.brown.cs.student.api.formats;

import com.squareup.moshi.Json;

/***
 * Record to hold date information (for Tracks and Albums).
 * REFERENCE: https://developer.spotify.com/documentation/web-api/reference/get-track
 * @param release_date "The date the album was first released."
 * @param release_date_precision "The precision with which release_date value is known."
 */
public record DateRecord(
        @Json(name = "release_date") String release_date,
        @Json(name = "release_date_precision") String release_date_precision) {

  /**
   * Comparison function for dates. Returns an int indicating which date is more recent, or 0 if they are the same.
   * If the precision are the same (for example, we have 1-2-3 and 4-5-6)
   *    - (> 0) if left MORE RECENT than right
   *    - (< 0) if left LESS RECENT than right
   *    - (0) if left == right
   * If the precisions are different, the date is filled in with 1's. For example, a date of "2023-04" (april 2023) becomes
   * "2023-04-1", and "2020" would become "2020-01-01"
   * @param left the first date to compare
   * @param right the second date to compare
   * @return int indicating which date is more recent. See above notes for explanation of returns!
   */
  public static int compareDates(DateRecord left, DateRecord right) {
    // error check inputs
    if (left == null || right == null
        || left.release_date == null || right.release_date == null
        || left.release_date_precision == null || right.release_date_precision == null) {
      // error!
      throw new NullPointerException("ERROR: called DateRecord.compareDates() with null values!");
    }

    // grab release dates
    String leftDate = left.release_date;
    String rightDate = right.release_date;

    // check and fix precisions
    switch (left.release_date_precision) {
      case "month" -> leftDate += "-01";
      case "year" -> leftDate += "-01-01";
      default -> {}
    }
    switch (right.release_date_precision) {
      case "month" -> rightDate += "-01";
      case "year" -> rightDate += "-01-01";
      default -> {}
    }

    // compare lexicographically!
    return left.release_date.compareTo(right.release_date);
  }
}
