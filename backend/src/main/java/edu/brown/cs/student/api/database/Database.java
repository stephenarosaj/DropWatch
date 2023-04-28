package edu.brown.cs.student.api.database;
import java.io.File;
import java.sql.*;

/**
 * A class for connecting to and interacting with the sqlite3 database for our
 * backend server. This database will store information on what artists are being
 * tracked by which users, what artists' most recent release dates are, and more
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
 */
public class Database {

  /**
   * The Connection to our database
   */
  private Connection conn;

  /**
   * Constructor for a Database object!
   *
   * @param conn The Connection to our database
   */
  public Database(Connection conn) {
    this.conn = conn;
  }

  /**
   * method to check that we have the org.sqlite.JDBC class, indicating that
   * our JDBC is configured properly. returns nothing, but throws exception
   * if we don't have the class
   * @throws ClassNotFoundException if we don't have the org.sqlite.JDBC class
   */
  private void checkClass() throws ClassNotFoundException {
    // check if we have an org.sqlite.JDBC class
    // (throws ClassNotFoundException if no)
    Class.forName("org.sqlite.JDBC");
  }

  /**
   * method to report class not found error!
   * @param e the ClassNotFoundException that was thrown!
   */
  private void classNotFoundExceptionHandler(ClassNotFoundException e) {
    // error! we don't have an org.sqlite.JDBC class!
    System.err.println("ERROR: Could not load SQLite JDBC driver:");
    System.out.println(e.getMessage());
  }

  /**
   * method to check if this.conn is null. returns nothing, but throws exception
   * if this.conn is null
   * @throws SQLException if this.conn is null
   */
  public void checkConn() throws SQLException {
    // verify that we actually have a conn
    if (this.conn == null) {
      // there is no conn to close!
      throw new SQLException("this.conn == null");
    }
  }

  /**
   * method to report sql error!
   * @param e the SQLException that was thrown!
   */
  private void SQLExceptionHandler(SQLException e, String message) {
    // error!
    System.out.println(message + ":");
    System.out.println(e.getMessage());
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
   * @param filepath the filepath of the DB to be created
   * @return a boolean indicating success of creation
   */
  public boolean createDB(String filepath) {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      checkClass();

      // conn represents our connection to the database itself
      Connection conn = null;

      // check if database already exists
      File f = new File(System.getProperty("user.dir") + filepath);
      if (f.exists()) {
        // if it already exists, throw error. We don't want to connect to an
        // existing database, we want to create a new one!
        throw new SQLException("database '" + System.getProperty("user.dir") + filepath + "' already exists!");
      }

      // calculate path to db (adding "jdbc:sqlite:" is required by the library)
      String url = "jdbc:sqlite:" + System.getProperty("user.dir") + filepath;
      // create a connection to the database
      // (throws SQLException if can't connect to DB)
      conn = DriverManager.getConnection(url);
      // update this.conn
      this.conn = conn;

      // success!
      System.out.println("Success! New SQLite DB has been created");
      return true;
    } catch (SQLException e) {
      // error! we couldn't create the new DB
      SQLExceptionHandler(e, "ERROR: New SQLite DB could not be created");
      return false;
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      classNotFoundExceptionHandler(e);
      return false;
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
   * @param filepath the relative filepath of the database, relative to the project root
   * @return a boolean indicating if we could connect or not
   */
  public boolean connectDB(String filepath) {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      checkClass();

      // conn represents our connection to the database itself
      Connection conn = null;

      // check if database already exists
      File f = new File(System.getProperty("user.dir") + filepath);
      if (!f.exists()) {
        // if it already exists, throw error. We don't want to create a new database,
        // we want to connect to an existing one!
        throw new SQLException("database '" + System.getProperty("user.dir") + filepath + "' does not exist!");
      }

      // calculate path to db (adding "jdbc:sqlite:" is required by the library)
      String url = "jdbc:sqlite:" + System.getProperty("user.dir") + filepath;
      // create a connection to the database
      // (throws SQLException if can't connect to DB)
      conn = DriverManager.getConnection(url);
      // update this.conn
      this.conn = conn;

      // success!
      System.out.println("Success! Connection to SQLite DB has been established");
      return true;
    } catch (SQLException e) {
      // error! we couldn't connect to the specified DB
      SQLExceptionHandler(e, "ERROR: Connection to SQLite DB could not be established");
      return false;
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      classNotFoundExceptionHandler(e);
      return false;
    }
  }

  /**
   * Function to close this.conn
   *
   * @return boolean indicating success of closure
   */
  public boolean closeDB() {
    // try to close this.conn!
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkClass();

      // verify that we actually have a conn
      // (throws exception if no)
      this.checkConn();

      // close, set this.conn to null
      this.conn.close();
      this.conn = null;

      // return success!
      return true;
    } catch (SQLException e) {
      // error! we couldn't close connection to the DB (this.conn)
      SQLExceptionHandler(e, "ERROR: Couldn't close this.conn");
      return false;
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      classNotFoundExceptionHandler(e);
      return false;
    }
  }

  /**
   * method to check if a table exists, returning a boolean indicating existence
   * @param name the name of the table to be checked
   * @return boolean indicating existence of table
   * @throws SQLException if the existence of the table could not be determined
   * -----------------------------------------------------------------------------
   * <a href="https://stackoverflow.com/questions/27007931/java-check-table-existence-in-sqlite">...</a>
   * -----------------------------------------------------------------------------
   */
  public boolean tableExists(String name) throws SQLException {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkClass();

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
      // error! we couldn't check for table's existence (this.conn)
      SQLExceptionHandler(e, "ERROR: Couldn't check if table '" + name + "' exists");
      if (this.conn == null) {
        return false;
      } else {
        throw e;
      }
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      classNotFoundExceptionHandler(e);
      return false;
    }
  }

  /**
   * Create a new table in the connected DB, returning boolean indicating success
   * of creation. Fails if no this.conn!
   * -----------------------------------------------------------------------------
   * Source material: <a href="https://www.sqlitetutorial.net/sqlite-java/create-table/">...</a>
   * -----------------------------------------------------------------------------
   * @param name   the name of the table to be created
   * @param schema the schema of the table to be created
   *               EXAMPLE SCHEMA:
   *               String schema = "id integer PRIMARY KEY,\n"
   *                             + "name text NOT NULL,\n"
   *                             + "capacity real";
   * @return a boolean indicating success of table creation
   */
  public boolean createNewTable(String name, String schema) {
    try {
      // check if we have an org.sqlite.JDBC class
      // (throws ClassNotFoundException if no)
      this.checkClass();

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
      SQLExceptionHandler(e, "ERROR: Couldn't create new table '" + name + "'");
      return false;
    } catch (ClassNotFoundException e) {
      // error! we don't have an org.sqlite.JDBC class!
      classNotFoundExceptionHandler(e);
      return false;
    }
  }

  /**
   * method to insert...
   * -----------------------------------------------------------------------------
   * Source material: <a href="https://www.sqlitetutorial.net/sqlite-java/insert/">...</a>
   * -----------------------------------------------------------------------------
   */
  public boolean insert() {
    return false;
  }

  /**
   * method to select...
   * -----------------------------------------------------------------------------
   * Source material: <a href="https://www.sqlitetutorial.net/sqlite-java/select/">...</a>
   * -----------------------------------------------------------------------------
   */
  public boolean select() {
    return false;
  }

  /**
   * method to update...
   * -----------------------------------------------------------------------------
   * Source material: <a href="https://www.sqlitetutorial.net/sqlite-java/update/">...</a>
   * -----------------------------------------------------------------------------
   */
  public boolean update() {
    return false;
  }

  /**
   * method to delete...
   * -----------------------------------------------------------------------------
   * Source material: <a href="https://www.sqlitetutorial.net/sqlite-java/delete/">...</a>
   * -----------------------------------------------------------------------------
   */
  public boolean delete() {
    return false;
  }
}