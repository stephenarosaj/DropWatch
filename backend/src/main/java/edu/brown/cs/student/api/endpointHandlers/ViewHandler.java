package edu.brown.cs.student.api.endpointHandlers;

import edu.brown.cs.student.CSVparser.Parser;
import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.rowCreation.FactoryFailureException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class that handles requests to view a previously loaded CSV. */
public class ViewHandler implements Route {

  LoadHandler loadHandler;

  /**
   * Creates a ViewHandler.
   *
   * @param inputLoadHandler The LoadHandler from which to get the CSV file.
   */
  public ViewHandler(LoadHandler inputLoadHandler) {
    loadHandler = inputLoadHandler;
  }

  /**
   * Handles requests to view a CSV file.
   *
   * @param request Browser HTTP request.
   * @param response The response object providing functionality for modifying the response.
   * @return String JSON returning results and the CSV.
   */
  @Override
  public Object handle(Request request, Response response) {
    Reader file;
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("data", "");
    responseMap.put("details", "");

    try {
      file = loadHandler.getCSVfile();
    } catch (FileNotFoundException e) {
      responseMap.put("details", "no file loaded");
      return MoshiUtil.serialize(responseMap, "error_datasource");
    }

    Parser<List<String>> parser;
    try {
      parser = new Parser<>(file);
    } catch (IOException e) {
      responseMap.put("details", "failed to read header line");
      return MoshiUtil.serialize(responseMap, "error_parse");
    }

    try {
      responseMap.put("data", parser.parse());
    } catch (IOException e) {
      responseMap.put("details", "I/O Exception ocurred while parsing");
      return MoshiUtil.serialize(responseMap, "error_parse");
    } catch (FactoryFailureException e) {
      responseMap.put("details", "factory failure in parsing: " + e.getMessage());
      return MoshiUtil.serialize(responseMap, "error_parse");
    }
    return MoshiUtil.serialize(responseMap, "success");
  }
}
