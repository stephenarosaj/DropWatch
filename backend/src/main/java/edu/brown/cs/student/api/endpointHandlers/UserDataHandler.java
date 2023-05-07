package edu.brown.cs.student.api.endpointHandlers;

import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.endpointHandlers.ExternalAPI.SpotifyDataSource;
import edu.brown.cs.student.api.formats.SearchRecord;
import okio.Buffer;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class UserDataHandler implements Route {

    SpotifyDataSource SpotifyAPIRequester;

    public UserDataHandler(SpotifyDataSource SpotifyAPIRequester) {
        this.SpotifyAPIRequester = SpotifyAPIRequester;
    }
    @Override
    public Object handle(Request request, Response response) throws Exception {
        // the return map, will be turned into JSON
        Map<String, Object> ret = new HashMap<String, Object>();
        // add our params to the map!
        ret.put("params", request.queryMap().toMap());
        try {
            // grab input params
            String userToken = request.queryParams("user_token");
            String urlString = "https://api.spotify.com/v1/me/playlists?limit=10";

            SpotifyAPIRequester.setAccessToken("BQCHhFkbgXJiwcIUBvIm66fihghfMHZfV9smt9vgv-k9tjtLxab1i0ddynbavOj9uAu1e8JBubp04gbxWk_7GeKRcZSsqbhx5zpqGLhrBCNBgjOfGBd0StFta-iM8jkZatwxDnUSV-3KvNf9R7ItuxI_QXB7fr2TVVOgcsJ7Sj6d1aZR7ir5UTelxEcRHJEFiXy0Lq1SlDc8tK7wgHfV");
            Buffer buf = this.SpotifyAPIRequester.getData(urlString);

            // deserialize the api's JSON response
            SearchRecord searchResponse = MoshiUtil.deserializeSearch(buf);

            // add the data to our response map
            ret.put("data", searchResponse);

            // return our response map!
            return MoshiUtil.serialize(ret, "success");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return MoshiUtil.serialize(ret, "ERROR: " + e.getMessage());
        }
    }
}
