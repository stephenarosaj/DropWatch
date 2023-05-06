package edu.brown.cs.student.api.database;

import edu.brown.cs.student.api.formats.AlbumRecord;
import org.testng.internal.collections.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    // EXAMPLE:
    // user_id | artist_id
    //    1    |    JID
    //    1    |   Smino
    //    2    |    JID
    //    2    |   Tyler
    //    3    |   Smino
    //    3    |   Tyler
    //
    // tracking(user_id = 3) --> [Smino, Tyler]
    // tracking(artist_id = JID) --> [1, 2]

    // make the table in the db (if it doesn't exist)
    String trackingName = "tracking";
    String trackingSchema = "" +
      "user_id VARCHAR(100)," + // user_id - the unique spotify id of the user
      "artist_id VARCHAR(100)," + // artist_id - the unique spotify id of the artist
      "PRIMARY KEY (user_id, artist_id)"; // our primary key is the tuple of these two
    if (!db.tableExists(trackingName)) {
      db.createNewTable(trackingName, trackingSchema);
    }
    // update tables field
    this.tables.put(trackingName, trackingSchema);

    // this table is for keeping track of the latest release date of an artist
    // EXAMPLE:
    // artist_id |    date    | precision
    //    JID    |   2022-10  |   month
    //   Smino   | 2023-09-16 |    day
    //   Tyler   |    2021    |    year
    //
    // releaseDates(artist_id = JID) --> ["2022-10", "month"]
    // tracking(artist_id = JID) --> [1, 2]
    // make the table in the db (if it doesn't exist)
    String releaseDatesName = "releaseDates";
    String releaseDatesSchema = "" +
      "artist_id VARCHAR(100) PRIMARY KEY," + // artist_id - the unique spotify id of the artist - our primary key
      "date VARCHAR(10)," + // date - the latest release date for this artist
      "precision VARCHAR(5)"; // precision - the precision of the latest release date - year, month, or day
    if (!db.tableExists(releaseDatesName)) {
      db.createNewTable(releaseDatesName, releaseDatesSchema);
    }
    // update tables field
    this.tables.put(releaseDatesName, releaseDatesSchema);
  }

  /**
   * method that queries the db to see if a specific user follows a specific artist
   * @param user_id the user to look up in the db
   * @param artist_id the artist to look up in the db
   * @return boolean indicating if user follows artist
   */
  public boolean isUserTrackingArtist(String user_id, String artist_id) throws SQLException, ClassNotFoundException {
    // execute our query!
    String sqlQuery = "" +
      "SELECT count(*) FROM table WHERE user_id = \"" + user_id + "\" AND artist_id = \"" + artist_id + "\"";
    try (ResultSet result = db.executeSQLQuery(sqlQuery)) {
      // return whether there is a single entry of this user_id tracking this artist_id
      boolean ret = (result.next() && (result.getInt(1) == 1));
      // close results!
      result.close();
      return ret;
    }
  }

  /**
   * method that queries the db for one of two things:
   *  - the artists a user is tracking, returning their artist_ids as a list
   *  - the users tracking an artist, returning their user_ids as a list
   * @param id the artist/user to look up in the db
   * @param selectUsers indicates whether we're selecting users (true) or artists (false)
   * @return an array list of ids (empty if none)
   */
  public ArrayList<String> queryTracking(String id, boolean selectUsers) throws SQLException, ClassNotFoundException {
    // execute our query!
    String sqlQuery = "" +
      "SELECT " + (selectUsers ? "user_id" : "artist_id") + " FROM tracking" +
      "WHERE " + (selectUsers ? "artist_id" : "user_id") + " = \"" + id + "\";";
    try (ResultSet result = db.executeSQLQuery(sqlQuery)) {
      // collect the results as a list
      ArrayList<String> ret = new ArrayList<>();
      while (result.next()) {
        ret.add(result.getString(1));
      }
      // return our results!
      result.close();
      return ret;
    }
  }

  /**
   * method that inserts a new (user_id, artist_id) pair into the tracking table!
   * NOTE: since the primary key of the tracking table is a tuple (user_id, artist_id),
   * if you try to enter the same pair, an error will occur
   * @param user_id the user who is tracking the artist
   * @param artist_id the artist who is being tracked
   * @return an array list of artist_ids that the user is tracking (empty if none)
   */
  public boolean insertTracking(String user_id, String artist_id) throws SQLException, ClassNotFoundException {
    // make our statement!
    String SQLStatement = "" +
      "INSERT INTO tracking tracking VALUES (" +
      "\"" + user_id + "\", \"" + artist_id + "\");";

    // execute our statement!
    Pair<Boolean, Statement> ret;
    try (Statement statement = (ret = db.executeSQLStatement(SQLStatement)).second()) {
      if (ret.first() || (statement.getUpdateCount() != 1)) {
        // result set returned or wrong number for updateCount - ERROR!
        throw new SQLException("ERROR: Insert into DropWatchDB tracking table ('" + user_id + "', '" + artist_id + "') FAILED");
      }
      // executed successfully
      return true;
    }
  }

  /**
   * method that removes a (user_id, artist_id) pair from the tracking table!
   * @param user_id the user who is tracking the artist
   * @param artist_id the artist who is being tracked
   * @return boolean indicating successful removal
   */
  public boolean removeTracking(String user_id, String artist_id) throws SQLException, ClassNotFoundException {
    // TODO
    return false;
  }

  /**
   * method to insert data for an artist's latest release date
   * @param artist_id the artist_id whose data we're adding
   * @param date their latest release date
   * @param precision the precision of their latest release date - "day", "month", or "year"
   * @return a boolean indicating success or failure
   */
  public boolean insertLatestRelease(String artist_id, String date, String precision) throws SQLException, ClassNotFoundException {
    // make our statement!
    String SQLStatement = "" +
      "INSERT INTO recentDrops VALUES (" +
      "\"" + artist_id + "\", \"" + date + "\", \"" + precision + "\");";

    // execute our statement!
    Pair<Boolean, Statement> ret;
    try (Statement statement = (ret = db.executeSQLStatement(SQLStatement)).second()) {
      if (ret.first() || (statement.getUpdateCount() != 1)) {
        // result set returned or wrong number for updateCount - ERROR!
        throw new SQLException("ERROR: Insert into DropWatchDB tracking table ('" + artist_id + "', '" + date + "', '" + precision + "') FAILED");
      }
      // executed successfully
      return true;
    }
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

  public boolean insertRecentDrops() throws SQLException, ClassNotFoundException {
    // TODO
    return false;
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
}
