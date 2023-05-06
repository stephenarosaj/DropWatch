package edu.brown.cs.student.api.endpointHandlers;

import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.database.DropWatchDB;
import edu.brown.cs.student.api.endpointHandlers.ExternalAPI.SpotifyDataSource;
import edu.brown.cs.student.api.exceptions.APIRequestException;
import edu.brown.cs.student.api.formats.AlbumRecord;
import edu.brown.cs.student.api.formats.DateRecord;
import okio.Buffer;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;

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
     * The filepath for our DropWatchDB!
     */
    private String filepath = "data/DropWatchDB.db";

    /**
     * The db we use to store stuff!
     */
    private DropWatchDB db;

    /**
     * Constructor for this class
     */
    public UpdateHandler() {
        try {
            this.db = new DropWatchDB(filepath);
        } catch (Exception e) {
            System.out.println("CRITICAL ERROR: COULD NOT SET UP CONNECTION TO DB '" + filepath + "', ABORTING UpdateHandler()!\n");
            System.out.println(e.getMessage());
            exit(1);
        }
    }

    /**
     * Function that checks for a new release
     * @param artist_id the artist who we want to check
     * @return if there is a new release, returns a list where the first value is
     * the old release date and the second is the newest release date, or null if
     * there is no new release, and the third value is the precision of the new date
     */
    public ArrayList<String> checkNewRelease(String artist_id) throws APIRequestException, IOException, SQLException, ClassNotFoundException {
        // make the request!
        // we want just 1 album - the most recent one!
        String urlString = "https://api.spotify.com/v1/artists/"
          + artist_id + "/albums?"
          + "&market=US"
          + "&limit=" + 1
          + "&offset=0";
        Buffer buf = this.SpotifyAPIRequester.getData(urlString);
        AlbumRecord album = MoshiUtil.deserializeUpdate(buf);

        // parse request results to find the latest release date (being mindful of precision)

        //String newDate = album.release_date();
        //String newDatePrecision = album.release_date_precision();
        DateRecord release = new DateRecord(album.release_date(), album.release_date_precision());

        // grab stored latest release date
        // TODO: implement DropWatchDB.findLatestRelease()
        String[] oldDateList = this.db.findLatestRelease(artist_id);
        String oldDate = oldDateList[0];
        String oldDatePrecision = oldDateList[1];

        // TODO: do something inside these if statements
        // compare old and new dates. if there is a new release, return both!
        // since the possible values are "day", "month", and "year", and when
        // lexicographically ordered they are also ordered in terms of precision,
        // we can use String.compareTo, which compares lexicographic ordering,
        // to determine which is more precise
//        if (newDatePrecision == null || oldDatePrecision == null || newDate == null || oldDate == null) {
//            // error!
//        } else if (oldDatePrecision.compareTo(newDatePrecision) < 0) {
//            // if this is true, we could have "day".compareTo("month")
//            // new date is less precise than old date
//        } else if (oldDatePrecision.compareTo(newDatePrecision) > 0) {
//            // if this is true, we could have "year".compareTo("month")
//            // new date is more precise than old date
//        } else {
//            // same precision
//        }
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
            ArrayList<String> artist_ids = this.db.queryTracking(user, true);

            // map containing new drops!
            HashMap<String, ArrayList<AlbumRecord>> drops = new HashMap<String, ArrayList<AlbumRecord>>();

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
                        String newDatePrecision = dates.get(2);

                        // TODO: implement DropWatchDB.findLatestRelease()
                        // TODO: implement notifyUser()
                        // if they have new music, update albums and artistAlbums
                            // FOR NOW:
                        String album_id = null;
                        String link = null;
                        this.db.addNewAlbum(artist_id, album_id, newDate, newDatePrecision, link);

                        // TODO: implement function to do this
                        // now we have to use the old and new dates to grab all artist's
                        // music in the range (oldDate, newDate]
                        ArrayList<AlbumRecord> newAlbums = new ArrayList<AlbumRecord>();

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
