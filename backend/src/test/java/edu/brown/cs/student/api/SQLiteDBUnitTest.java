package edu.brown.cs.student.api;

import edu.brown.cs.student.api.database.sqliteDB;
import org.junit.jupiter.api.*;
import org.testng.internal.collections.Pair;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Database Class!
 */
public class SQLiteDBUnitTest {

  /**
   * The Database Object we use for testing!
   */
  static sqliteDB db = null;

  /**
   * Path to our testing DBs!
   */
  static String testDBFilepath = "src/test/java/edu/brown/cs/student/api/testDB.db";

  /**
   *   this one is DB with some simple mock data:
   *   TABLE "ageName": id INTEGER, name VARCHAR(10), age INTEGER
   *   0,rosa,21
   *   1,bry,21
   *   2,aku,20
   *   3,maia,19
   */
  static String mockDBFilePath = "data/mockedData/mockDB.db";

  /**
   * Helper function to make a new empty test DB for testing
   * @throws SQLException (shouldn't)
   * @throws ClassNotFoundException (shouldn't)
   */
  public void createEmptyTestDB() throws SQLException, ClassNotFoundException {
    // remove DB if it exists
    File file = new File(testDBFilepath);
    if (file.exists()) {
      if (!db.connIsNull()) {
        assertTrue(db.closeDB());
      }
      assertTrue(file.delete());
    }
    // verify it doesn't exist anymore
    assertFalse(new File(testDBFilepath).exists());
    // create DB, then check if it's there now
    assertTrue(db.createDB(testDBFilepath));
    assertTrue(new File(testDBFilepath).exists());
    assertFalse(db.connIsNull());
  }

  /**
   * Helper function to make a new empty test DB for testing
   * @throws SQLException (shouldn't)
   * @throws ClassNotFoundException (shouldn't)
   */
  public void connectMockDB() throws SQLException, ClassNotFoundException {
    // make sure db exists
    File file = new File(mockDBFilePath);
    assertTrue(file.exists());
    // disconnect if we have something open, connect to mockDB
    if (!db.connIsNull()) {
      assertTrue(db.closeDB());
    }
    // connect to it!
    assertTrue(db.connectDB(mockDBFilePath));
    assertFalse(db.connIsNull());
  }

  /**
   * A set of sanity checks to check if the state of our testing objects is consistent
   * @param filepath the relativeFilepath of our databse
   * @param where the location (method) in which we are calling this sanity check from
   * @return a boolean indicating whether there is any DB currently connected (in a consistent way)
   *         in other words, a true indicates a database is connected, and in a consistent way, while a false indicates
   *         a database is not connected, and in a consistent way. All other states result in exceptions due to inconsistency
   * @throws Exception Throws when state is inconsistent.
   *                   Ex: db.relativeFilepath != null, but File(db.relativeFilepath).exists() == false!
   */
  public static boolean sanityChecks(String filepath, String where) throws Exception {
    // clear DB if it exists!
    // check if we have a relative filepath
    if (filepath == null || filepath.length() == 0) {
      // nothing set - do we have a conn?
      if (db.connIsNull()) {
        // all set!
        return false;
      }
      throw new Exception("SANITYCHECK FAILED: relativeFilepath == null && conn != null in " + where);
    }
    // we do have a filepath - make sure file exists
    File file = new File(filepath);
    if (!file.exists()) {
      // file doesn't exist...
      throw new Exception("SANITYCHECK FAILED: file doesn't exist && relativeFilepath != null in " + where);
    }
    // file exists - check conn
    if (db.connIsNull()) {
      // conn is null!
      throw new Exception("SANITYCHECK FAILED: relativePath != null && file exists && conn == null in " + where);
    }
    return false;
  }


  /**
   * Before all tests, make a new database object
   */
  @BeforeAll
  public static void setup_before_everything() {
    try {
      // make new db object, do sanity checks!
      db = new sqliteDB();
      sanityChecks(db.copyRelativeFilepath(), "@BeforeAll: setup_before_everything()");
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @AfterEach
  public void teardown() {
    try {
      // sanity checks!
      String filepath = db.copyRelativeFilepath();
      // if file there consistently...
      if (sanityChecks(filepath, "@AfterEach: teardown() START")) {
        // get rid of old DB object
        db = new sqliteDB();
        // if not mock, delete db file!
        if (!filepath.equals(mockDBFilePath)) {
          assertTrue(new File(filepath).delete());
        }
      }
      // another sanity check
      sanityChecks(filepath, "@AfterEach: teardown() END");
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  @AfterAll
  public static void cleanup() {
    try {
      // sanity checks!
      String filepath = db.copyRelativeFilepath();
      // if file there consistently...
      if (sanityChecks(filepath, "@AfterEach: teardown() START")) {
        // delete connected DB if it's testDB
        if (!filepath.equals(mockDBFilePath)) {
          assertTrue(db.deleteDB());
        }
      }
      // another sanity check
      sanityChecks(filepath, "@AfterEach: teardown() END");
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
      File file = new File(testDBFilepath);
      if (file.exists()) {
        if (!db.connIsNull()) {
          assertTrue(db.closeDB());
        }
        assertTrue(file.delete());
      }
      // verify it doesn't exist anymore
      assertFalse(new File(testDBFilepath).exists());
      // create DB, then check if it's there now
      assertTrue(db.createDB(testDBFilepath));
      assertTrue(new File(testDBFilepath).exists());

      // make sure conn was successfully made!
      assertFalse(db.connIsNull());
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
      File file = new File(testDBFilepath);
      if (file.exists()) {
        if (!db.connIsNull()) {
          assertTrue(db.closeDB());
        }
        assertTrue(file.delete());
      }
      // verify it doesn't exist anymore
      assertFalse(new File(testDBFilepath).exists());
      // create DB, then check if it's there now
      assertTrue(db.createDB(testDBFilepath));
      assertTrue(new File(testDBFilepath).exists());
      assertFalse(db.connIsNull());
      // now, try to create same DB again
      assertThrows(SQLException.class, () -> {
        db.createDB(testDBFilepath);
      }, "ERROR: new SQLiteDB could not be created\n:" + "database '" + testDBFilepath + "' already exists!");
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // test closing connection
  @Test
  void test_closeDB_GoodInput() {
    try {
      // remove DB if it exists, then remake it, then check it really exists
      createEmptyTestDB();
      // now, disconnect DB - conn should be null, but file should still be there
      assertTrue(db.closeDB());
      assertTrue(db.connIsNull());
      assertTrue(new File(testDBFilepath).exists());
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // test closing connection bad input
  @Test
  void test_closeDB_BadInput() {
    try {
      // remove DB if it exists, then remake it, then check it really exists
      createEmptyTestDB();
      // close normally
      assertTrue(db.closeDB());
      // now, try to close again (fail)
      assertThrows(SQLException.class, () -> {
        db.closeDB();
      }, "ERROR: Connection to SQLite DB couldn't be closed:\n" + "this.conn == null");
      assertTrue(new File(testDBFilepath).exists());
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // Test deleting a DB
  @Test
  void test_DeleteDB_GoodInput() {
    try {
      // remove DB if it exists, then remake it, then check it really exists
      createEmptyTestDB();
      // now delete DB
      assertTrue(db.deleteDB());
      // verify it doesn't exist anymore!
      assertFalse(new File(testDBFilepath).exists());
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  @Test
  void test_DeleteDB_BadInput() {
    try {
      // remove DB if it exists, then remake it, then check it really exists
      createEmptyTestDB();
      // close DB and then delete it
      assertTrue(db.closeDB());
      assertTrue(db.connIsNull());
      assertThrows(SQLException.class, () -> {
        db.deleteDB();
      }, "ERROR: Couldn't delete SQLiteDB:\n" + "this.conn == null");

      // now connect to the db again
      assertTrue(db.connectDB(testDBFilepath));

      // delete the db once
      assertTrue(db.deleteDB());
      // verify it doesn't exist anymore
      assertFalse(new File(testDBFilepath).exists());
      // delete DB again!
      assertThrows(SQLException.class, () -> {
        db.deleteDB();
      }, "ERROR: Couldn't delete SQLiteDB:\n" + "File.delete() failed on '" + System.getProperty("user.dir") + "/" + testDBFilepath + "'");
      // verify it doesn't exist anymore!
      assertFalse(new File(testDBFilepath).exists());
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // Test checking if a table exists
  @Test
  void test_TableExists_GoodInput() {
    try {
      // connect to mockDB!
      connectMockDB();
      // check that table "ageName" exists, and that a random table doesn't exist
      assertTrue(db.tableExists("ageName"));
      assertFalse(db.tableExists("NOTageName"));
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // Test checking if a table exists
  @Test
  void test_GrabTableSchema_GoodInput() {
    try {
      // connect to mockDB!
      connectMockDB();
      // grab schema and don't throw error...
      ArrayList<sqliteDB.ColumnMetaData> schema = db.grabTableSchema("ageName");
      // make sure schemas match!
      // table name
      assertEquals(schema.get(0).TABLE_NAME(), "ageName");
      assertEquals(schema.get(1).TABLE_NAME(), "ageName");
      assertEquals(schema.get(2).TABLE_NAME(), "ageName");
      // column name
      assertEquals(schema.get(0).COLUMN_NAME(), "id");
      assertEquals(schema.get(1).COLUMN_NAME(), "name");
      assertEquals(schema.get(2).COLUMN_NAME(), "age");
      // type name
      assertEquals(schema.get(0).TYPE_NAME(), "INTEGER");
      assertEquals(schema.get(1).TYPE_NAME(), "VARCHAR");
      assertEquals(schema.get(2).TYPE_NAME(), "INTEGER");
      // VARCHAR length
      assertEquals(schema.get(1).VARCHAR_LEN(), 10);
      // column index
      assertEquals(schema.get(0).COLUMN_INDEX(), 1);
      assertEquals(schema.get(1).COLUMN_INDEX(), 2);
      assertEquals(schema.get(2).COLUMN_INDEX(), 3);
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // Test checking if a table exists
  @Test
  void test_GrabTableSchema_BadInput() {
    try {
      // connect to test db!
      createEmptyTestDB();
      // grab schema and throw error
      String tableName = "anyTableName";
      assertThrows(SQLException.class, () -> {
        db.grabTableSchema(tableName);
      }, "ERROR: Couldn't grab schema of table '" + tableName + "':\n" + "table '" + tableName + "' does not exist!");

      // disconnect db!
      assertTrue(db.closeDB());
      // grab schema and throw error
      assertThrows(SQLException.class, () -> {
        db.grabTableSchema(tableName);
      }, "ERROR: Couldn't grab schema of table '" + tableName + "':\n" + "this.conn == null");
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // Test creating a table
  @Test
  void test_CreateNewTable_GoodInput() {
    try {
      // remove DB if it exists, then remake it, then check it really exists
      createEmptyTestDB();
      // create new table
      String tableName = "test";
      assertTrue(db.createNewTable(tableName, "id INTEGER"));
      // verify it exists
      assertTrue(db.tableExists(tableName));
      // verify correct schema
      ArrayList<sqliteDB.ColumnMetaData> schema = db.grabTableSchema("test");
      assertEquals(schema.get(0).TABLE_NAME(), "test");
      assertEquals(schema.get(0).COLUMN_NAME(), "id");
      assertEquals(schema.get(0).TYPE_NAME(), "INTEGER");
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // Test creating a table (error)
  @Test
  void test_CreateNewTable_BadInput() {
    try {
      // remove DB if it exists, then remake it, then check it really exists
      createEmptyTestDB();
      // create new table
      String tableName = "test";
      assertTrue(db.createNewTable(tableName, "id INTEGER"));
      // verify it exists
      assertTrue(db.tableExists(tableName));
      // create another table same name
      assertThrows(SQLException.class, () -> {
        db.createNewTable(tableName, "id INTEGER");
      }, "ERROR: Couldn't create new table '" + tableName + "':\n" + "table '" + tableName + "' already exists!");
      // disconnect db
      assertTrue(db.closeDB());
      // create table with no db connected
      assertThrows(SQLException.class, () -> {
        db.createNewTable(tableName, "id INTEGER");
      }, "ERROR: Couldn't create new table '" + tableName + "':\n" + "this.conn == null");
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // Test checking if a table exists (error)
  @Test
  void test_TableExists_BadInput() {
    try {
      // remove DB if it exists, then remake it, then check it really exists
      createEmptyTestDB();
      // disconnect db
      assertTrue(db.closeDB());
      // create table with no db connected
      String tableName = "test";
      assertThrows(SQLException.class, () -> {
        db.tableExists(tableName);
      }, "ERROR: Couldn't check if table '" + tableName + "' exists:\n" + "this.conn == null");
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // Test dropping a table
  @Test
  void test_Commit_GoodInput() {
    try {
      // remove DB if it exists, then remake it, then check it really exists
      createEmptyTestDB();
      // create tables, make sure they exist
      assertTrue(db.createNewTable("test", "id INTEGER"));
      assertTrue(db.tableExists("test"));
      // disconnect and reconnect, should see no changes saved
      assertTrue(db.closeDB());
      assertTrue(db.connectDB(testDBFilepath));
      assertFalse(db.tableExists("test1"));

      // now do it again, but commit this time
      // create tables, make sure they exist
      assertTrue(db.createNewTable("test", "id INTEGER"));
      assertTrue(db.tableExists("test"));
      // commit!
      assertTrue(db.commit());
      // disconnect and reconnect, should see saved changes!
      assertTrue(db.closeDB());
      assertTrue(db.connectDB(testDBFilepath));
      assertTrue(db.tableExists("test"));
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // Test dropping a table
  @Test
  void test_DropTable_GoodInput() {
    try {
      // remove DB if it exists, then remake it, then check it really exists
      createEmptyTestDB();
      // create tables, make sure they exist
      assertTrue(db.createNewTable("test1", "id INTEGER"));
      assertTrue(db.createNewTable("test2", "id INTEGER"));
      assertTrue(db.tableExists("test1"));
      assertTrue(db.tableExists("test2"));
      // now try deleting, make sure only selected table is deleted
      assertTrue(db.dropTable("test2"));
      assertFalse(db.tableExists("test2"));
      assertTrue(db.tableExists("test1"));
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // Test dropping a table (error)
  @Test
  void test_DropTable_BadInput() {
    try {
      // remove DB if it exists, then remake it, then check it really exists
      createEmptyTestDB();
      // drop non-existent table
      String tableName = "test";
      assertThrows(SQLException.class, () -> {
        db.dropTable(tableName);
      }, "ERROR: Couldn't drop table '" + tableName + "':\n" + "table '" + tableName + "' does not exist!");
      // create table and verify it exists
      assertTrue(db.createNewTable(tableName, "id INTEGER"));
      assertTrue(db.tableExists(tableName));
      // disconnect db
      assertTrue(db.closeDB());
      // drop table with no db connected
      assertThrows(SQLException.class, () -> {
        db.dropTable(tableName);
      }, "ERROR: Couldn't drop table '" + tableName + "':\n" + "this.conn == null");
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // Test executeSQLQuery
  @Test
  void test_ExecuteSQLQuery_GoodInput() {
    ResultSet rs = null;
    try {
      // connect to mock DB!
      connectMockDB();

      // query through a statement: GRAB EVERYTHING
      String SQLStatement = "SELECT * FROM \"ageName\";";
      rs = db.executeSQLQuery(SQLStatement);

      // check if all rows are there
      assertEquals(1, rs.findColumn("id"));
      assertEquals(2, rs.findColumn("name"));
      assertEquals(3, rs.findColumn("age"));
      // iterate through results for query, checking if they are accurate
      // get to first row, verify values
      assertTrue(rs.next());
      assertEquals(rs.getInt("id"), 0);
      assertEquals(rs.getString("name"), "rosa");
      assertEquals(rs.getInt("age"), 21);
      // get to second row, verify values
      assertTrue(rs.next());
      assertEquals(rs.getInt("id"), 1);
      assertEquals(rs.getString("name"), "bry");
      assertEquals(rs.getInt("age"), 21);
      // get to third row, verify values
      assertTrue(rs.next());
      assertEquals(rs.getInt("id"), 2);
      assertEquals(rs.getString("name"), "aku");
      assertEquals(rs.getInt("age"), 20);
      // get to fourth row, verify values
      assertTrue(rs.next());
      assertEquals(rs.getInt("id"), 3);
      assertEquals(rs.getString("name"), "maia");
      assertEquals(rs.getInt("age"), 19);
      // should be no more 5th row
      assertFalse(rs.next());
      // close result set
      rs.close();

      // query through a statement: GRAB AGE ONLY
      SQLStatement = "SELECT age FROM \"ageName\";";
      rs = db.executeSQLQuery(SQLStatement);

      // make sure all rows are there
      assertEquals(1, rs.findColumn("age"));
      // make sure some rows AREN'T there
      ResultSet copy1 = rs;
      assertThrows(SQLException.class, () -> {
        copy1.findColumn("id");
      });
      assertThrows(SQLException.class, () -> {
        copy1.findColumn("name");
      });

      // iterate through results for query, checking if they are accurate
      // get to first row, verify values
      assertTrue(rs.next());
      assertEquals(rs.getInt("age"), 21);
      // get to second row, verify values
      assertTrue(rs.next());
      assertEquals(rs.getInt("age"), 21);
      // get to third row, verify values
      assertTrue(rs.next());
      assertEquals(rs.getInt("age"), 20);
      // get to fourth row, verify values
      assertTrue(rs.next());
      assertEquals(rs.getInt("age"), 19);
      // should be no more 5th row
      assertFalse(rs.next());
      // close result set
      rs.close();

      // complex statement!!!!!!!!!
      // query through a statement: COMLPEX
      SQLStatement = "WITH under21 AS (" +
                        "SELECT name AS nameees, age AS ageees FROM \"ageName\" " +
                        "WHERE age < 21) " +
                      "SELECT nameees AS naaames, ageees AS aaages FROM under21 " +
                      "ORDER BY aaages ASC;";
      rs = db.executeSQLQuery(SQLStatement);

      // check if all rows are there
      assertEquals(1, rs.findColumn("naaames"));
      assertEquals(2, rs.findColumn("aaages"));
      ResultSet copy2 = rs;
      assertThrows(SQLException.class, () -> {
        copy2.findColumn("id");
      });
      assertThrows(SQLException.class, () -> {
        copy2.findColumn("name");
      });
      assertThrows(SQLException.class, () -> {
        copy2.findColumn("age");
      });
      // iterate through results for query, checking if they are accurate
      // get to first row, verify values
      assertTrue(rs.next());
      assertEquals(rs.getString("naaames"), "maia");
      assertEquals(rs.getInt("aaages"), 19);
      // get to second row, verify values
      assertTrue(rs.next());
      assertEquals(rs.getString("naaames"), "aku");
      assertEquals(rs.getInt("aaages"), 20);
      // should be no more 3rd row
      assertFalse(rs.next());
      // close result set
      rs.close();

      // query through a statement: EMPTY!
      SQLStatement = "SELECT id FROM \"ageName\" WHERE age > 30;";
      rs = db.executeSQLQuery(SQLStatement);

      // make sure all rows are there
      assertEquals(1, rs.findColumn("id"));
      // make sure some rows AREN'T there
      ResultSet copy3 = rs;
      assertThrows(SQLException.class, () -> {
        copy3.findColumn("name");
      });
      assertThrows(SQLException.class, () -> {
        copy3.findColumn("age");
      });

      // iterate through results for query, checking if they are accurate
      // should be no more 1st row
      assertFalse(rs.next());
      // close result set
      rs.close();
    } catch (Exception e1) {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e2) {
          fail(e1.getMessage() + "\nThen tried to close rs and ERROR:\n" + e2.getMessage());
        }
      }
      // shouldn't error...
      fail(e1.getMessage());
    }
  }

  // Test executeSQLQuery (error)
  @Test
  void test_ExecuteSQLQuery_BadInput() {
    try {
      // remove DB if it exists, then remake it, then check it really exists
      createEmptyTestDB();
      // input malformed command
      String SQLStatement1 = "HIJKLMNOP?";
      assertThrows(SQLException.class, () -> {
        ResultSet rs = db.executeSQLQuery(SQLStatement1);
        rs.close();
      });

      // input malformed command
      String SQLStatement2 = "SELECT ** FROM \"test\"";
      assertThrows(SQLException.class, () -> {
        ResultSet rs = db.executeSQLQuery(SQLStatement2);
        rs.close();
      });
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // Test executeSQLStatement
  @Test
  void test_ExecuteSQLStatement_GoodInput() {
    Pair<Boolean, Statement> ret;
    try {
      // remove DB if it exists, then remake it, then check it really exists
      createEmptyTestDB();

      // add a table through statement
      String SQLStatement = "CREATE TABLE \"test\" (" +
        "id INTEGER," +
        "name VARCHAR(10)," +
        "age INTEGER" +
        ");";
      // make auto-closable try statement! WOW!
      try (Statement statement = (ret = db.executeSQLStatement(SQLStatement)).second()) {
        assertFalse(ret.first());
        // check for effects
        assertTrue(db.tableExists("test"));
        // close statement!
        ret.second().close();
      }

      // now insert into rows
      SQLStatement = "" +
        "INSERT INTO \"test\" VALUES (0, \"rosa\", 21);";
      // make auto-closable try statement! WOW!
      try (Statement statement = (ret = db.executeSQLStatement(SQLStatement)).second()) {
        assertFalse(ret.first());
        assertEquals(statement.getUpdateCount(), 1);
        // check for effects
        try (ResultSet rs = db.executeSQLQuery("SELECT * FROM \"test\";")) {
          assertTrue(rs.next());
          assertEquals(rs.getInt(1), 0);
          assertEquals(rs.getString(2), "rosa");
          assertEquals(rs.getInt(3), 21);
        }
        // close statement!
        ret.second().close();
      }

      // now remove added row
      SQLStatement = "" +
        "DELETE FROM \"test\" WHERE id = 0 AND name = \"rosa\" AND age = 21;";
      // make auto-closable try statement! WOW!
      try (Statement statement = (ret = db.executeSQLStatement(SQLStatement)).second()) {
        assertFalse(ret.first());
        assertEquals(statement.getUpdateCount(), 1);
        // check for effects
        try (ResultSet rs = db.executeSQLQuery("SELECT * FROM \"test\";")) {
          assertFalse(rs.next());
        }
        // close statement!
        ret.second().close();
      }

      // now delete table through statement
      SQLStatement = "DROP TABLE \"test\";";
      // make auto-closable try statement! WOW!
      try (Statement statement = (ret = db.executeSQLStatement(SQLStatement)).second()) {
        assertFalse(ret.first());
        // check for effects
        assertFalse(db.tableExists("test"));
        // close statement!
        ret.second().close();
      }
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }

  // Test executeSQLStatement (error)
  @Test
  void test_ExecuteSQLStatement_BadInput() {
    try {
      // remove DB if it exists, then remake it, then check it really exists
      createEmptyTestDB();
      // input malformed command
      String SQLStatement1 = "ABCDEFG!";
      assertThrows(SQLException.class, () -> {
          db.executeSQLStatement(SQLStatement1);
        }, "ERROR: Couldn't execute SQLQuery\n" + "+'ABCDEFG!':\n" + "[SQLITE_ERROR] SQL error or missing database (near \"ABCDEFG\": syntax error)");

      // input malformed command
      String SQLStatement2 = "DROP TABLES \"test\"";
      assertThrows(SQLException.class, () -> {
        db.executeSQLStatement(SQLStatement2);
      }, "ERROR: Couldn't execute SQLQuery\n" + "+'DROP TABLES \"test\"':\n" + "[SQLITE_ERROR] SQL error or missing database (near \"TABLES\": syntax error)");
    } catch (Exception e) {
      // shouldn't error...
      fail(e.getMessage());
    }
  }
}