package edu.brown.cs.student.api.endpointHandlers;

import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.endpointHandlers.ExternalAPI.SpotifyAPIRequester;
import edu.brown.cs.student.api.endpointHandlers.ExternalAPI.SpotifyDataSource;
import edu.brown.cs.student.api.formats.SearchRecord;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import okio.Buffer;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Class for SearchHandler - handles searching spotify's data!
 */
public class SearchHandler implements Route {

  /**
   * The spotify data source for this SearchHandler class - holds responses from the spotify API
   */
  SpotifyDataSource SpotifyAPIRequester;

  // EXAMPLE RESPONSES FROM SPOTIFY API CAN BE FOUND IN MOCKS!!!

  /**
   * Constructor for this class
   * @param SpotifyAPIRequester the buffer that represents data incoming from spotify's API
   */
  public SearchHandler(SpotifyDataSource SpotifyAPIRequester) {
    this.SpotifyAPIRequester = SpotifyAPIRequester;
  }

//  // get the access token - REQUIRED TO MAKE ANY API CALL
//  public Map<String, String> getAccessMap() {
//    String client_id = "1be4c1544f31438693f0c3b488f9ceee";
//    String client_secret = "c44a4bd0073440178a7c3477202b7a74";
//    // https://stackoverflow.com/questions/65750837/how-to-use-this-curl-post-request-in-java-spotify-api
//    try {
//      URL url = new URL("https://accounts.spotify.com/api/token");
//      URLConnection urlc = url.openConnection();
//
//      urlc.setDoOutput(true);
//      urlc.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded");
//
//      OutputStreamWriter writer = new OutputStreamWriter(urlc.getOutputStream());
//
//      writer.write("grant_type=client_credentials&client_id="+client_id+"&client_secret="+client_secret);
//      writer.flush();
//
//      Buffer buf = new Buffer().readFrom(urlc.getInputStream());
//
//      writer.close();
//
//      return MoshiUtil.deserializeToken(buf);
//    } catch (Exception e) {
//      System.out.println(e.getMessage());
//      return null;
//    }
//  }

  /**
   * Function that handles searching. Will take the users query params and use them
   * to call the spotify API.
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

      // grab request url
      String url = request.url();

      // number of params
      int nParams = request.queryParams().size();
      // error check params - we should have exactly 2
      if (nParams != 2) {
        // return error!
        return MoshiUtil.serialize(ret, "ERROR: /search endpoint requires exactly 2 params, but did receive 2 (received " + nParams + ")");
      }

      // error check params - we should have rawQuery and offset
      if (rawQuery == null || offset == null) {
        // return error!
        return MoshiUtil.serialize(ret, "ERROR: /search endpoint requires params 'query' and 'offset', but did not receive them both");
      }

      // our params are safe - let's execute a search!
      // can't use spaces, must use +
      String query = rawQuery.replace(' ', '+');

      // make the request!
      Buffer buf = this.SpotifyAPIRequester.getData("https://api.spotify.com/v1/search?"
              + "query=" + query
              + "&type=artist,album,track"
              + "&market=US"
              + "&limit=8"
              + "&offset=" + offset);

      // deserialize the api's JSON response
      SearchRecord searchResponse = MoshiUtil.deserializeSearch(buf);

      // add the data to our response map
      ret.put("data", searchResponse);

      // return our response map!
      return MoshiUtil.serialize(ret, "success");
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
  }
}
