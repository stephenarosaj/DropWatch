package edu.brown.cs.student.api.endpointHandlers;

import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.formats.SearchRecord;
import edu.brown.cs.student.api.formats.UpdateRecord;
import okio.Buffer;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class UpdateHandler implements Route {

    public UpdateHandler() {}

    /***
     * Retrieves the token. The token is required to make any API Call.
     * @return - access token
     */
    public Map<String, String> getAccessMap() {
        String client_id = "1be4c1544f31438693f0c3b488f9ceee";
        String client_secret = "c44a4bd0073440178a7c3477202b7a74";
        // https://stackoverflow.com/questions/65750837/how-to-use-this-curl-post-request-in-java-spotify-api
        try {
            URL url = new URL("https://accounts.spotify.com/api/token");
            URLConnection urlc = url.openConnection();

            urlc.setDoOutput(true);
            urlc.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded");

            OutputStreamWriter writer = new OutputStreamWriter(urlc.getOutputStream());

            writer.write("grant_type=client_credentials&client_id="+client_id+"&client_secret="+client_secret);
            writer.flush();

            okio.Buffer buf = new Buffer().readFrom(urlc.getInputStream());

            writer.close();

            return MoshiUtil.deserializeToken(buf);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /***
     * Handles calls to update endpoint.
     * @param request - Request received from user.
     * @param response -
     */
    @Override
    public Object handle(Request request, Response response) {
        Map<String, Object> ret = new HashMap<>();

        try {
            // check to see what might need to be done with these params
            String limit = request.queryParams("limit");
            String offset = request.queryParams("offset");

            String artist_id = ""; // needs to be instantiated properly, need to retrieve the artist id from somewhere

            // an idea is to make ArtistRecord.java? to externalize some code existing in SearchRecord

            URL url = new URL("https://api.spotify.com/v1/artists/"
                    + artist_id + "/albums?"
                    + "&market=US"
                    + "&limit=" + limit
                    + "&offset=" + offset);

            URLConnection urls = url.openConnection();

            urls.setDoOutput(true);
            //set access token before reading input stream
            urls.setRequestProperty("Authorization","Bearer  " +
                    "BQDjG5oaXU0UU7r3fOwU-vCt_72JHENSSDxnx2AJEMwG2M3tQelrxXaRr8FWqfrkXyDkg90035jQvzrPwAtAup4CHrxJnsG1zIU3PI3njcYdIY9qKxK3");


            UpdateRecord updateResponse = MoshiUtil.deserializeUpdate(
                    new Buffer().readFrom(urls.getInputStream()));

            ret.put("data", updateResponse);

            return MoshiUtil.serialize(ret, "success");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
