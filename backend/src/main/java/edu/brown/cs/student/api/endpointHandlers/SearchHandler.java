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

public class SearchHandler implements Route {

  SpotifyDataSource SpotifyAPIRequester;

  public SearchHandler(SpotifyDataSource SpotifyAPIRequester) {
    this.SpotifyAPIRequester = SpotifyAPIRequester;
  }

  // get the access token - REQUIRED TO MAKE ANY API CALL
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

  @Override
  public Object handle(Request request, Response response) {
    Map<String, Object> ret = new HashMap();

    try {

      // need to deal with null query / offset
      String rawQuery = request.queryParams("query");
      String offset = request.queryParams("offset");

      String query = rawQuery.replace(' ', '+');

      Buffer buf = this.SpotifyAPIRequester.getData("https://api.spotify.com/v1/search?"
              + "query=" + query
              + "&type=artist"
              + "&market=US"
              + "&offset=" + offset);

      SearchRecord searchResponse = MoshiUtil.deserializeSearch(buf);

      ret.put("data", searchResponse);

      return MoshiUtil.serialize(ret, "success");
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
  }
}
