package edu.brown.cs.student.api.database;
import java.io.File;
import java.io.IOException;
import java.sql.*;

/**
 * A class for connecting to and interacting with the sqlite3 database for our
 * backend server. This database will store information on what artists are being
 * tracked by which users, what artists' most recent release dates are, and more!
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
 *    - you must commit your changes to the DB for them to take effect! autocommit is set to false by default, but this
 *      can be changed with Database.setAutoCommit(), or each commit can be done manually with Database.commit()
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
    return new String(relativeFilepath);
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
   * method to check if this.conn is null. returns nothing, but throws exception
   * if this.conn is null
   *
   * @throws SQLException if this.conn is null
   * 
   */
  public void checkConn() throws SQLException {
    // verify that we actually have a conn
    if (this.conn == null) {
      // there is no conn to close!
      throw new SQLException("this.conn == null");
    }
  }

  /**
   * Method for committing manually. When used, commits staged changes to the DB
   *
   * @return boolean indicating success of commit
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not succesfully commit
   * 
   */
  public boolean commit() throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throws exception if no)
      this.checkConn();

      // commit!
      this.conn.commit();

      // return success!
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
   * @throws SQLException if could not succesfully setAutoCommit
   * 
   */
  public boolean setAutoCommit(boolean bool) throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throws exception if no)
      this.checkConn();

      // set autocommit!
      this.conn.setAutoCommit(bool);

      // return success!
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
   * and therefore this method checks to see if the databse exists before connecting,
   * and if it DOES exist, it does not connect. If you want to connect to a DB
   * that already exists, use Database.connectDB()!
   * -----------------------------------------------------------------------------
   * Source material: <a href="https://www.sqlitetutorial.net/sqlite-java/create-database/">...</a>
   * -----------------------------------------------------------------------------
   *
   * @param relativeFilepath the filepath of the DB to be created
   * @return a boolean indicating success of creation
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not succesfully create SQLiteDB
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
   * If you want to create a new databse, use Database.createDB()!
   * -----------------------------------------------------------------------------
   * Source material: <a href="https://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/">...</a>
   * -----------------------------------------------------------------------------
   *
   * @param relativeFilepath the relative filepath of the database, relative to the project root
   * @return a boolean indicating if we could connect or not
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not succesfully connect to SQLiteDB
   * 
   */
  public boolean connectDB(String relativeFilepath) throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      checkForSqliteDriver();

      // conn represents our connection to the database itself
      Connection conn = null;

      // check if database already exists
      File f = new File(relativeFilepath);
      if (!f.exists()) {
        // if it already exists, throw error. We don't want to create a new database,
        // we want to connect to an existing one!
        throw new SQLException("SQLiteDB '" + System.getProperty("user.dir") + "/" + relativeFilepath + "' does not exist!");
      }

      // check if this.conn != null
      if (this.conn != null) {
        throw new SQLException("Already connected to another database '" + System.getProperty("user.dir") + "/" + this.relativeFilepath + "'," +
        "must disconnect with sqliteDB.close() before connecting to new database!");
      }

      // calculate path to db (adding "jdbc:sqlite:" is required by the library)
      String url = "jdbc:sqlite:" + relativeFilepath;
      // create a connection to the database
      // (throws SQLException if can't connect to DB)
      conn = DriverManager.getConnection(url);
      // update this.conn
      this.conn = conn;
      // set autocommit to false
      this.setAutoCommit(false);

      // success!
      System.out.println("Success! Connection to SSQLiteDB has been established");
      this.relativeFilepath = relativeFilepath;
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
   * @throws SQLException if could not succesfully close SQLiteDB
   * 
   */
  public boolean closeDB() throws ClassNotFoundException, SQLException {
    // try to close this.conn!
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throws exception if no)
      this.checkConn();

      // close, set this.conn to null
      this.conn.close();
      this.conn = null;

      // return success!
      return true;
    } catch (SQLException e) {
      // error creating the new table!
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
   * @throws SQLException if could not succesfully check if table exists
   * 
   * -----------------------------------------------------------------------------
   * <a href="https://stackoverflow.com/questions/27007931/java-check-table-existence-in-sqlite">...</a>
   * -----------------------------------------------------------------------------
   */
  public boolean tableExists(String name) throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throws exception if no)
      this.checkConn();

      // grab metadata of database
      DatabaseMetaData md = conn.getMetaData();
      // get tables that match our table's name
      ResultSet rs = md.getTables(null, null, name, null);
      rs.last();
      // return if there are results in the ResultSet
      return rs.getRow() > 0;
    } catch (SQLException e) {
      // error checking if the table exists!
      throw new SQLException("ERROR: Couldn't check if table '" + name + "' exists:\n" + e.getMessage());
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
   * @throws SQLException if could not succesfully create new table
   * 
   */
  public boolean createNewTable(String name, String schema) throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throws exception if no)
      this.checkConn();

      // check if table already exists
      if (tableExists(name)) {
        // if exists, throw error
        throw new SQLException("table '" + name + "' already exists!");
      }

      // make SQL statement for creating a new table
      String sqlStatment = "CREATE TABLE " + name + " (" + schema + ");";
      // execute the statement!
      this.conn.createStatement().execute(sqlStatment);

      // return success!
      return true;
    } catch (SQLException e) {
      // error creating the new table!
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
   * @throws SQLException if could not succesfully clear table
   * 
   */
  public boolean clearTable(String name) throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throws exception if no)
      this.checkConn();

      // check if table doesn't exist
      if (!tableExists(name)) {
        // if exists, throw error
        throw new SQLException("table '" + name + "' does not exist!");
      }

      // make SQL statement for clearing a table
      String sqlStatment = "DELETE FROM " + name + ";";
      // execute the statement!
      this.conn.createStatement().execute(sqlStatment);

      // return success!
      return true;
    } catch (SQLException e) {
      // error clearing the table!
      throw new SQLException("ERROR: Couldn't clear table '" + name + "':\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }

  /**
   * Method for dropping a table (delete table itself)
   *
   * @param name the name of the table to drop
   * @return a boolean indicating if table was deleted successfully
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not succesfully drop table
   * 
   */
  public boolean dropTable(String name) throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throws exception if no)
      this.checkConn();

      // check if table doesn't exist
      if (!tableExists(name)) {
        // if exists, throw error
        throw new SQLException("table '" + name + "' does not exist!");
      }

      // make SQL udpate for dropping a table
      String sqlUpdate = "DROP TABLE " + name + ";";
      // execute the statement!
      this.conn.createStatement().executeUpdate(sqlUpdate);

      // return success!
      return true;
    } catch (SQLException e) {
      // error dropping the table!
      throw new SQLException("ERROR: Couldn't drop table '" + name + "':\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }

  /**
   * Method for clearing out a DB (all elements, all tables, all rows)
   *
   * @return a boolean indicating success of clearing of table
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not succesfully clear SQLiteDB
   * 
   */
  public boolean clearDB() throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throws exception if no)
      this.checkConn();

      // make SQL statement object!
      Statement statement = this.conn.createStatement();

      // make SQL query for selecting all tables
      String sqlQuery = "SELECT name FROM sqlite_master WHERE type='table'";
      // execute the statement!
      ResultSet result = statement.executeQuery(sqlQuery);

      // Generate a series of "DROP TABLE" queries for each table name
      while (result.next()) {
        // drop tables!
        String name = result.getString("name");
        if (!this.dropTable(name)) {
          // if drop table failed, report failure
          throw new SQLException("ERROR: Couldn't clear SQLiteDB");
        }
      }

      // return success!
      return true;
    } catch (SQLException e) {
      // error clear the DB!
      throw new SQLException("ERROR: Couldn't clear SQLiteDB:\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }

  /**
   * Method for deleting a DB (the file itself)
   *
   * @return a boolean indicating success of deleting this DB!
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not succesfully delete SQLiteDB
   * 
   */
  public boolean deleteDB() throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throws exception if no)
      this.checkConn();

      // close conn to our file
      if (!this.closeDB()) {
        throw new SQLException("ERROR: Couldn't close this.conn");
      }

      // grab our file, delete
      File f = new File(this.relativeFilepath);
      if (!f.delete()) {
        System.out.println(f.exists());
        throw new SQLException("File.delete() failed on '" + System.getProperty("user.dir") + "/" + this.relativeFilepath + "'");
      }

      // return success!
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
   * @param sqlQuery the query to be executed
   * @return a ResultSet with the results of the query
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not succesfully execute SQLQuery
   * 
   */
  public ResultSet executeSQLQuery(String sqlQuery) throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throws exception if no)
      this.checkConn();

      // execute the statement!
      ResultSet result = this.conn.createStatement().executeQuery(sqlQuery);

      // return success!
      return result;
    } catch (SQLException e) {
      // error executing the SQLQuery!
      throw new SQLException("ERROR: Couldn't execute SQLQuery\n'" + sqlQuery + "':\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }

  /**
   * Function for executing an SQL Statement! Very open-ended for maximum
   * flexibility :)
   * @param sqlStatement the statement to be executed
   * @return a boolean indicating success of statement execution
   * @throws ClassNotFoundException if could not load SQLite JDBC driver
   * @throws SQLException if could not succesfully execute SQLStatement
   * 
   */
  public boolean executeSQLStatement(String sqlStatement) throws ClassNotFoundException, SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkForSqliteDriver();

      // verify that we actually have a conn
      // (throws exception if no)
      this.checkConn();

      // execute the statement!
      this.conn.createStatement().execute(sqlStatement);

      // return success!
      return true;
    } catch (SQLException e) {
      // error executing the SQLStatement!
      throw new SQLException("ERROR: Couldn't execute SQLStatement\n'" + sqlStatement + "':\n" + e.getMessage());
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      throw new ClassNotFoundException("ERROR: Could not load SQLite JDBC driver:\n" + e.getMessage());
    }
  }
}