package edu.brown.cs.student.api.endpointHandlers;

import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.endpointHandlers.ExternalAPI.SpotifyDataSource;
import edu.brown.cs.student.api.exceptions.APIRequestException;
import edu.brown.cs.student.api.exceptions.DeserializeException;
import edu.brown.cs.student.api.formats.ArtistFollowRecord;
import edu.brown.cs.student.api.formats.SearchRecord;
import okio.Buffer;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

/**
 * The endpoint that servers user data (i.e., the user's following, playlists, etc.)
 */
public class UserDataHandler implements Route {

    SpotifyDataSource SpotifyAPIRequester;

    /**
     * Constructor that takes in an object that returns a SpotifyAPI buffer response.
     * @param SpotifyAPIRequester Object that returns a SpotifyAPI buffer response, for mocking purposes.
     */
    public UserDataHandler(SpotifyDataSource SpotifyAPIRequester) {
        this.SpotifyAPIRequester = SpotifyAPIRequester;
    }

    /**
     * Called whenever this endpoint is called.
     * @param request HTTP URL request.
     * @param response
     * @return A JSON containing the user's followed spotify artists or an error message.
     */
    @Override
    public Object handle(Request request, Response response) {
        // the return map, will be turned into JSON
        Map<String, Object> ret = new HashMap<String, Object>();
        // add our params to the map!
        ret.put("params", request.queryMap().toMap());
        try {
            // grab input params
            String userToken = request.queryParams("user_token");
            String urlString = "https://api.spotify.com/v1/me/following?type=artist&limit=10";

            SpotifyAPIRequester.setAccessToken(userToken);
            Buffer buf = this.SpotifyAPIRequester.getData(urlString);

            // deserialize the api's JSON response
            ArtistFollowRecord artistFollowResponse = MoshiUtil.deserializeArtistsFollow(buf);

            // add the data to our response map
            ret.put("data", artistFollowResponse.artistsJSON().artists());

            // return our response map!
            return MoshiUtil.serialize(ret, "success");
        } catch (APIRequestException e) {
            return MoshiUtil.serialize(ret, "ERROR (API Request to User's Followed Artists): "
                    + e.getMessage());
        } catch (DeserializeException e) {
            return MoshiUtil.serialize(ret, "ERROR (Deserializing User's Followed Artists): "
                    + e.getMessage());
        }
    }
}
