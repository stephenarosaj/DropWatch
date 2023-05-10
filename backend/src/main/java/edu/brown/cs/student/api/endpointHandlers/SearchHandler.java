package edu.brown.cs.student.api.endpointHandlers;

import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.endpointHandlers.ExternalAPI.SpotifyAPIRequester;
import edu.brown.cs.student.api.endpointHandlers.ExternalAPI.SpotifyDataSource;
import edu.brown.cs.student.api.exceptions.APIRequestException;
import edu.brown.cs.student.api.exceptions.DeserializeException;
import edu.brown.cs.student.api.formats.SearchRecord;
import java.util.HashMap;
import java.util.Map;
import okio.Buffer;
import spark.Request;
import spark.Response;
import spark.Route;

/** Class for SearchHandler - handles searching spotify's api for artists, tracks, and albums! */
public class SearchHandler implements Route {

  /** The spotify data source for this SearchHandler class - holds responses from the spotify API */
  SpotifyDataSource SpotifyAPIRequester;

  // EXAMPLE RESPONSES FROM SPOTIFY API CAN BE FOUND IN MOCKS!!!

  /**
   * Constructor for this class
   *
   * @param SpotifyAPIRequester the buffer that represents data incoming from spotify's API
   */
  public SearchHandler(SpotifyDataSource SpotifyAPIRequester) {
    this.SpotifyAPIRequester = SpotifyAPIRequester;
  }

  /**
   * Function that handles searching. Will take the users query params and use them to call the
   * spotify API.
   *
   * @param request the request made by the user to this endpoint
   * @param response the response to the user via this endpoint
   */
  @Override
  public Object handle(Request request, Response response) {
    // the return map, will be turned into JSON
    Map<String, Object> ret = new HashMap<String, Object>();
    // add our params to the map!
    ret.put("params", request.queryMap().toMap());

    try {
      // grab input params
      String rawQuery = request.queryParams("query");
      String offset = request.queryParams("offset");

      // number of params
      int nParams = request.queryParams().size();
      // error check params - we should have exactly 2
      if (nParams != 2) {
        // return error!
        return MoshiUtil.serialize(
            ret, "ERROR: /search endpoint requires exactly 2 params, but received " + nParams);
      }

      // error check params - we should have rawQuery and offset
      if (rawQuery == null || offset == null) {
        // return error!
        return MoshiUtil.serialize(
            ret,
            "ERROR: /search endpoint requires params 'query' and 'offset', but did not receive them both");
      }

      // error check offset - should be an int!
      try {
        int parsedOffset = Integer.parseInt(offset);
        if (parsedOffset < 0) {
          throw new NumberFormatException("ERROR: parsedOffset < 0 - we need a positive number!");
        }
      } catch (NumberFormatException e) {
        // offset is not a number!
        System.out.println(e.getMessage());
        return MoshiUtil.serialize(
            ret,
            "ERROR: /search endpoint requires param 'offset' to be a number >= 0, but recevied '"
                + offset
                + "'");
      }

      // our params are safe - let's execute a search!
      // can't use spaces, must use +
      String query = rawQuery.replace(' ', '+');

      // make the request!
      String urlString =
          "https://api.spotify.com/v1/search?"
              + "query="
              + query
              + "&type=artist,album,track"
              + "&market=US"
              + "&limit=8"
              + "&offset="
              + offset;
      Buffer buf = this.SpotifyAPIRequester.getData(urlString);

      // deserialize the api's JSON response
      SearchRecord searchResponse = MoshiUtil.deserializeSearch(buf);

      // add the data to our response map
      ret.put("data", searchResponse);

      // return our response map!
      return MoshiUtil.serialize(ret, "success");
    } catch (APIRequestException e) {
      return MoshiUtil.serialize(ret, "ERROR (API Request to Search): " + e.getMessage());
    } catch (DeserializeException e) {
      return MoshiUtil.serialize(ret, "ERROR (Deserializing Search): " + e.getMessage());
    }
  }
}
