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

import java.io.File;
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
     * A few helper methods in this util!
     */
    private HandlerUtil util;

    /**
     * Constructor for this class
     */
    public UpdateHandler() {
        try {
            // check for mac or windows file path
            if (new File(filepath).exists()) {
                // windows project root = .../DropWatch/backend
                this.db = new DropWatchDB(filepath);
            } else if (new File("backend/" + filepath).exists()) {
                // mac project root = .../DropWatch
                this.db = new DropWatchDB("backend/" + filepath);
            } else {
                throw new SQLException("ERROR: Couldn't find DropWatchDB.db file!");
            }
            // give our handler this db!
            this.util = new HandlerUtil(this.db);
        } catch (Exception e) {
            System.out.println("CRITICAL ERROR: COULD NOT SET UP CONNECTION TO DB '" + filepath + "', ABORTING UpdateHandler()!\n");
            System.out.println(e.getMessage());
            exit(1);
        }
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

            // error check params - we should have user_id
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
                if ((artistNewReleases = util.checkNewRelease(artist_id, storedDate)).size() != 0) {
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
