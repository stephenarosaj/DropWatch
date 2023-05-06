package edu.brown.cs.student.api.database;
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
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not connect to DB
   */
  public DropWatchDB(String filepath) throws SQLException, ClassNotFoundException {
    // set up fields and connect to db
    this.filepath = filepath;
    this.db.connectDB(this.filepath);
    this.tables = new HashMap<>();

    // setup!
    initializeDB();
    this.db.commit();
  }

  /**
   * method to initialize our DB with the right tables
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not create tables/check for them
   */
  private void initializeDB() throws ClassNotFoundException, SQLException {
    // CREATE TABLES!
    createTracking();
    createArtists();
    createAlbums();
    createArtistAlbums();
  }

  /**
   * Helper to create the tracking table. this table is for keeping track of the art
   * users tracking an artist!
   * EXAMPLE:
   *      tracking
   * user_id | artist_id
   *    1    |    JID
   *    1    |   Smino
   *    2    |    JID
   *    2    |   Tyler
   *    3    |   Smino
   *    3    |   Tyler
   * tracking(user_id = 3) --> [Smino, Tyler]
   * tracking(artist_id = JID) --> [1, 2]
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not check for/create tracking table
   */
  private void createTracking() throws SQLException, ClassNotFoundException {
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
  }

  /**
   * Helper to create the artists table. this table is for keeping track of artists,
   * EXAMPLE:
   *                  artists
   * artist_id |    date    | precision | link
   *    JID    |   2022-10  |   month   |  a
   *   Smino   | 2023-09-16 |    day    |  b
   *   Tyler   |    2021    |    year   |  c
   * artists(artist_id = JID) --> (2022-10, month, a)
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not check for/create tracking table
   */
  private void createArtists() throws SQLException, ClassNotFoundException {
    // make the table in the db (if it doesn't exist)
    String latestReleaseName = "artists";
    String latestReleaseSchema = "" +
      "artist_id VARCHAR(100) PRIMARY KEY," + // artist_id - the unique spotify id of the artist - our primary key
      "date VARCHAR(10)," + // date - the latest release date for this artist
      "precision VARCHAR(5)," + // precision - the precision of the latest release date - year, month, or day
      "link VARCHAR(100)"; // link - the link to grab more data on an artist
    if (!db.tableExists(latestReleaseName)) {
      db.createNewTable(latestReleaseName, latestReleaseSchema);
    }
    // update tables field
    this.tables.put(latestReleaseName, latestReleaseSchema);
  }

  /**
   * Helper to create the tracking table. this table is for keeping track of albums
   * artists release
   * EXAMPLE:
   *                    albums
   * album_id | release_date | precision | link
   *    1     |  2020-10-06  |    day    |  a
   *    2     |     04-05    |   month   |  b
   *    3     |     2021     |   year    |  c
   * albums(album_id = 1) --> (2020-10-06, day, a)
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not check for/create tracking table
   */
  private void createAlbums() throws SQLException, ClassNotFoundException {
    // make the table in the db (if it doesn't exist)
    String albumsName = "albums";
    String albumsSchema = "" +
      "album_id VARCHAR(100) PRIMARY KEY," + // album_id - the unique spotify id of the album
      "date VARCHAR(10)," + // date - the latest release date for this artist
      "precision VARCHAR(5)," + // precision - the precision of the latest release date - year, month, or day
      "link VARCHAR(100)"; // link - the link to get more info about an album
    if (!db.tableExists(albumsName)) {
      db.createNewTable(albumsName, albumsSchema);
    }
    // update tables field
    this.tables.put(albumsName, albumsSchema);
  }

  /**
   * Helper to create the artistAlbums table. this table is for keeping track of
   * the relationship between artists and albums (releasing albums)
   * EXAMPLE:
   *     artistAlbums
   * artist_id | album_id
   *     4     |    1
   *     5     |    2
   *     6     |    3
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not check for/create tracking table
   */
  private void createArtistAlbums() throws SQLException, ClassNotFoundException {
    // make the table in the db (if it doesn't exist)
    String artistAlbumsName = "artistAlbums";
    String artistAlbumsSchema = "" +
      "artist_id VARCHAR(100)," + // artist_id - the unique spotify id of the artist
      "album_id VARCHAR(100)," + // album_id - the unique spotify id of the album
      "PRIMARY KEY (artist_id, album_id)," + // primary key is tuple of columns
      "FOREIGN KEY artist_id REFERENCES artists(artist_id)," + // ids are from other tables!
      "FOREIGN KEY album_id REFERENCES albums(album_id)"; // ids are from other tables!
    if (!db.tableExists(artistAlbumsName)) {
      db.createNewTable(artistAlbumsName, artistAlbumsSchema);
    }
    // update tables field
    this.tables.put(artistAlbumsName, artistAlbumsSchema);
  }

  /**
   * method that queries the db to see if a specific user follows a specific artist
   * @param user_id the user to look up in the db
   * @param artist_id the artist to look up in the db
   * @return boolean indicating if user follows artist
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not check if user is tracking artist
   */
  public boolean isUserTrackingArtist(String user_id, String artist_id) throws SQLException, ClassNotFoundException {
    // execute our query!
    String sqlQuery = "" +
      "SELECT count(*) FROM table WHERE user_id = \"" + user_id + "\" AND artist_id = \"" + artist_id + "\"";
    try (ResultSet result = db.executeSQLQuery(sqlQuery)) {
      // return whether there is a single entry of this user_id tracking this artist_id
      boolean ret = result.next();
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
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not query tracking table
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
   * method that adds a new (user_id, artist_id) pair into the tracking table!
   * NOTE: since the primary key of the tracking table is a tuple (user_id, artist_id),
   * if you try to enter the same pair, an error will occur
   * @param user_id the user who is tracking the artist
   * @param artist_id the artist who is being tracked
   * @return an array list of artist_ids that the user is tracking (empty if none)
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not insert into tracking table
   */
  public boolean addTracking(String user_id, String artist_id) throws SQLException, ClassNotFoundException {
    // make our statement!
    String SQLStatement = "" +
      "INSERT INTO tracking VALUES (" +
      "\"" + user_id + "\", \"" + artist_id + "\");";

    // execute our statement!
    Pair<Boolean, Statement> ret;
    try (Statement statement = (ret = db.executeSQLStatement(SQLStatement)).second()) {
      if (ret.first() || (statement.getUpdateCount() != 1)) {
        // result set returned or wrong number for updateCount - ERROR!
        statement.close();
        throw new SQLException("ERROR: Insert into DropWatchDB tracking table ('" + user_id + "', '" + artist_id + "') FAILED");
      }
      // executed successfully
      statement.close();
      return true;
    }
  }

  /**
   * method that removes a (user_id, artist_id) row from the tracking table!
   * @param user_id the user who is tracking the artist
   * @param artist_id the artist who is being tracked
   * @return boolean indicating successful removal - true means it was present and removed,
   *         false means it was not present and thus not removed
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not delete row from tracking table
   */
  public boolean removeTracking(String user_id, String artist_id) throws SQLException, ClassNotFoundException {
    // check that this entry is even actually there
    if (!isUserTrackingArtist(user_id, artist_id)) {
      return false;
    }

    // make our statement!
    String SQLStatement = "" +
      "DELETE FROM tracking WHERE " +
      "user_id = \"" + user_id + "\" AND artist_id = \"" + artist_id + "\";";

    // execute our statement!
    Pair<Boolean, Statement> ret;
    try (Statement statement = (ret = db.executeSQLStatement(SQLStatement)).second()) {
      if (ret.first() || (statement.getUpdateCount() != 1)) {
        // result set returned or wrong number for updateCount - ERROR!
        statement.close();
        throw new SQLException("ERROR: Delete from DropWatchDB tracking table ('" + user_id + "', '" + artist_id + "') FAILED");
      }
      // executed successfully
      statement.close();
      return true;
    }
  }

  /**
   * method that queries the db for albums by searching either album_id (one result) or artist_id (up to 4 results)
   * @param id the album_id/artist_id to look up in the db
   * @param isAlbum_id indicates whether we're selecting 1 album (true) or 4 (false)
   * @return an array album links (empty if none)
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not query tracking table
   */
  public ArrayList<String> queryAlbums(String id, boolean isAlbum_id) throws SQLException, ClassNotFoundException {
    // execute our query!
    String oneAlbum = "= \"" + id + "\"";
    String fourAlbum = "IN (SELECT album_id FROM artistAlbums WHERE artist_id = \"" + id + "\")";
    String sqlQuery = "" +
      "SELECT release_date, precision, link FROM albums" +
      "WHERE album_id " + (isAlbum_id ? oneAlbum : fourAlbum) + ";";
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
   * method to update the albums table
   * @param album_id the album_id of the album whose data we're adding
   * @param date the release date
   * @param precision the precision of the release date - "day", "month", or "year"
   * @param link the link to get more info about the album
   * @return a boolean indicating success or failure
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not insert into latest release table
   */
  public boolean insertOrReplaceAlbums(String album_id, String date, String precision, String link) throws SQLException, ClassNotFoundException {
    // make our statement!
    String SQLStatement = "" +
      "INSERT OR REPLACE INTO albums VALUES (" +
      "\"" + album_id + "\", \"" + date + "\", \"" + precision + "\"" + link + "\");";

    // execute our statement!
    Pair<Boolean, Statement> ret;
    try (Statement statement = (ret = db.executeSQLStatement(SQLStatement)).second()) {
      if (ret.first() || (statement.getUpdateCount() != 1)) {
        // result set returned or wrong number for updateCount - ERROR!
        statement.close();
        throw new SQLException("ERROR: Insert into DropWatchDB albums table ('" + album_id + "', '" + date + "', '" + precision + "', " + link + "') FAILED");
      }
      // executed successfully
      statement.close();
      return true;
    }
  }

  // add new album to albums and artistAlbums tables
  public String[] findLatestRelease(String artist_id) {
    String[] ret = new String[3];
    ret[0] = null;
    ret[1] = null;
    ret[2] = null;
    return ret;
  }

  // add new album to albums and artistAlbums tables
  public void addNewAlbum(String artist_id, String album_id, String newDate, String newDatePrecision, String link) {
    return;
  }


  // TODO: MORE!?
  // tracking done!
  // insertOrRepalce albums - need to keep only 4 at a time, also update artistAlbums
  // query artistAlbums
  // insertOrReplace artistAlbums

  // findLatestRelease - use these tables to calculate latest release date for an artist
  // addNewAlbum - use these tables to calculate latest release date for an artist
}
