package edu.brown.cs.student.api;

import edu.brown.cs.student.api.database.sqliteDB;
import org.junit.jupiter.api.*;
import spark.Spark;

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
  static String dbFilepath = "test.db";


  /**
   * Before all tests, make a new database
   */
  @BeforeAll
  public static void setup_before_everything() {
    db = new sqliteDB();
  }

//  /**
//   * Before each test, refresh connection to the database
//   */
//  @AfterEach
//  public void teardown() {
//    try {
//      db.checkConn();
//      if (!db.clearDB()) {
//          fail("failed to clear db!");
//        }
//      } catch (SQLException e) {
//      fail("failed to clear db!: " + e.getMessage());
//    }
//  }
//
//  @AfterAll
//  public static void cleanup() {
//    try {
//      db.checkConn();
//      if (!db.deleteDB()) {
//        fail("failed to delete db!");
//      }
//    } catch (SQLException e) {
//      fail("failed to delete db!: " + e.getMessage());
//    }
//  }

// Test creating a new DB (actually)
  @Test
  void test_CreateDB_GoodInput() {
    // verify a DB doesn't exist, then create it, then check if it's there now
    assertFalse(new File(dbFilepath).exists());
    assertTrue(db.createDB(dbFilepath));
    assertTrue(new File(dbFilepath).exists());
    try {
      db.checkConn();
    } catch (SQLException e) {
      fail("conn == null!!!");
    }
  }

  // Test deleting a DB (actually)
  @Test
  void test_DeleteDB_GoodInput() {
    // verify a DB doesn't exist, then create it, then check if it's there now
    assertTrue(new File(dbFilepath).exists());
    assertTrue(db.deleteDB()); // ERRORING HERE!!!
    assertFalse(new File(dbFilepath).exists());
    try {
      db.checkConn();
      fail();
    } catch (SQLException e) {
      return;
    }
  }


  // Test creating a new DB (not actually, test bad inputs)
  @Test
  void test_CreateDB_BadInput() {
    // create db
    assertTrue(db.createDB(dbFilepath));
    try {
      db.checkConn();
    } catch (SQLException e) {
      fail("conn == null!!!");
    }

    // try to create same db, should error
    assertFalse(db.createDB(dbFilepath));
  }
}