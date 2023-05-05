package edu.brown.cs.student.api.database;

import edu.brown.cs.student.api.formats.AlbumRecord;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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
  private sqliteDB db = new sqliteDB();

  /**
   * The relative filepath of the database we are connecting to (during use,
   * should always be "data/DropWatchDB.db", but during testing this will change)
   */
  private String filepath;

  /**
   * tables map! maps table names in the DB to their schema!
   */
  public HashMap<String, String> tables;

  /**
   * Constructor for this class!
   */
  public DropWatchDB(String filepath) throws SQLException, ClassNotFoundException {
    // set up fields and connect to db
    this.filepath = filepath;
    this.db.connectDB(this.filepath);

    // setup!
    initializeDB();
  }

  /**
   * method to initialize our DB with the right tables
   */
  private void initializeDB() throws ClassNotFoundException, SQLException {
    // CREATE TABLES!
    // this table is for keeping track of the artists a user tracks, and the
    // users tracking an artist!
    String trackingName = "tracking";
    String trackingSchema = """
      user_id VARCHAR(80),
      artist_id VARCHAR(80)
      """;
    if (!db.tableExists(trackingName)) {
      db.createNewTable(trackingName, trackingSchema);
    }
    //TODO: pickup here - more tables

    this.tables.put(trackingName, trackingSchema);
  }

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
   * Method that queries db for the recent drops for a user
   * @param user the user whose recent drops we want to query
   * @return a map of (artist_id, AlbumRecord[]) pairs which are the recent drops
   */
  public HashMap<String, ArrayList<AlbumRecord>> queryRecentDrops(String user) {
    // TODO
    return new HashMap<String, ArrayList<AlbumRecord>>();
  }

  /**
   * Method that updates recent drops for a user in the db
   * @param user the user whose recent drops we want to query
   * @return a map of (artist_id, AlbumRecord[]) pairs which are the recent drops
   */
  public HashMap<String, ArrayList<AlbumRecord>> updateRecentDrops(String user) {
    // TODO
    return new HashMap<String, ArrayList<AlbumRecord>>();
  }

  /**
   * Returns a string (SHOULD IT BE A STRING?) representing the latest release
   * date stored on the db for the artist given by artist_id
   * @param artist_id the artist whose latest release date we want to check
   * @return an array of 2 strings representing the stored latest release date for the artist
   * and that date's precision
   */
  public String[] queryLatestRelease(String artist_id) {
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
