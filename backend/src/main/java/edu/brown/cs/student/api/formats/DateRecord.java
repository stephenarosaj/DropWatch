package edu.brown.cs.student.api.formats;

import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

  /**
   * method that filters down a list of artistDrops to the n most recent ones!
   * @param artistDrops the drops of an artist to filter down - list of lists,
   *                   where each inner list looks like:
   *                   [first image, name of artist, name of album, album type, link, releaseDate, precision]
   * @param n how many drops we want to return from this artist, max
   * @return the (up to n) most recent drops from this artist
   */
  public static ArrayList<ArrayList<String>> filterMostRecent(ArrayList<ArrayList<String>> artistDrops, int n) {
    // define a custom comparator to compare the release dates in the inner lists
    Comparator<ArrayList<String>> dropComparator = new Comparator<ArrayList<String>>() {
      @Override
      public int compare(ArrayList<String> list1, ArrayList<String> list2) {
        // lists are [first image, name of artist, name of album, album type, link, releaseDate, precision]
        DateRecord left = new DateRecord(list1.get(5), list1.get(6));
        DateRecord right = new DateRecord(list2.get(5), list2.get(6));
        return DateRecord.compareDates(left, right);
      }
    };

    // Sort the albums list using the custom comparator
    artistDrops.sort(dropComparator);

    // grab only the 4 most recent items!
    return new ArrayList<>(artistDrops.subList(0, 4));
  }
}
