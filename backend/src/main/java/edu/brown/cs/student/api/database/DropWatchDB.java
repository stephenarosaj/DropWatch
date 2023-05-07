package edu.brown.cs.student.api.database;
import edu.brown.cs.student.api.formats.ArtistRecord;
import edu.brown.cs.student.api.formats.DateRecord;
import org.testng.internal.collections.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
   * Method to close our DB connection!
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not commit :(
   */
  public boolean closeDB() throws SQLException, ClassNotFoundException {
    return this.db.closeDB();
  }

  /**
   * Method to check our DB's conn!
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not commit :(
   */
  public boolean connIsNull() throws SQLException, ClassNotFoundException {
    return this.db.connIsNull();
  }

  /**
   * Method to commit our DB's changes!
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not commit :(
   */
  public boolean commit() throws SQLException, ClassNotFoundException {
    return this.db.commit();
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
   *      artists
   * artist_id | link
   *    JID    |  a
   *   Smino   |  b
   *   Tyler   |  c
   * artists(artist_id = JID) --> (2022-10, month, a)
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not check for/create tracking table
   */
  private void createArtists() throws SQLException, ClassNotFoundException {
    // make the table in the db (if it doesn't exist)
    String artistsName = "artists";
    String artistsSchema = "" +
      "artist_id VARCHAR(100) PRIMARY KEY," + // artist_id - the unique spotify id of the artist - our primary key
      "link VARCHAR(100)"; // link - the link to grab more data on an artist
    if (!db.tableExists(artistsName)) {
      db.createNewTable(artistsName, artistsSchema);
    }
    // update tables field
    this.tables.put(artistsName, artistsSchema);
  }

  /**
   * Helper to create the albums table. this table is for keeping track of albums
   * artists release
   * EXAMPLE:
   *                    albums
   * album_id | releaseDate | precision | link
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
      "releaseDate VARCHAR(10)," + // releaseDate - the latest release date for this artist
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
      "FOREIGN KEY (artist_id) REFERENCES artists(artist_id)," + // ids are from other tables!
      "FOREIGN KEY (album_id) REFERENCES albums(album_id)"; // ids are from other tables!
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
      "SELECT * FROM tracking WHERE user_id = \"" + user_id + "\" AND artist_id = \"" + artist_id + "\"";
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
   * @param isUser_id indicates whether we're selecting users (true) or artists (false)
   * @return an array list of ids (empty if none)
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not query tracking table
   */
  public ArrayList<String> queryTracking(String id, boolean isUser_id) throws SQLException, ClassNotFoundException {
    // execute our query!
    String sqlQuery = "" +
      "SELECT " + (isUser_id ? "artist_id" : "user_id") + " FROM tracking " +
      "WHERE " + (isUser_id ? "user_id" : "artist_id") + " = \"" + id + "\";";
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
   * method that queries the db for albums by searching album_id
   * @param album_id the album_id to look up in the db
   * @return an arrayList of (releaseDate, precision, link) lists (empty if none)
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not query tracking table
   */
  public ArrayList<String[]> queryAlbums(String album_id) throws SQLException, ClassNotFoundException {
    // execute our query!
    String sqlQuery = "" +
      "SELECT releaseDate, precision, link FROM albums " +
      "WHERE album_id  = \"" + album_id + "\";";
    try (ResultSet result = db.executeSQLQuery(sqlQuery)) {
      // collect the results as a list
      ArrayList<String[]> ret = new ArrayList<>();
      while (result.next()) {
        String[] arr = new String[3];
        arr[0] = result.getString(1);
        arr[1] = result.getString(2);
        arr[2] = result.getString(3);
        ret.add(arr);
      }
      // return our results!
      result.close();
      return ret;
    }
  }

  /**
   * method that removes an album row from the albums table!
   * @param album_id the album we want to remove
   * @return boolean indicating successful removal - true means it was present and removed,
   *         false means it was not present and thus not removed
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not delete album from albums table
   */
  public boolean removeAlbums(String album_id) throws SQLException, ClassNotFoundException {
    // make sure entry exists
    if (queryAlbums(album_id).size() == 0) {
      return false;
    }

    // make our statement!
    String SQLStatement = "" +
      "DELETE FROM albums WHERE " +
      "album_id = \"" + album_id + "\";";

    // execute our statement!
    Pair<Boolean, Statement> ret;
    try (Statement statement = (ret = db.executeSQLStatement(SQLStatement)).second()) {
      if (ret.first() || (statement.getUpdateCount() != 1)) {
        // result set returned or wrong number for updateCount - ERROR!
        statement.close();
        throw new SQLException("ERROR: Delete from DropWatchDB albums table '" + album_id + "' FAILED");
      }
      // executed successfully
      statement.close();
      return true;
    }
  }

  /**
   * method to update the albums table
   * @param album_id the album_id of the album whose data we're adding
   * @param releaseDate the release date
   * @param precision the precision of the release date - "day", "month", or "year"
   * @param link the link to get more info about the album
   * @return a boolean indicating success or failure
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not insert into latest release table
   */
  public boolean insertOrReplaceAlbums(String album_id, String releaseDate, String precision, String link) throws SQLException, ClassNotFoundException {
    // make our statement!
    String SQLStatement = "" +
      "INSERT OR REPLACE INTO albums VALUES (" +
      "\"" + album_id + "\", \"" + releaseDate + "\", \"" + precision + "\", \"" + link + "\");";

    // execute our statement!
    Pair<Boolean, Statement> ret;
    try (Statement statement = (ret = db.executeSQLStatement(SQLStatement)).second()) {
      if (ret.first() || (statement.getUpdateCount() != 1)) {
        // result set returned or wrong number for updateCount - ERROR!
        statement.close();
        throw new SQLException("ERROR: Insert into DropWatchDB albums table ('" + album_id + "', '" + releaseDate + "', '" + precision + "', " + link + "') FAILED");
      }
      // executed successfully
      statement.close();
      return true;
    }
  }

  /**
   * method that queries the db for artists by searching artist_id
   * @param artist_id the artist_id to look up in the db
   * @return the link of this artist (null if none)
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not query artists table
   */
  public String queryArtists(String artist_id) throws SQLException, ClassNotFoundException {
    // execute our query!
    String sqlQuery = "" +
      "SELECT link FROM artists " +
      "WHERE artist_id  = \"" + artist_id + "\";";
    try (ResultSet result = db.executeSQLQuery(sqlQuery)) {
      // return result link, or null if not exist
      if (!result.next()) {
        result.close();
        return null;
      }
      String ret = result.getString(1);
      // return our results!
      result.close();
      return ret;
    }
  }

  /**
   * method that removes an artist row from the artists table!
   * @param artist_id the artist we want to remove
   * @return boolean indicating successful removal - true means it was present and removed,
   *         false means it was not present and thus not removed
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not delete artist from artists table
   */
  public boolean removeArtists(String artist_id) throws SQLException, ClassNotFoundException {
    // make sure entry exists
    if (queryArtists(artist_id) == null) {
      return false;
    }

    // make our statement!
    String SQLStatement = "" +
      "DELETE FROM artists WHERE " +
      "artist_id = \"" + artist_id + "\";";

    // execute our statement!
    Pair<Boolean, Statement> ret;
    try (Statement statement = (ret = db.executeSQLStatement(SQLStatement)).second()) {
      if (ret.first() || (statement.getUpdateCount() != 1)) {
        // result set returned or wrong number for updateCount - ERROR!
        statement.close();
        throw new SQLException("ERROR: Delete from DropWatchDB artists table '" + artist_id + "' FAILED");
      }
      // executed successfully
      statement.close();
      return true;
    }
  }

  /**
   * method to update the artists table
   * @param artist_id the artist_id of the artist whose data we're adding
   * @param link the link to get more info about the artist
   * @return a boolean indicating success or failure
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not insert into latest release table
   */
  public boolean insertOrReplaceArtists(String artist_id, String link) throws SQLException, ClassNotFoundException {
    // make our statement!
    String SQLStatement = "" +
      "INSERT OR REPLACE INTO artists VALUES (" +
      "\"" + artist_id + "\", \"" + link + "\");";

    // execute our statement!
    Pair<Boolean, Statement> ret;
    try (Statement statement = (ret = db.executeSQLStatement(SQLStatement)).second()) {
      if (ret.first() || (statement.getUpdateCount() != 1)) {
        // result set returned or wrong number for updateCount - ERROR!
        statement.close();
        throw new SQLException("ERROR: Insert into DropWatchDB artists table ('" + artist_id + "', '" + link + "') FAILED");
      }
      // executed successfully
      statement.close();
      return true;
    }
  }

  /**
   * method that queries the db for multiple artist-album relationships by searching for one artist_id/album_id
   * @param id the artist_id/album_id to look up in the db
   * @param isArtist_id indicates whether id is an artist_id or not (if not, it's album_id)
   * @return an arrayList of album_ids/artist_ids (empty if none)
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not query artists table
   */
  public ArrayList<String> queryArtistAlbums(String id, boolean isArtist_id) throws SQLException, ClassNotFoundException {
    // execute our query!
    String sqlQuery = "" +
      "SELECT " + (isArtist_id ? "album_id" : "artist_id") + " FROM artistAlbums " +
      "WHERE " + (isArtist_id ? "artist_id" : "album_id") + "  = \"" + id + "\";";
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
   * method that queries the db for one artist-album relationship by searching for an (artist_id, album_id) pair
   * @param artist_id the artist_id to look up in the DB
   * @param album_id the album_id to look up in the DB
   * @return an arrayList of album_ids/artist_ids (empty if none)
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not query artists table
   */
  public boolean artistAlbumRelationshipExists(String artist_id, String album_id) throws SQLException, ClassNotFoundException {
    // execute our query!
    String sqlQuery = "" +
      "SELECT * FROM artistAlbums " +
      "WHERE artist_id = \"" + artist_id + "\" AND album_id = \"" + album_id + "\";";
    try (ResultSet result = db.executeSQLQuery(sqlQuery)) {
      // collect the results as a list
      boolean ret = result.next();
      // return our results!
      result.close();
      return ret;
    }
  }

  /**
   * method that removes an artist-album relationship from the db (by pair)
   * @param artist_id the artist_id to remove
   * @param album_id the album_id to remove
   * @return boolean indicating successful removal - true means it was present and removed,
   *         false means it was not present and thus not removed
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not delete artist from artists table
   */
  public boolean removeArtistAlbums(String artist_id, String album_id) throws SQLException, ClassNotFoundException {
    // make sure entry exists
    if (!artistAlbumRelationshipExists(artist_id, album_id)) {
      return false;
    }

    // make our statement!
    String SQLStatement = "" +
      "DELETE FROM albumArtists WHERE " +
      "artist_id = \"" + artist_id + "\" AND album_id = \"" + album_id + "\";";

    // execute our statement!
    Pair<Boolean, Statement> ret;
    try (Statement statement = (ret = db.executeSQLStatement(SQLStatement)).second()) {
      if (ret.first() || (statement.getUpdateCount() != 1)) {
        // result set returned or wrong number for updateCount - ERROR!
        statement.close();
        throw new SQLException("ERROR: Delete from DropWatchDB artistAlbums table ('" + artist_id + "', '" + album_id + "') FAILED");
      }
      // executed successfully
      statement.close();
      return true;
    }
  }

  /**
   * method to update the artistAlbums table
   *
   * @param artist_id the artist_id of the artist whose data we're adding
   * @param album_id  the album_id of the album whose data we're adding
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException           if could not insert into latest release table
   */
  public void insertOrReplaceArtistAlbums(String artist_id, String album_id) throws SQLException, ClassNotFoundException {
    // make our statement!
    String SQLStatement = "" +
      "INSERT OR REPLACE INTO artistAlbums VALUES (" +
      "\"" + artist_id + "\", \"" + album_id + "\");";

    // execute our statement!
    Pair<Boolean, Statement> ret;
    try (Statement statement = (ret = db.executeSQLStatement(SQLStatement)).second()) {
      if (ret.first() || (statement.getUpdateCount() != 1)) {
        // result set returned or wrong number for updateCount - ERROR!
        statement.close();
        throw new SQLException("ERROR: Insert into DropWatchDB artistAlbums table ('" + artist_id + "', '" + album_id + "') FAILED");
      }
      // executed successfully
      statement.close();
    }
  }

  /**
   * method to find latest release date of an artist
   * @param artist_id the artist whose latest release date we want to find
   * @return a DateRecord containing the latest release date for thar artist
   */
  public DateRecord findLatestRelease(String artist_id) throws SQLException, ClassNotFoundException {
    // grab all album_ids of artist
    ArrayList<String> album_ids = queryArtistAlbums(artist_id, true);
    // if we have no albums, return null
    if (album_ids.size() == 0) {
      return null;
    }

    // grab all albums given by album_ids - [releaseDate, precision, link]
    ArrayList<String[]> albums = new ArrayList<>();
    for (String album_id: album_ids) {
      albums.addAll(queryAlbums(album_id));
    }

    // hold the latest date in DateRecord!
    DateRecord latestDate = new DateRecord("0000", "year");
    for (String[] album: albums) {
      // make new DateRecord
      DateRecord albumDate = new DateRecord(album[0], album[1]);
      // compare dates!
      if (DateRecord.compareDates(albumDate, latestDate) > 0) {
        // if newer, albumDate is new latestDate!
        latestDate = albumDate;
      }
    }
    return latestDate;
  }

  /**
   * a method to add a new album to our database. this means adding it to our
   * albums table and to our artistAlbums table
   * @param artist_ids the artists who are on the album
   * @param album_id the album_id of the album
   * @param releaseDate the date the album was released
   * @param releaseDatePrecision the precision of the releaseDate ("day", "month", or "year")
   * @param link a link to get more data about the album
   */
  public void addNewAlbum(List<ArtistRecord> artist_ids, String album_id, String releaseDate, String releaseDatePrecision, String link) throws SQLException, ClassNotFoundException {
    // add the album to our db!
    insertOrReplaceAlbums(album_id, releaseDate, releaseDatePrecision, link);
    for (ArtistRecord artist: artist_ids) {
      // for each artist:
      // add the artist to our db if it's not already there
      insertOrReplaceArtists(artist.id(), artist.href());
      // add the artist to our relationship table if it's not already there
      insertOrReplaceArtistAlbums(artist.id(), album_id);
    }
  }

  // TODO: MORE!?
  // addNewAlbum - add an album and update all tables accordingly
  //   - should get called on 4 most recent albums whenever we track a new artist
  //   - should get called when we run checkreleases and see new releases
}
