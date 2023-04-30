package edu.brown.cs.student.api;

import edu.brown.cs.student.api.database.sqliteDB;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Database Class!
 */
public class DatabaseUnitTest {

  /**
   * The Database Object we use for testing!
   */
  static sqliteDB db = null;

  /**
   * Path to our testing DB!
   */
  static String dbFilepath = "src/test/java/edu/brown/cs/student/api/testDB.db";


  /**
   * Before all tests, make a new database
   */
  @BeforeAll
  public static void setup_before_everything() {
    db = new sqliteDB();
  }

  @AfterEach
  public void teardown() {
    try {
      // clear DB if it exists
      File file = new File(dbFilepath);
      if (file.exists()) {
        assertTrue(db.clearDB());
      }
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  @AfterAll
  public static void cleanup() {
    try {
      // clear DB if it exists
      File file = new File(dbFilepath);
      if (file.exists()) {
        assertTrue(db.closeDB());
        assertTrue(file.delete());
      }
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }
  // Test creating a new DB (actually)
  @Test
  void test_CreateDB_GoodInput() {
    try {
      // remove DB if it exists
      File file = new File(dbFilepath);
      if (file.exists()) {
        assertTrue(db.closeDB());
        assertTrue(file.delete());
      }
      // verify it doesn't exist anymore
      assertFalse(new File(dbFilepath).exists());
      // create DB, then check if it's there now
      assertTrue(db.createDB(dbFilepath));
      assertTrue(new File(dbFilepath).exists());
      try {
        db.checkConn();
      } catch (SQLException e) {
        fail("conn == null!!!");
      }
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // Test creating a new DB (actually)
  @Test
  void test_CreateDB_BadInput() {
    try {
      // remove DB if it exists
      File file = new File(dbFilepath);
      if (file.exists()) {
        assertTrue(file.delete());
      }
      // verify it doesn't exist anymore
      assertFalse(new File(dbFilepath).exists());
      // create DB, then check if it's there now
      assertTrue(db.createDB(dbFilepath));
      assertTrue(new File(dbFilepath).exists());
      db.checkConn();
      // now, try to create same DB again
      assertThrows(SQLException.class, () -> {
        db.createDB(dbFilepath);
      }, "ERROR: new SQLiteDB could not be created\n:" + "database '" + dbFilepath + "' already exists!");
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // Test deleting a DB (actually)
  @Test
  void test_DeleteDB_GoodInput() {
    try {
      // remove DB if it exists
      File file = new File(dbFilepath);
      if (file.exists()) {
        assertTrue(file.delete());
      }
      // verify it doesn't exist anymore
      assertFalse(new File(dbFilepath).exists());
      // create DB, then delete it
      assertTrue(db.createDB(dbFilepath));
      assertTrue(db.deleteDB());
      // verify it doesn't exist anymore!
      assertFalse(new File(dbFilepath).exists());
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  @Test
  void test_DeleteDB_BadInput() {
    try {
      // remove DB if it exists
      File file = new File(dbFilepath);
      if (file.exists()) {
        assertTrue(file.delete());
      }
      // verify it doesn't exist anymore
      assertFalse(new File(dbFilepath).exists());
      // delete non-existent DB
      assertThrows(SQLException.class, () -> {
        db.deleteDB();
      }, "ERROR: Could not load SQLite JDBC driver:\n" + "File.delete() failed on '" + System.getProperty("user.dir") + "/" + dbFilepath + "'");
      // verify it doesn't exist anymore!
      assertFalse(new File(dbFilepath).exists());
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }
}