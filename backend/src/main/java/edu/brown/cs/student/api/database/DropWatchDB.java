package edu.brown.cs.student.api.database;

import edu.brown.cs.student.api.formats.AlbumRecord;

import java.util.ArrayList;

/**
 * Class that is the database controller for our actual database. It's like
 * a wrapper around the sqliteDB class!
 * This database will store information on what artists are being
 * tracked by which users, what artists' most recent release dates are, and more!
 */
public class DropWatchDB {

  /**
   * The actual sqliteDB Object that this class wraps - the real connection to
   * the database!
   */
  private sqliteDB db;

  /**
   * Constructor for this class!
   */
  public DropWatchDB() {}

  /**
   * method that queries the db for the artists a user is tracking, returning
   * their artist_ids as a list!
   * @param user the user whose tracked artists we want to query from the db
   * @return an array list of artist_ids that the user is tracking (empty if none)
   */
  public ArrayList<String> queryTracking(String user) {
    // TODO
    return new ArrayList<String>();
  }

  /**
   * Returns a string (SHOULD IT BE A STRING?) representing the latest release
   * date stored on the db for the artist given by artist_id
   * @param artist_id the artist whose latest release date we want to check
   * @return a string representing the stored latest release date for the artist
   */
  public String queryLatestRelease(String artist_id) {
    // TODO
    return null;
  }

  /**
   * method that updates an artist's latest release date!
   * @param artist_id the artist whose latest release date needs to be updated!
   * @param newDate the new latest release date
   * @return a list where the first value is the old release date and the second
   * is the newest release date
   */
  public ArrayList<String> updateLatestRelease(String artist_id, String newDate) {
    // TODO
    return new ArrayList<String>();
  }
}
