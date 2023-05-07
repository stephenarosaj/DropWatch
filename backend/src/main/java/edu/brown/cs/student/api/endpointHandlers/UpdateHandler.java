package edu.brown.cs.student.api.endpointHandlers;

import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.database.DropWatchDB;
import edu.brown.cs.student.api.endpointHandlers.ExternalAPI.SpotifyDataSource;
import edu.brown.cs.student.api.exceptions.APIRequestException;
import edu.brown.cs.student.api.exceptions.DeserializeException;
import edu.brown.cs.student.api.formats.AlbumRecord;
import edu.brown.cs.student.api.formats.DateRecord;
import edu.brown.cs.student.api.formats.SearchRecord;
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
     * @return if there is a new release, returns a list of DateRecords, the first
     * entry being the new DateRecord and the second being the old DateRecord. null otherwise!
     */
    public ArrayList<AlbumRecord> checkNewRelease(String artist_id, DateRecord storedDate) throws APIRequestException, DeserializeException {
        // make our return list
        ArrayList<AlbumRecord> artistNewReleases = new ArrayList<>();
        // make the request!
        // we want just 1 album/single/appears_on/compilation - the most recent one!
        String[] groups = new String[4];
        groups[0] = "album";
        groups[1] = "single";
        groups[2] = "appears_on";
        groups[3] = "compilation";
        // for each group, check newest release
        for (int i = 0; i < 4; i++) {
            String urlString = "https://api.spotify.com/v1/artists/"
              + artist_id + "/albums?"
              + "&market=US"
              + "&limit=" + 4
              + "&offset=0"
              + "&include_groups=" + groups[i];
            // make the request and grab results
            Buffer buf = this.SpotifyAPIRequester.getData(urlString);
            SearchRecord.Albums albums = MoshiUtil.deserializeUpdate(buf);

            // for each album in the results, if the release date is newer, add it to our db and artistNewReleases!
            for (AlbumRecord album: albums.items()) {
                // grab release date
                DateRecord fetchedDate = new DateRecord(album.release_date(), album.release_date_precision());
                // compare fetched and stored dates!
                if (DateRecord.compareDates(fetchedDate, storedDate) > 0) {
                    // fetched date is more recent than stored date!
                    try {
                        // add to our list
                        artistNewReleases.add(album);
                        // add it to our db
                        this.db.addNewAlbum(album.artists(), album.id(), album.release_date(), album.release_date_precision(), album.href());
                    } catch (Exception e) {
                        // remove the latest album, continue!
                        artistNewReleases.remove(artistNewReleases.size() - 1);
                    }
                }
            }
        }
        return artistNewReleases;
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
            String user_id = request.queryParams("user_id");

            // number of params
            int nParams = request.queryParams().size();
            // error check params - we should have exactly 1
            if (nParams != 1) {
                // return error!
                return MoshiUtil.serialize(ret, "ERROR: /update endpoint requires exactly 1 param, but received " + nParams);
            }

            // error check params - we should have user
            if (user_id == null) {
                // return error!
                return MoshiUtil.serialize(ret, "ERROR: /update endpoint requires param 'user_id', but did not receive it");
            }

            // query db for list of artist ids being tracked by this user
            ArrayList<String> artist_ids = this.db.queryTracking(user_id, true);

            // map containing new drops!
            HashMap<String, ArrayList<AlbumRecord>> drops = new HashMap<String, ArrayList<AlbumRecord>>();

            // for each artist id, check for new music
            for (String artist_id: artist_ids) {
                // grab stored latest release date
                DateRecord storedDate = this.db.findLatestRelease(artist_id);

                // collect new releases for this artist in a list!
                ArrayList<AlbumRecord> artistNewReleases;
                if ((artistNewReleases = checkNewRelease(artist_id, storedDate)).size() != 0) {
                    // update drops so that we can return the frontend some new info!
                    drops.put(artist_id, artistNewReleases);
                }
            }

            // add new drops to our map!
            ret.put("data", drops);
            // TODO: notify here? or somewhere else?
            // notifyUser(user, drops);
            this.db.commit();
            return MoshiUtil.serialize(ret, "success");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return MoshiUtil.serialize(ret, "ERROR: " + e.getMessage());
        }
    }

}
