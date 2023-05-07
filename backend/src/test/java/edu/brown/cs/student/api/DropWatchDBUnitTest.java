package edu.brown.cs.student.api;

import edu.brown.cs.student.api.database.DropWatchDB;
import edu.brown.cs.student.api.database.sqliteDB;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DropWatchDB!
 */
public class DropWatchDBUnitTest {
  /**
   * The Database Object we use for testing!
   */
  static DropWatchDB db = null;

  /**
   * Path to our testing DBs!
   */
  static String testDWDBFilepath = "src/test/java/edu/brown/cs/student/api/testDWDB.db";

  /**
   *   this one is DB with some mock data:
   * albums: album_id | releaseDate | precision |  link
   *            1     | 2002-02-09  |    day    | rosa.com
   *            2     | 2002-03-21  |    day    | bry.com
   *            3     | 2002-07     |   month   | aku.com
   *            4     | 2002        |    year   | maia.com
   *            5     | 2002-02     |   month   | rosashort.com
   *            6     | 2002-03     |   month   | bryshort.com
   *            7     | 2001        |    year   | 2001.com
   *            8     | 2003        |    year   | 2003.com
   * artists: arist_id |  link
   *             1     | aaa.com
   *             2     | bbb.com
   *             3     | ccc.com
   *             4     | ddd.com
   * artistAlbums: arist_id | album_id
   *                   1    |    1
   *                   2    |    2
   *                   3    |    3
   *                   4    |    4
   *                   2    |    1
   *                   3    |    1
   *                   4    |    1
   *                   1    |    2
   * tracking: user_id | artist_id
   *              1    |    1
   *              2    |    2
   *              3    |    3
   *              4    |    4
   *              4    |    3
   *              4    |    2
   *              4    |    1
   */
  static String mockDWDBFilePath = "data/mockedData/mockDWDB.db";

  /**
   * Helper function to make a new empty test DB for testing
   * @throws SQLException (shouldn't)
   * @throws ClassNotFoundException (shouldn't)
   */
  public void createEmptyTestDB() throws SQLException, ClassNotFoundException {
    // remove DB if it exists
    File file = new File(testDWDBFilepath);
    if (file.exists()) {
      if (db != null && !db.connIsNull()) {
        assertTrue(db.closeDB());
      }
      assertTrue(file.delete());
    }
    // verify it doesn't exist anymore
    assertFalse(new File(testDWDBFilepath).exists());
    // create DB, then check if it's there now
    try {
      assertTrue(file.createNewFile());
      assertTrue(new File(testDWDBFilepath).exists());
      db = new DropWatchDB(testDWDBFilepath);
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Helper function to make a new empty test DB for testing
   * @throws SQLException (shouldn't)
   * @throws ClassNotFoundException (shouldn't)
   */
  public void connectMockDB() throws SQLException, ClassNotFoundException {
    // make sure db exists
    File file = new File(mockDWDBFilePath);
    assertTrue(file.exists());
    // disconnect if we have something open, connect to mockDB
    if (db != null && !db.connIsNull()) {
      assertTrue(db.closeDB());
    }
    // connect to it!
    try {
      db = new DropWatchDB(mockDWDBFilePath);
      assertFalse(db.connIsNull());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @BeforeEach
  public void setup() {
    try {
      createEmptyTestDB();
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  @AfterAll
  public static void cleanup() {
    try {
      if (!db.connIsNull()) {
        assertTrue(db.closeDB());
      }
      File file = new File(testDWDBFilepath);
      assertTrue(file.exists());
      assertTrue(file.delete());
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // test queryTracking
  @Test
  void test_queryTracking() {
    try {
      // connect to mock db
      connectMockDB();
      // check who users are tracking!
      ArrayList<String> tracking = new ArrayList<>();
      tracking.add("1");
      assertEquals(db.queryTracking("1", true), tracking);
      tracking.remove(0);
      tracking.add("2");
      assertEquals(db.queryTracking("2", true), tracking);
      tracking.remove(0);
      tracking.add("3");
      assertEquals(db.queryTracking("3", true), tracking);
      tracking.remove(0);
      tracking.add("1");
      tracking.add("2");
      tracking.add("3");
      tracking.add("4");
      assertEquals(db.queryTracking("4", true), tracking);

      // check who is tracking artists!
      System.out.println(tracking.size());
      tracking.remove(0);
      tracking.remove(0);
      tracking.remove(0);
      tracking.remove(0);
      tracking.add("1");
      tracking.add("4");
      assertEquals(db.queryTracking("1", false), tracking);
      tracking.remove(0);
      tracking.remove(0);
      tracking.add("2");
      tracking.add("4");
      assertEquals(db.queryTracking("2", false), tracking);
      tracking.remove(0);
      tracking.remove(0);
      tracking.add("3");
      tracking.add("4");
      assertEquals(db.queryTracking("3", false), tracking);
      tracking.remove(0);
      tracking.remove(0);
      tracking.add("4");
      assertEquals(db.queryTracking("4", false), tracking);

      // check not exists!
      tracking.remove(0);
      assertEquals(db.queryTracking("5", true), tracking);
      assertEquals(db.queryTracking("5", false), tracking);

      // close!
      assertTrue(db.closeDB());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  // test isUserTrackingArtist
  @Test
  void test_isUserTrackingArtist() {
    try {
      // connect to mock db
      connectMockDB();
      // check for tracking!
      assertTrue(db.isUserTrackingArtist("1", "1"));
      assertTrue(db.isUserTrackingArtist("2", "2"));
      assertTrue(db.isUserTrackingArtist("3", "3"));
      assertTrue(db.isUserTrackingArtist("4", "4"));
      assertTrue(db.isUserTrackingArtist("4", "3"));
      assertTrue(db.isUserTrackingArtist("4", "2"));
      assertTrue(db.isUserTrackingArtist("4", "1"));
      // check for not tracking!
      assertFalse(db.isUserTrackingArtist("1", "2"));
      assertFalse(db.isUserTrackingArtist("1", "3"));
      assertFalse(db.isUserTrackingArtist("1", "4"));
      assertFalse(db.isUserTrackingArtist("2", "1"));
      assertFalse(db.isUserTrackingArtist("2", "3"));
      assertFalse(db.isUserTrackingArtist("2", "4"));
      assertFalse(db.isUserTrackingArtist("3", "1"));
      assertFalse(db.isUserTrackingArtist("3", "2"));
      assertFalse(db.isUserTrackingArtist("3", "4"));

      // check not exists!
      assertFalse(db.isUserTrackingArtist("5", "1"));
      assertFalse(db.isUserTrackingArtist("1", "5"));

      // close!
      assertTrue(db.closeDB());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  // test addTracking
  @Test
  void test_addTracking() {
    try {
      // connect to empty db
      createEmptyTestDB();
      // verify no tracks!
      assertEquals(db.queryTracking("a", true), new ArrayList<>());
      assertEquals(db.queryTracking("b", false), new ArrayList<>());
      assertFalse(db.isUserTrackingArtist("a", "b"));
      // add some tracks!
      assertTrue(db.addTracking("a", "b"));
      assertTrue(db.addTracking("a", "c"));
      assertTrue(db.addTracking("a", "d"));
      assertTrue(db.addTracking("b", "b"));
      // verify they're there!
      ArrayList<String> tracking = new ArrayList<>();
      tracking.add("b");
      tracking.add("c");
      tracking.add("d");
      assertEquals(db.queryTracking("a", true), tracking);
      tracking.remove(0);
      tracking.remove(0);
      tracking.remove(0);
      tracking.add("a");
      tracking.add("b");
      assertEquals(db.queryTracking("b", false), tracking);
      assertTrue(db.isUserTrackingArtist("a", "b"));
      assertTrue(db.isUserTrackingArtist("a", "c"));
      assertTrue(db.isUserTrackingArtist("a", "d"));

      tracking.remove(0);
      tracking.remove(0);
      tracking.add("b");
      assertEquals(db.queryTracking("b", true), tracking);
      assertTrue(db.isUserTrackingArtist("b", "b"));

      // verify no false tracks!
      assertFalse(db.isUserTrackingArtist("b", "c"));
      assertFalse(db.isUserTrackingArtist("b", "d"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  // test removeTracking
  @Test
  void test_removeTracking() {
    try {
      // connect to empty db
      createEmptyTestDB();

      // add some stuff
      db.addTracking("a", "a");
      db.addTracking("a", "b");
      db.addTracking("a", "c");
      db.addTracking("b", "a");
      db.addTracking("a", "d");

      // verify its there
      assertTrue(db.isUserTrackingArtist("a", "a"));
      assertTrue(db.isUserTrackingArtist("a", "b"));
      assertTrue(db.isUserTrackingArtist("a", "c"));
      assertTrue(db.isUserTrackingArtist("a", "d"));

      // now remove some stuff!
      assertTrue(db.removeTracking("a", "a"));
      // verify its gone/stuff is still there!
      assertFalse(db.isUserTrackingArtist("a", "a"));
      assertTrue(db.isUserTrackingArtist("a", "b"));
      assertTrue(db.isUserTrackingArtist("a", "c"));
      assertTrue(db.isUserTrackingArtist("a", "d"));
      assertTrue(db.isUserTrackingArtist("b", "a"));

      // remove more
      assertTrue(db.removeTracking("a", "b"));
      // verify its gone/stuff is still there!
      assertFalse(db.isUserTrackingArtist("a", "b"));
      assertTrue(db.isUserTrackingArtist("a", "c"));
      assertTrue(db.isUserTrackingArtist("a", "d"));
      assertTrue(db.isUserTrackingArtist("b", "a"));

      // make sure returns false if try to remove again, or remove non-existent entry
      assertFalse(db.removeTracking("a", "a"));
      assertFalse(db.removeTracking("a", "e"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * helper function for testing - used to create album arrays on the fly!
   * @param releaseDate release date of array
   * @param precision preicsion of release date of array
   * @param link link in array
   * @return an array representing the album array that would be returned from a query to albums
   */
  String[] buildAlbumsArray(String releaseDate, String precision, String link) {
    String[] arr = new String[3];
    arr[0] = releaseDate;
    arr[1] = precision;
    arr[2] = link;
    return arr;
  }

  // test queryAlbums
  @Test
  void test_queryAlbums() {
    try {
      // connect to mock db
      connectMockDB();
      // collect albums in list!
      ArrayList<String[]> albums = new ArrayList<>();

      // query albums!
      albums.add(buildAlbumsArray("2002-02-09", "day", "rosa.com"));
      assertArrayEquals(db.queryAlbums("1").toArray(), albums.toArray());
      albums.remove(0);
      albums.add(buildAlbumsArray("2002-03-21", "day", "bry.com"));
      assertArrayEquals(db.queryAlbums("2").toArray(), albums.toArray());
      albums.remove(0);
      albums.add(buildAlbumsArray("2002-07", "month", "aku.com"));
      assertArrayEquals(db.queryAlbums("3").toArray(), albums.toArray());
      albums.remove(0);
      albums.add(buildAlbumsArray("2002", "year", "maia.com"));
      assertArrayEquals(db.queryAlbums("4").toArray(), albums.toArray());
      albums.remove(0);
      albums.add(buildAlbumsArray("2002-02", "month", "rosashort.com"));
      assertArrayEquals(db.queryAlbums("5").toArray(), albums.toArray());
      albums.remove(0);
      albums.add(buildAlbumsArray("2002-03", "month", "bryshort.com"));
      assertArrayEquals(db.queryAlbums("6").toArray(), albums.toArray());
      albums.remove(0);
      albums.add(buildAlbumsArray("2001", "year", "2001.com"));
      assertArrayEquals(db.queryAlbums("7").toArray(), albums.toArray());
      albums.remove(0);
      albums.add(buildAlbumsArray("2003", "year", "2003.com"));
      assertArrayEquals(db.queryAlbums("8").toArray(), albums.toArray());

      // check not exists!
      albums.remove(0);
      assertArrayEquals(db.queryAlbums("9").toArray(), albums.toArray());

      // close!
      assertTrue(db.closeDB());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  // test insertOrReplaceAlbums
  @Test
  void test_insertOrReplaceAlbums() {
    try {
      // connect to empty db
      createEmptyTestDB();
      // verify no albums!
      assertTrue(db.queryAlbums("a").isEmpty());

      // add some albums!
      assertTrue(db.insertOrReplaceAlbums("a", "2020", "year", "a.com"));
      assertTrue(db.insertOrReplaceAlbums("b", "2021", "year", "b.com"));
      assertTrue(db.insertOrReplaceAlbums("c", "2022", "year", "c.com"));

      // verify they're there!
      ArrayList<String[]> albums = new ArrayList<>();
      albums.add(buildAlbumsArray("2020", "year", "a.com"));
      assertArrayEquals(db.queryAlbums("a").toArray(), albums.toArray());
      albums.remove(0);
      albums.add(buildAlbumsArray("2021", "year", "b.com"));
      assertArrayEquals(db.queryAlbums("b").toArray(), albums.toArray());
      albums.remove(0);
      albums.add(buildAlbumsArray("2022", "year", "c.com"));
      assertArrayEquals(db.queryAlbums("c").toArray(), albums.toArray());

      // verify no false albums!
      albums.remove(0);
      assertArrayEquals(db.queryAlbums("d").toArray(), albums.toArray());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  // test removeAlbums
  @Test
  void test_removeAlbums() {
    try {
      // connect to empty db
      createEmptyTestDB();
      // verify no albums!
      assertTrue(db.queryAlbums("a").isEmpty());

      // add some albums!
      assertTrue(db.insertOrReplaceAlbums("a", "2020", "year", "a.com"));
      assertTrue(db.insertOrReplaceAlbums("b", "2021", "year", "b.com"));
      assertTrue(db.insertOrReplaceAlbums("c", "2022", "year", "c.com"));

      // verify they're there!
      ArrayList<String[]> albums = new ArrayList<>();
      albums.add(buildAlbumsArray("2020", "year", "a.com"));
      assertArrayEquals(db.queryAlbums("a").toArray(), albums.toArray());
      albums.remove(0);
      albums.add(buildAlbumsArray("2021", "year", "b.com"));
      assertArrayEquals(db.queryAlbums("b").toArray(), albums.toArray());
      albums.remove(0);
      albums.add(buildAlbumsArray("2022", "year", "c.com"));
      assertArrayEquals(db.queryAlbums("c").toArray(), albums.toArray());

      // now remove some stuff!
      assertTrue(db.removeAlbums("a"));

      // verify its gone/stuff is still there!
      albums.remove(0);
      assertArrayEquals(db.queryAlbums("a").toArray(), albums.toArray());
      albums.add(buildAlbumsArray("2021", "year", "b.com"));
      assertArrayEquals(db.queryAlbums("b").toArray(), albums.toArray());
      albums.remove(0);
      albums.add(buildAlbumsArray("2022", "year", "c.com"));
      assertArrayEquals(db.queryAlbums("c").toArray(), albums.toArray());

      // remove more
      assertTrue(db.removeAlbums("c"));

      // verify its gone/stuff is still there!
      albums.remove(0);
      assertArrayEquals(db.queryAlbums("a").toArray(), albums.toArray());
      assertArrayEquals(db.queryAlbums("c").toArray(), albums.toArray());
      albums.add(buildAlbumsArray("2021", "year", "b.com"));
      assertArrayEquals(db.queryAlbums("b").toArray(), albums.toArray());

      // make sure returns false if try to remove again, or remove non-existent entry
      assertFalse(db.removeAlbums("a"));
      assertFalse(db.removeAlbums("d"));

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }


  // test queryArtists
  @Test
  void test_queryArtists() {
    try {
      // connect to mock db
      connectMockDB();

      // query artists!
      assertEquals(db.queryArtists("1"), "aaa.com");
      assertEquals(db.queryArtists("2"), "bbb.com");
      assertEquals(db.queryArtists("3"), "ccc.com");
      assertEquals(db.queryArtists("4"), "ddd.com");

      // check not exists!
      assertNull(db.queryArtists("5"));

      // close!
      assertTrue(db.closeDB());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  // test insertOrReplaceArtists
  @Test
  void test_insertOrReplaceArtists() {
    try {
      // connect to empty db
      createEmptyTestDB();
      // verify no artists!
      assertNull(db.queryArtists("a"));
      assertNull(db.queryArtists("b"));

      // add some artists!
      assertTrue(db.insertOrReplaceArtists("a", "a.com"));
      assertTrue(db.insertOrReplaceArtists("b", "b.com"));
      // verify they're there!
      assertEquals(db.queryArtists("a"), "a.com");
      assertEquals(db.queryArtists("b"), "b.com");

      // verify no false artists!
      assertNull(db.queryArtists("c"));
      assertNull(db.queryArtists("d"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  // test removeArtists
  @Test
  void test_removeArtists() {
    try {
      // connect to empty db
      createEmptyTestDB();

      // add some artists!
      assertTrue(db.insertOrReplaceArtists("a", "a.com"));
      assertTrue(db.insertOrReplaceArtists("b", "b.com"));
      assertTrue(db.insertOrReplaceArtists("c", "c.com"));
      assertTrue(db.insertOrReplaceArtists("d", "d.com"));
      // verify they're there!
      assertEquals(db.queryArtists("a"), "a.com");
      assertEquals(db.queryArtists("b"), "b.com");
      assertEquals(db.queryArtists("c"), "c.com");
      assertEquals(db.queryArtists("d"), "d.com");

      // now remove some stuff!
      assertTrue(db.removeArtists("a"));
      assertTrue(db.removeArtists("b"));

      // verify its gone/stuff is still there!
      assertNull(db.queryArtists("a"));
      assertNull(db.queryArtists("b"));
      assertEquals(db.queryArtists("c"), "c.com");
      assertEquals(db.queryArtists("d"), "d.com");

      // remove more
      assertTrue(db.removeArtists("d"));

      // verify its gone/stuff is still there!
      assertNull(db.queryArtists("a"));
      assertNull(db.queryArtists("b"));
      assertEquals(db.queryArtists("c"), "c.com");
      assertNull(db.queryArtists("d"));

      // make sure returns false if try to remove again, or remove non-existent entry
      assertFalse(db.removeArtists("a"));
      assertFalse(db.removeArtists("b"));
      assertFalse(db.removeArtists("d"));
      assertFalse(db.removeArtists("e"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  // test queryArtistAlbums_byOneID
  @Test
  void test_queryArtistAlbums_byOneID() {
    try {
      // connect to mock db
      connectMockDB();
      // collect albums/artists in list!
      ArrayList<String[]> albums = new ArrayList<>();
      ArrayList<?> artists = new ArrayList<>();

      // query albums!

      // check not exists!

      // close!
      assertTrue(db.closeDB());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  // test queryArtistAlbums_byPair
  @Test
  void test_queryArtistAlbums_byPair() {
    try {
      // connect to mock db
      connectMockDB();
      // collect albums in list!
      ArrayList<String[]> albums = new ArrayList<>();

      // query albums!

      // check not exists!

      // close!
      assertTrue(db.closeDB());
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  // test insertOrReplaceArtistAlbums
  @Test
  void test_insertOrReplaceArtistAlbums() {
    try {
      // connect to empty db
      createEmptyTestDB();
      // verify no albums!

      // add some albums!

      // verify they're there!


      // verify no false albums!

    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  // test removeArtistAlbums
  @Test
  void test_removeArtistAlbums() {
    try {
      // connect to empty db
      createEmptyTestDB();

      // add some stuff

      // verify its there

      // now remove some stuff!

      // verify its gone/stuff is still there!

      // remove more

      // verify its gone/stuff is still there!

      // make sure returns false if try to remove again, or remove non-existent entry
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
