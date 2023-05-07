//package edu.brown.cs.student.api;
//
//import edu.brown.cs.student.api.database.DropWatchDB;
//import edu.brown.cs.student.api.database.sqliteDB;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//
//import java.io.File;
//import java.sql.SQLException;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Unit tests for DropWatchDB!
// */
//public class DropWatchDBUnitTest {
//  /**
//   * The Database Object we use for testing!
//   */
//  static DropWatchDB db = null;
//
//  /**
//   * Path to our testing DBs!
//   */
//  static String testDWDBFilepath = "src/test/java/edu/brown/cs/student/api/testDWDB.db";
//
//  /**
//   *   this one is DB with some simple mock data:
//   *   TABLE "ageName": id INTEGER, name VARCHAR(10), age INTEGER
//   *   0,rosa,21
//   *   1,bry,21
//   *   2,aku,20
//   *   3,maia,19
//   */
//  static String mockDWDBFilePath = "data/mockedData/mockDWDB.db";
//
//  /**
//   * Helper function to make a new empty test DB for testing
//   * @throws SQLException (shouldn't)
//   * @throws ClassNotFoundException (shouldn't)
//   */
//  public void createEmptyTestDB() throws SQLException, ClassNotFoundException {
//    // remove DB if it exists
//    File file = new File(testDWDBFilepath);
//    if (file.exists()) {
//      if (!db.connIsNull()) {
//        assertTrue(db.closeDB());
//      }
//      assertTrue(file.delete());
//    }
//    // verify it doesn't exist anymore
//    assertFalse(new File(testDBFilepath).exists());
//    // create DB, then check if it's there now
//    assertTrue(db.createDB(testDBFilepath));
//    assertTrue(new File(testDBFilepath).exists());
//    assertFalse(db.connIsNull());
//  }
//
//  /**
//   * Helper function to make a new empty test DB for testing
//   * @throws SQLException (shouldn't)
//   * @throws ClassNotFoundException (shouldn't)
//   */
//  public void connectMockDB() throws SQLException, ClassNotFoundException {
//    // make sure db exists
//    File file = new File(mockDBFilePath);
//    assertTrue(file.exists());
//    // disconnect if we have something open, connect to mockDB
//    if (!db.connIsNull()) {
//      assertTrue(db.closeDB());
//    }
//    // connect to it!
//    assertTrue(db.connectDB(mockDBFilePath));
//    assertFalse(db.connIsNull());
//  }
//
//  /**
//   * A set of sanity checks to check if the state of our testing objects is consistent
//   * @param filepath the relativeFilepath of our databse
//   * @param where the location (method) in which we are calling this sanity check from
//   * @return a boolean indicating whether there is any DB currently connected (in a consistent way)
//   *         in other words, a true indicates a database is connected, and in a consistent way, while a false indicates
//   *         a database is not connected, and in a consistent way. All other states result in exceptions due to inconsistency
//   * @throws Exception Throws when state is inconsistent.
//   *                   Ex: db.relativeFilepath != null, but File(db.relativeFilepath).exists() == false!
//   */
//  public static boolean sanityChecks(String filepath, String where) throws Exception {
//    // clear DB if it exists!
//    // check if we have a relative filepath
//    if (filepath == null || filepath.length() == 0) {
//      // nothing set - do we have a conn?
//      if (db.connIsNull()) {
//        // all set!
//        return false;
//      }
//      throw new Exception("SANITYCHECK FAILED: relativeFilepath == null && conn != null in " + where);
//    }
//    // we do have a filepath - make sure file exists
//    File file = new File(filepath);
//    if (!file.exists()) {
//      // file doesn't exist...
//      throw new Exception("SANITYCHECK FAILED: file doesn't exist && relativeFilepath != null in " + where);
//    }
//    // file exists - check conn
//    if (db.connIsNull()) {
//      // conn is null!
//      throw new Exception("SANITYCHECK FAILED: relativePath != null && file exists && conn == null in " + where);
//    }
//    return false;
//  }
//
//
//  /**
//   * Before all tests, make a new database object
//   */
//  @BeforeAll
//  public static void setup_before_everything() {
//    try {
//      // make new db object, do sanity checks!
//      db = new sqliteDB();
//      sanityChecks(db.copyRelativeFilepath(), "@BeforeAll: setup_before_everything()");
//    } catch (Exception e) {
//      fail(e.getMessage());
//    }
//  }
//
//  @AfterEach
//  public void teardown() {
//    try {
//      // sanity checks!
//      String filepath = db.copyRelativeFilepath();
//      // if file there consistently...
//      if (sanityChecks(filepath, "@AfterEach: teardown() START")) {
//        // get rid of old DB object
//        db = new sqliteDB();
//        // if not mock, delete db file!
//        if (!filepath.equals(mockDBFilePath)) {
//          assertTrue(new File(filepath).delete());
//        }
//      }
//      // another sanity check
//      sanityChecks(filepath, "@AfterEach: teardown() END");
//    } catch (Exception e) {
//      // shouldn't error...
//      fail(e.getMessage());
//    }
//  }
//
//  @AfterAll
//  public static void cleanup() {
//    try {
//      // sanity checks!
//      String filepath = db.copyRelativeFilepath();
//      // if file there consistently...
//      if (sanityChecks(filepath, "@AfterEach: teardown() START")) {
//        // delete connected DB if it's testDB
//        if (!filepath.equals(mockDBFilePath)) {
//          assertTrue(db.deleteDB());
//        }
//      }
//      // another sanity check
//      sanityChecks(filepath, "@AfterEach: teardown() END");
//    } catch (Exception e) {
//      // shouldn't error...
//      fail(e.getMessage());
//    }
//  }
//}
