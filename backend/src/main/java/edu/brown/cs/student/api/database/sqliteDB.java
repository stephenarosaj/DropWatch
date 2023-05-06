package edu.brown.cs.student.api.database;
import org.testng.internal.collections.Pair;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A class for connecting to and interacting with an sqlite3 database.
 * This class uses the JDBC (Java DataBase Controller) library with an sqlite3
 * driver (because I <3 sqlite3). My understanding is that JDBC is like the VFS
 * (Virtual File System) and sqlite3 is like S5FS (a filesystem), if that makes
 * sense to you, reader.
 * -----------------------------------------------------------------------------
 * JDBC Driver Documentation: <a href="https://www.sqlite.org/java/raw/doc/overview.html?name=0a704f4b7294a3d63e6ea2b612daa3b997c4b5f1">...</a>
 * sqliteTutorial Documentation (lots of source material from here): <a href="https://www.sqlitetutorial.net/sqlite-java/">...</a>
 * -----------------------------------------------------------------------------
 * NOTES:
 *    - "the only data types supported on SQLite tables are java.lang.String, short, int, float, and double"
 *    - connecting to a DB that does not exist will NOT create a new DB (typically it does, but this functionality is suppressed by this class)
 *    - almost (ALMOST) none of these methods throw exceptions. instead, they print to stdout. exceptions to this rule are:
 *      - tableExists(): this method returns true/false indicating if the table exists. if its existence cannot be determined,
 *        an exception is thrown.
 *    - autoCommit is on by default!
 */
public class sqliteDB {
  /**
   * The Connection to our database
   */
  private Connection conn;

  /**
   * Relative filepath of our database
   */
  private String relativeFilepath;

  /**
   * Constructor for a Database object!
   */
  public sqliteDB() {
    this.conn  = null;
    this.relativeFilepath  = null;
  }

  /**
   * Get defensive copy of relativeFilepath
   */
  public String copyRelativeFilepath() {
    if (this.relativeFilepath == null) {
      return null;
    } else {
      return new String(this.relativeFilepath);
    }
  }

  /**
   * method to check that we have the org.sqlite.JDBC class, indicating that
   * our JDBC is configured properly. returns nothing, but throws exception
   * if we don't have the class
   *
   * @throws ClassNotFoundException if we don't have the org.sqlite.JDBC class
   */
  private void checkForSqliteDriver() throws ClassNotFoundException {
    // check if we have an org.sqlite.JDBC class
    // (throws ClassNotFoundException if no)
    Class.forName("org.sqlite.JDBC");
  }

  /**
   * method to check if this.conn is null. returns boolean indicating if null
   * @return boolean indicating if this.conn == null
   *
   */
  public boolean connIsNull() {
    // verify that we actually have a conn
    return this.conn == null;
  }

  /**
   * Method for committing manually. When used, commits staged changes to the DB
   *
   * @return boolean indicating success of commit
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not successfully commit
   * 
   */
  public boolean commit() throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throw exception if no)
      if (this.connIsNull()) {
        throw new SQLException("this.conn == null");
      }

      // commit!
      this.conn.commit();

      // return success!
      System.out.println("Success! Committed to DB!");
      return true;
    } catch (SQLException e) {
      // error committing!
      throw new SQLException("ERROR: Couldn't this.conn.commit():\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }

  /**
   * Method for setting autocommit. If true, after each statement executed,
   * the database will commit automatically
   *
   * @return a boolean indicating success of setting autocommit
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not successfully setAutoCommit
   * 
   */
  public boolean setAutoCommit(boolean bool) throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throw exception if no)
      if (this.connIsNull()) {
        throw new SQLException("this.conn == null");
      }

      // set autocommit!
      this.conn.setAutoCommit(bool);

      // return success!
      System.out.println("Success! AutoCommit successfully changed for this.conn!");
      return true;
    } catch (SQLException e) {
      // error commiting!
      throw new SQLException("ERROR: Couldn't this.conn.setAutoCommit(" + (bool ? "TRUE" : "FALSE") + "):\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }


  /**
   * Create a new database file with a chosen filepath, returning boolean indicating success.
   * Also updates this.conn on success
   * NOTE: connecting to a database that does not exist CREATES A NEW DATABASE!
   * This method is used to deliberately connect to a database that does not exist,
   * and therefore this method checks to see if the database exists before connecting,
   * and if it DOES exist, it does not connect. If you want to connect to a DB
   * that already exists, use Database.connectDB()!
   * -----------------------------------------------------------------------------
   * Source material: <a href="https://www.sqlitetutorial.net/sqlite-java/create-database/">...</a>
   * -----------------------------------------------------------------------------
   *
   * @param relativeFilepath the filepath of the DB to be created
   * @return a boolean indicating success of creation
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not successfully create SQLiteDB
   * 
   */
  public boolean createDB(String relativeFilepath) throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      checkForSqliteDriver();

      // conn represents our connection to the database itself
      Connection conn = null;

      // check if database already exists
      File f = new File(relativeFilepath);
      if (f.exists()) {
        // if it already exists, throw error. We don't want to connect to an
        // existing database, we want to create a new one!
        throw new SQLException("database '" + relativeFilepath + "' already exists!");
      }

      // calculate path to db (adding "jdbc:sqlite:" is required by the library)
      String url = "jdbc:sqlite:" + relativeFilepath;
      // create a connection to the database
      // (throws SQLException if can't connect to DB)
      conn = DriverManager.getConnection(url);
      // update this.conn and this.relativeFilepath
      this.conn = conn;
      this.relativeFilepath = relativeFilepath;
      this.setAutoCommit(false);

      // success!
      System.out.println("Success! New SQLite DB has been created");
      return true;
    } catch (SQLException e) {
      // error creating the SQLiteDB!
      throw new SQLException("ERROR: new SQLiteDB could not be created\n:" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }


  /**
   * Connect to a database specified by filepath, returning boolean indicating success.
   * Also updates this.conn on success
   * NOTE: connecting to a database that does not exist CREATES A NEW DATABASE!
   * Therefore, this method checks to see if the database exists before connecting,
   * and if it DOES NOT exist, it does not connect (it does not create a new database).
   * If you want to create a new database, use Database.createDB()!
   * -----------------------------------------------------------------------------
   * Source material: <a href="https://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/">...</a>
   * -----------------------------------------------------------------------------
   *
   * @param relativeFilepath the relative filepath of the database, relative to the project root
   * @return a boolean indicating if we could connect or not
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not successfully connect to SQLiteDB
   * 
   */
  public boolean connectDB(String relativeFilepath) throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      checkForSqliteDriver();

      // check if database already exists
      File f = new File(relativeFilepath);
      if (!f.exists()) {
        // if it already exists, throw error. We don't want to create a new database,
        // we want to connect to an existing one!
        throw new SQLException("SQLiteDB '" + System.getProperty("user.dir") + "/" + relativeFilepath + "' does not exist!");
      }

      // verify that we DO NOT have a conn
      // (throw exception if yes)
      if (!this.connIsNull()) {
        throw new SQLException("Already connected to another database '" + System.getProperty("user.dir") + "/" + this.relativeFilepath + "'," +
        "must disconnect with sqliteDB.close() before connecting to new database!");
      }

      // calculate path to db (adding "jdbc:sqlite:" is required by the library)
      String url = "jdbc:sqlite:" + relativeFilepath;
      // create a connection to the database
      // (throws SQLException if can't connect to DB)
      this.conn = DriverManager.getConnection(url);;
      // update filepath and this.conn
      this.relativeFilepath = relativeFilepath;
      // set autocommit to false
      this.setAutoCommit(false);


      // success!
      System.out.println("Success! Connection to SSQLiteDB has been established");
      return true;
    } catch (SQLException e) {
      // error connecting to the SQLiteDB!
      throw new SQLException("ERROR: Connection to SQLiteDB could not be established:\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }

  /**
   * Function to close this.conn
   *
   * @return boolean indicating success of closure
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not successfully close SQLiteDB
   * 
   */
  public boolean closeDB() throws ClassNotFoundException, SQLException {
    // try to close this.conn!
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throw exception if no)
      if (this.connIsNull()) {
        throw new SQLException("this.conn == null");
      }

      // close, set this.conn to null
      this.conn.close();
      this.conn = null;
      this.relativeFilepath = null;

      // return success!
      System.out.println("Success! Connection to SSQLiteDB has been closed");
      return true;
    } catch (SQLException e) {
      // error closing connection!
      throw new SQLException("ERROR: Connection to SQLite DB couldn't be closed:\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }

  /**
   * method to check if a table exists, returning a boolean indicating existence
   *
   * @param name the name of the table to be checked
   * @return boolean indicating existence of table
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not successfully check if table exists
   * 
   * -----------------------------------------------------------------------------
   * <a href="https://stackoverflow.com/questions/27007931/java-check-table-existence-in-sqlite">...</a>
   * -----------------------------------------------------------------------------
   */
  public boolean tableExists(String name) throws ClassNotFoundException, SQLException {
    ResultSet rs = null;
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throw exception if no)
      if (this.connIsNull()) {
        throw new SQLException("this.conn == null");
      }

      // grab metadata of database
      DatabaseMetaData metadata = conn.getMetaData();
      // get tables that match our table's name
      rs = metadata.getTables(null, null, name, null);
      // move cursor to next row of result set - will return false if nothing there
      boolean exists = rs.next();
      // close our statement!
      rs.close();
      // return existence!
      return exists;
    } catch (SQLException e) {
      // error checking if the table exists!
      // close our statement!
      if (rs != null) {
        rs.close();
      }
      throw new SQLException("ERROR: Couldn't check if table '" + name + "' exists:\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }

  /**
   * method to grab schema of a table, returning a string describing the schema
   *
   * @param name the name of the table to be checked
   * @return list representation of the schema as a string. each element is
   *         of the form "[name] [type]", such as "ID INTEGER" or "name VARCHAR(10)"
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not successfully check if table exists
   *
   * -----------------------------------------------------------------------------
   * <a href="https://stackoverflow.com/questions/27007931/java-check-table-existence-in-sqlite">...</a>
   * -----------------------------------------------------------------------------
   */
  public ArrayList<ColumnMetaData> grabTableSchema(String name) throws ClassNotFoundException, SQLException {
    ResultSet rs = null;
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throw exception if no)
      if (this.connIsNull()) {
        throw new SQLException("this.conn == null");
      }

      // check if table exists
      if (!this.tableExists(name)) {
        throw new SQLException("table '" + name + "' does not exist!");
      }

      // grab metadata of database
      DatabaseMetaData metadata = conn.getMetaData();
      // get columns of tables that match our table's name
      rs = metadata.getColumns(null, null, name, null);
      // iterate through results for metadata query by tablename, adding to list
      ArrayList<ColumnMetaData> schema =  new ArrayList<>();
      while (rs.next()) {
        // build record of metadata by grabbing from
        String TABLE_NAME = rs.getString("TABLE_NAME");
        String TABLE_SCHEM = rs.getString("TABLE_SCHEM");
        String COLUMN_NAME = rs.getString("COLUMN_NAME");
        String DATA_TYPE = rs.getString("DATA_TYPE");
        String TYPE_NAME = rs.getString("TYPE_NAME");
        int COLUMN_INDEX = rs.getInt("ORDINAL_POSITION");
        String NULLABLE = rs.getString("IS_NULLABLE");
        String COLUMN_DEFAULT = rs.getString("COLUMN_DEF");
        int VARCHAR_LEN = rs.getInt("COLUMN_SIZE");
        // build record and add to list
        schema.add(new ColumnMetaData(TABLE_NAME, TABLE_SCHEM, COLUMN_NAME, DATA_TYPE, TYPE_NAME, COLUMN_INDEX, NULLABLE, COLUMN_DEFAULT, VARCHAR_LEN));
      }

      // close our statement!
      rs.close();
      // return schema!
      return schema;
    } catch (SQLException e) {
      // close our statement!
      if (rs != null) {
        rs.close();
      }
      // error checking if the table exists!
      throw new SQLException("ERROR: Couldn't grab schema of table '" + name + "':\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }

  /**
   * Create a new table in the connected DB, returning boolean indicating success
   * of creation. Fails if no this.conn!
   * -----------------------------------------------------------------------------
   * Source material: <a href="https://www.sqlitetutorial.net/sqlite-java/create-table/">...</a>
   * -----------------------------------------------------------------------------
   *
   * @param name   the name of the table to be created
   * @param schema the schema of the table to be created
   *               EXAMPLE SCHEMA:
   *               String schema = "id integer PRIMARY KEY,\n"
   *               + "name text NOT NULL,\n"
   *               + "capacity real";
   * @return a boolean indicating success of table creation
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not successfully create new table
   * 
   */
  public boolean createNewTable(String name, String schema) throws ClassNotFoundException, SQLException {
    Statement statement = null;
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throw exception if no)
      if (this.connIsNull()) {
        throw new SQLException("this.conn == null");
      }

      // check if table already exists
      if (tableExists(name)) {
        // if exists, throw error
        throw new SQLException("table '" + name + "' already exists!");
      }

      // make SQL statement for creating a new table
      String sqlStatment = "CREATE TABLE " + name + " (" + schema + ");";
      // execute the statement!
      statement = this.conn.createStatement();
      statement.execute(sqlStatment);
      // close statement!
      statement.close();

      // return success!
      System.out.println("Success! New table has been created");
      return true;
    } catch (SQLException e) {
      // error creating the new table!
      // close our statement if its open!
      if (statement != null) {
        statement.close();
      }
      throw new SQLException("ERROR: Couldn't create new table '" + name + "':\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }

  /**
   * Method for clearing out a table (all elements, all rows)
   *
   * @param name the name of the table to clear
   * @return a boolean indicating success of clearing of DB
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not successfully clear table
   * 
   */
  public boolean clearTable(String name) throws ClassNotFoundException, SQLException {
    Statement statement = null;
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throw exception if no)
      if (this.connIsNull()) {
        throw new SQLException("this.conn == null");
      }

      // check if table doesn't exist
      if (!tableExists(name)) {
        // if exists, throw error
        throw new SQLException("table '" + name + "' does not exist!");
      }

      // make SQL statement for clearing a table
      String sqlStatment = "DELETE FROM " + name + ";";
      // execute the statement!
      statement = this.conn.createStatement();
      statement.execute(sqlStatment);
      // close statement!
      statement.close();

      // return success!
      System.out.println("Success! Table has been cleared in connected database");
      return true;
    } catch (SQLException e) {
      // error clearing the table!
      // close our statement if its open!
      if (statement != null) {
        statement.close();
      }
      throw new SQLException("ERROR: Couldn't clear table '" + name + "':\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }

  /**
   * Method for dropping a table (delete table itself).
   * @param name the name of the table to drop
   * @return a boolean indicating if table was deleted successfully
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not successfully drop table
   *
   */
  public boolean dropTable(String name) throws ClassNotFoundException, SQLException {
    Statement statement = null;
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throw exception if no)
      if (this.connIsNull()) {
        throw new SQLException("this.conn == null");
      }

      // check if table doesn't exist
      if (!tableExists(name)) {
        // if exists, throw error
        throw new SQLException("table '" + name + "' does not exist!");
      }

      // make SQL udpate for dropping a table
      String sqlUpdate = "DROP TABLE " + name + ";";
      // execute the statement!
      statement = this.conn.createStatement();
      statement.executeUpdate(sqlUpdate);
      // close statement!
      statement.close();

      // return success!
      System.out.println("Success! Table has been dropped in connected database");
      return true;
    } catch (SQLException e) {
      // error dropping the table!
      // close our statement if its open!
      if (statement != null) {
        statement.close();
      }
      throw new SQLException("ERROR: Couldn't drop table '" + name + "':\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }


//  NOTE: THIS METHOD DID NOT SEEM USEFUL, SO I COMMENTED IT OUT
//
//  /**
//   * Method for clearing out a DB (all elements, all tables, all rows)
//   *
//   * @return a boolean indicating success of clearing of table
//   * @throws ClassNotFoundException if could not load SQLite JDBC driver
//   * @throws SQLException if could not successfully clear SQLiteDB
//   *
//   */
//  public boolean clearDB() throws ClassNotFoundException, SQLException {
//    Statement statement = null;
//    try {
//      // check if we have an org.sqlite.JDBC class
//      // (throws ClassNotFoundException if no)
//      this.checkForSqliteDriver();
//
//      // verify that we actually have a conn
//      // (throw exception if no)
//      if (this.connIsNull()) {
//        throw new SQLException("this.conn == null");
//      }
//
//      // make SQL query for selecting all tables
//      String sqlQuery = "SELECT name FROM sqlite_master WHERE type='table'";
//      // execute the statement!
//      statement = this.conn.createStatement();
//      ResultSet result = statement.executeQuery(sqlQuery);
//      // close statement!
//      statement.close();
//
//      // Generate a series of "DROP TABLE" queries for each table name
//      while (result.next()) {
//        // drop tables!
//        String name = result.getString("name");
//        if (!this.dropTable(name)) {
//          // if drop table failed, report failure
//          throw new SQLException("ERROR: Couldn't clear SQLiteDB");
//        }
//      }
//
//      // return success!
//      System.out.println("Success! All tables have been deleted in database");
//      return true;
//    } catch (SQLException e) {
//      // error clear the DB!
//      // close our statement if its open!
//      if (statement != null) {
//        statement.close();
//      }      throw new SQLException("ERROR: Couldn't clear SQLiteDB:\n" + e.getMessage());
//    } catch (ClassNotFoundException e) {
//      // error! we don't have an org.sqlite.JDBC class!
//      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
//    }
//  }

  /**
   * Method for deleting a DB (the file itself)
   *
   * @return a boolean indicating success of deleting this DB!
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not successfully delete SQLiteDB
   * 
   */
  public boolean deleteDB() throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throw exception if no)
      if (this.connIsNull()) {
        throw new SQLException("this.conn == null");
      }

      String oldPath = this.relativeFilepath;
      // close conn to our file
      if (!this.closeDB()) {
        throw new SQLException("ERROR: Couldn't close this.conn");
      }

      // grab our file, check if it exists
      File f = new File(oldPath);
      if (!f.exists()) {
        throw new SQLException("File '" + System.getProperty("user.dir") + "/" + this.relativeFilepath + "' doesn't exist");
      }

      // try to delete our file
      if (!f.delete()) {
        throw new SQLException("File.delete() failed on '" + System.getProperty("user.dir") + "/" + this.relativeFilepath + "'");
      }

      // set conn and filepath
      this.conn = null;
      this.relativeFilepath = null;

      // return success!
      System.out.println("Success! Database has been deleted");
      return true;
    } catch (SQLException e) {
      // error deleting the DB!
      throw new SQLException("ERROR: Couldn't delete SQLiteDB:\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }
  /**
   * Function for executing an SQL Query! Very open-ended for maximum
   * flexibility :)
   * NOTE: DON'T FORGET TO CLOSE THE RETURNED ResultSet! You will encounter headaches later if you forget...
   * @param sqlQuery the query to be executed
   * @return a ResultSet with the results of the query
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not successfully execute SQLQuery
   * 
   */
  public ResultSet executeSQLQuery(String sqlQuery) throws ClassNotFoundException, SQLException {
    Statement statement = null;
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throw exception if no)
      if (this.connIsNull()) {
        throw new SQLException("this.conn == null");
      }

      // execute the statement!
      statement = this.conn.createStatement();
      ResultSet result = statement.executeQuery(sqlQuery);

      // return success!
      System.out.println("Success! SQL Query has been executed in connected database");
      return result;
    } catch (SQLException e) {
      // close our statement if its open!
      if (statement != null) {
        statement.close();
      }
      // error executing the SQLQuery!
      throw new SQLException("ERROR: Couldn't execute SQLQuery\n'" + sqlQuery + "':\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }

  /**
   * Function for executing an SQL Statement THAT RETURNS A RESULT SET OR UPDATES SOME ROWS!
   * Very open-ended for maximum flexibility :)
   * @param sqlStatement the statement to be executed
   * @return a boolean and Statement containing the results. The boolean indicates if there is
   *         a result set with items in it. If false, there are no items, and/or the return value of
   *         the statement isn't actually a ResultSet, but an updateCount.
   *         NOTE: on INSERT, bool = false && updateCount > 0
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not successfully execute SQLStatement
   * 
   */
  public Pair<Boolean, Statement> executeSQLStatement(String sqlStatement) throws ClassNotFoundException, SQLException {
    Statement statement = null;
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throw exception if no)
      if (this.connIsNull()) {
        throw new SQLException("this.conn == null");
      }

      // execute the statement!
      statement = this.conn.createStatement();
      boolean ret = statement.execute(sqlStatement);
      if (ret) {
        // ResultSet
        System.out.println("Success! SQL Statement has been executed in connected database - ResultSet returned from statement");
      }
      // no items or updateCount
      System.out.println("Success! SQL Statement has been executed in connected database - UpdateCount returned from statement, or no items in ResultSet");

      // return success!
      return new Pair<Boolean, Statement>(ret, statement);
    } catch (SQLException e) {
      // error executing the SQLStatement!
      // close our statement if its open!
      if (statement != null) {
        statement.close();
      }
      throw new SQLException("ERROR: Couldn't execute SQLStatement\n'" + sqlStatement + "':\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }

  /**
   * Record to hold metadata of a column from conn.getMetaData.getTables()
   * The record's information is populated fromDatabaseMetaData.java, line 1573 and onwards
   * @param TABLE_NAME name of table
   * @param TABLE_SCHEM schema of table??? (may be null)
   * @param COLUMN_NAME name of column
   * @param DATA_TYPE type of column
   * @param TYPE_NAME type name of column???
   * @param COLUMN_INDEX index of column (starting at 1)
   * @param NULLABLE is column nullable
   * @param COLUMN_DEFAULT default value for column (may be null)
   * @param VARCHAR_LEN if varchar, length of varchar
   *                    "Note that numeric arguments in parentheses that following
   *                    the type name (ex: "VARCHAR(255)") are ignored by SQLite.
   *                    SQLite does not impose any length restrictions (other than
   *                    the large global SQLITE_MAX_LENGTH limit) on the length
   *                    of strings, BLOBs or numeric values."
   *                    src = <a href="https://stackoverflow.com/questions/35413956/trying-to-get-the-column-size-of-a-column-using-jdbc-metadata">...</a>
   */
  public record ColumnMetaData(String TABLE_NAME, String TABLE_SCHEM,
                        String COLUMN_NAME, String DATA_TYPE, String TYPE_NAME, int COLUMN_INDEX,
                        String NULLABLE, String COLUMN_DEFAULT,
                        int VARCHAR_LEN) {
    /**
     * equality checker for this record!
     * @param that the object we want to compare this to for equality (generated by IntelliJ)
     * @return boolean indicating equality of this and that
     */
    @Override
    public boolean equals(Object that) {
      // check equality methodically!
      if (this == that) return true;
      // if that == null or classes don't match...
      if (that == null || this.getClass() != that.getClass()) return false;
      // cast that as ColumnMetaData
      ColumnMetaData thatCast = (ColumnMetaData) that;
      return COLUMN_INDEX == thatCast.COLUMN_INDEX && VARCHAR_LEN == thatCast.VARCHAR_LEN && TABLE_NAME.equals(thatCast.TABLE_NAME) && Objects.equals(TABLE_SCHEM, thatCast.TABLE_SCHEM) && COLUMN_NAME.equals(thatCast.COLUMN_NAME) && DATA_TYPE.equals(thatCast.DATA_TYPE) && Objects.equals(TYPE_NAME, thatCast.TYPE_NAME) && NULLABLE.equals(thatCast.NULLABLE) && Objects.equals(COLUMN_DEFAULT, thatCast.COLUMN_DEFAULT);
    }

    /**
     * Hasher for this record!
     * @return an int that is the hashcode for this object
     */
    @Override
    public int hashCode() {
      return Objects.hash(TABLE_NAME, TABLE_SCHEM, COLUMN_NAME, DATA_TYPE, TYPE_NAME, COLUMN_INDEX, NULLABLE, COLUMN_DEFAULT, VARCHAR_LEN);
    }
  }
}