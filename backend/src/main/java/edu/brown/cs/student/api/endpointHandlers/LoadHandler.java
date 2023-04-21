package edu.brown.cs.student.api.endpointHandlers;

import edu.brown.cs.student.api.MoshiUtil;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** The class that handles calls to loadcsv. */
public class LoadHandler implements Route {

  private String CSVfilepath;

  /** Constructs a new LoadHandler with empty filepath. */
  public LoadHandler() {
    CSVfilepath = "";
  }

  /**
   * Handles call to loadcsv by loading the requested filepath into the String variable.
   *
   * @param request Browser request; should contain a filepath param.
   * @param response Browser response.
   * @return A String JSON containing a success or error response and requested filepath.
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();
    String filepath = request.queryParams("filepath");
    if (filepath == null || filepath.equals("") || filepath.equals("undefined")) {
      // had to add undefined - this is how TypeScript sends empty strings :<
      responseMap.put("filepath", "");
      return MoshiUtil.serialize(responseMap, "error_bad_request");
    }
    responseMap.put("filepath", filepath);
    try {
      CSVfilepath = filepath;
      // sanity check this is a valid filepath
      getCSVfile();

      return MoshiUtil.serialize(responseMap, "success");
    } catch (IOException e) {
      return MoshiUtil.serialize(responseMap, "error_datasource");
    }
  }

  /**
   * Accessor for the CSV FileReader.
   *
   * @return CSV FileReader.
   * @throws FileNotFoundException If the filepath is empty or wasn't found.
   */
  public Reader getCSVfile() throws FileNotFoundException {
    return new FileReader(CSVfilepath);
  }
}
