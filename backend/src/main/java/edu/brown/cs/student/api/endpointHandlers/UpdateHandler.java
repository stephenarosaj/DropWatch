package edu.brown.cs.student.api.endpointHandlers;

import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.database.DropWatchDB;
import edu.brown.cs.student.api.endpointHandlers.ExternalAPI.SpotifyDataSource;
import edu.brown.cs.student.api.exceptions.APIRequestException;
import edu.brown.cs.student.api.formats.AlbumRecord;
import okio.Buffer;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for UpdateHandler - handles checking for new music releases!
 *      - a check for new music is made and new music is found
 *          - the artist's latest release date is updated in latest_release table
 *          - all the users in the artist's trackers table are notified
 */
public class UpdateHandler implements Route {

    /**
     * The spotify data source for this UpdateHandler class - holds responses from the spotify API
     */
    private SpotifyDataSource SpotifyAPIRequester;

    /**
     * The db we use to store stuff!
     */
    private DropWatchDB db = new DropWatchDB();

    /**
     * Constructor for this class
     */
    public UpdateHandler() {}

    /**
     * Function that checks for a new release
     * @param artist_id the artist who we want to check
     * @return if there is a new release, returns a list where the first value is
     * the old release date and the second is the newest release date, or null if
     * there is no new release
     */
    public ArrayList<String> checkNewRelease(String artist_id) throws APIRequestException, IOException {
        // make the request!
        // we want just 1 album - the most recent one!
        String urlString = "https://api.spotify.com/v1/artists/"
          + artist_id + "/albums?"
          + "&market=US"
          + "&limit=" + 1
          + "&offset=0";
        Buffer buf = this.SpotifyAPIRequester.getData(urlString);
        AlbumRecord album = MoshiUtil.deserializeUpdate(buf);

        // TODO: Review code below
        // parse request results to find the latest release date
        // TODO: To my understanding, this request should only return one result which is the latest album.
        // So I thought we could access the date with the call below but I'm unsure
        String newDate = album.getReleaseDate();


        // TODO:
        // compare old and new dates. if there is a new release, return both!
        // TODO: implement DropWatchDB.queryLatestRelease()
        String oldDate = this.db.queryLatestRelease(artist_id);
        return null;
    }

    /***
     * Handles calls to update endpoint. Refreshes database to have most updated
     * latest release data for specified artist.
     * @param request - Request received from user.
     * @param response -
     */
    @Override
    public Object handle(Request request, Response response) {
        // the return map, will be turned into JSON
        Map<String, Object> ret = new HashMap<String, Object>();
        // add our params to the map!
        ret.put("params", request.queryMap().toMap());

        try {
            // grab input params
            String user = request.queryParams("user");

        // NOTE: I think we should search by artist id like Aku suggested, and
        // this should be taken from the user's tracking table
            // TODO: implement DropWatchDB.queryTracking()
            // query db for list of artist ids being tracked by this user
            ArrayList<String> artist_ids = this.db.queryTracking(user);

            // map containing new drops!
            HashMap<String, AlbumRecord> drops = new HashMap<String, AlbumRecord>();

            // for each artist id, check for new music
            for (String artist_id: artist_ids) {
                ArrayList<String> dates = null;
                if ((dates = checkNewRelease(artist_id)) != null) {
                    try {
                        // TODO: I kind of arbitrarily said our dates would be strings, but if we want to use another type
                        // TODO: to represent them we should! It seems like parsing dates as strings might be kinda annoying,
                        // TODO: and it won't be the same process every time because the spotify api might return a date
                        // TODO: like "4/20/69" or "4/69" or just "69". The good news is that it returns a "release_date_precision"
                        // TODO: that tells us which format it's using for the date!
                        // grab new and old dates from return of checkNewRelease!
                        String oldDate = dates.get(0);
                        String newDate = dates.get(1);

                        // TODO: implement DropWatchDB.updateLatestRelease()
                        // TODO: implement notifyUser()
                        // if they have new music, update latest release date for that artist
                        this.db.updateLatestRelease(artist_id, newDate);

                        // TODO: implement function to do this
                        // now we have to use the old and new dates to grab all artist's
                        // music in the range (oldDate, newDate]
                        AlbumRecord newAlbums = null;

                        // update drops so that we can return the frontend some new info!
                        drops.put(artist_id, newAlbums);
                    } catch (Exception e) {
                        // TODO: we probably shouldn't exit - keep going through artists but at least print error out to console
                        // something went wrong!
                        System.out.println(e.getMessage());
                    }
                }
            }

            // add new drops to our map!
            ret.put("data", drops);
            // TODO: notify here? or somewhere else?
            // notifyUser(user, drops);
            return MoshiUtil.serialize(ret, "success");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
