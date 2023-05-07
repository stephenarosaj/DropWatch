package edu.brown.cs.student.api.endpointHandlers.ExternalAPI;

import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.exceptions.APIRequestException;
import okio.Buffer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class SpotifyAPIRequester implements SpotifyDataSource {

    String accessToken;
    public SpotifyAPIRequester() {
        this.accessToken = this.getAccessMap().get("access_token");
    }

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

            Buffer buf = new Buffer().readFrom(urlc.getInputStream());

            writer.close();

            return MoshiUtil.deserializeToken(buf);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public void setAccessToken(String token) {
        // allows us to differentiate between a general access token and a user access token
        this.accessToken = token;
    }

    @Override
    public Buffer getData(String urlString) throws APIRequestException {
        try {
        URL url = new URL(urlString);

        URLConnection urls = url.openConnection();

        urls.setDoOutput(true);
        //set access token before reading input stream
        urls.setRequestProperty("Authorization","Bearer  " +
                this.accessToken);

        return new Buffer().readFrom(urls.getInputStream());
        } catch (IOException e) {
            throw new APIRequestException(e.getMessage());
        }
    }
}
