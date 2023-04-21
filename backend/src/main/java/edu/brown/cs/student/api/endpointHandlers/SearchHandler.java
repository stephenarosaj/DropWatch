package edu.brown.cs.student.api.endpointHandlers;

import edu.brown.cs.student.CSVparser.*;
import edu.brown.cs.student.CSVsearch.Searcher;
import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.exceptions.BadColumnException;
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

/** The class that handles requests to search a previously loaded CSV. */
public class SearchHandler implements Route {

  LoadHandler loadHandler;

  /**
   * Creates a SearchHandler.
   *
   * @param inputLoadHandler The LoadHandler from which to get the CSV file.
   */
  public SearchHandler(LoadHandler inputLoadHandler) {
    loadHandler = inputLoadHandler;
  }

  /**
   * Handles requests to search a previously loaded CSV.
   *
   * @param request Browser HTTP request; should contain target value, whether the CSV has headers,
   *     and optionally a column.
   * @param response The response object providing functionality for modifying the response.
   * @return A String JSON with success response, search parameters, and targetRows.
   */
  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> responseMap = new HashMap<>();

    String target = request.queryParams("target");
    String hasHeaders = request.queryParams("has_headers");

    responseMap.put("details", "");
    responseMap.put("has_headers", "");
    responseMap.put("data", "");
    responseMap.put("target", "");

    // error checking fields
    if (target == null) {
      responseMap.put("column", "");
      responseMap.put("details", "must have target value");
      return MoshiUtil.serialize(responseMap, "error_bad_request");
    }

    if (hasHeaders == null) {
      responseMap.put("column", "");
      responseMap.put("details", "must specify whether the CSV has headers or not");
      return MoshiUtil.serialize(responseMap, "error_bad_request");
    }

    if (!hasHeaders.equals("true") && !hasHeaders.equals("false")) {
      responseMap.put("column", "");
      responseMap.put("details", "has_headers must be true or false");
      return MoshiUtil.serialize(responseMap, "error_bad_request");
    }

    String column = request.queryParams("column");

    return getTargetJSON(responseMap, target, hasHeaders, column);
  }

  /**
   * Searches the previously loaded CSV file and populates the responseMap according to results.
   *
   * @param responseMap Response Map to add info to.
   * @param target Target value to search for.
   * @param hasHeaders Boolean representing whether the CSV has headers.
   * @param column (Optional) Column to restrict search to.
   * @return String JSON containing result, parameters, and targetRows.
   */
  private String getTargetJSON(
      Map<String, Object> responseMap, String target, String hasHeaders, String column) {
    Reader file;

    Boolean boolHeaders = hasHeaders.equals("true") ? true : false;

    Searcher searcher = new Searcher(target);

    try {
      file = loadHandler.getCSVfile();
    } catch (FileNotFoundException e) {
      responseMap.put("details", "no file loaded");
      return MoshiUtil.serialize(responseMap, "error_datasource");
    }

    Parser<List<String>> parser;
    try {
      parser = new Parser<>(file, boolHeaders, searcher);
    } catch (IOException e) {
      responseMap.put("details", "failed to read header line");
      return MoshiUtil.serialize(responseMap, "error_parse");
    }

    if (column != null) {
      responseMap.put("column", column);
      try {
        searcher.setCol(column, parser.getHeaders());
      } catch (BadColumnException e) {
        responseMap.put("details", e.getMessage());
        return MoshiUtil.serialize(responseMap, "error_bad_column");
      }
    } else {
      responseMap.put("column", "");
    }

    try {
      parser.parse();
    } catch (IOException e) {
      responseMap.put("details", "I/O exception ocurred while parsing");
      return MoshiUtil.serialize(responseMap, "error_parse");
    } catch (FactoryFailureException e) {
      responseMap.put("details", "factory failure in parsing: " + e.getMessage());
      return MoshiUtil.serialize(responseMap, "error_parse");
    }
    responseMap.put("data", searcher.getTargetRows());
    return MoshiUtil.serialize(responseMap, "success");
  }
}
