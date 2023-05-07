package edu.brown.cs.student.api.endpointHandlers;

import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.database.DropWatchDB;
import edu.brown.cs.student.api.endpointHandlers.ExternalAPI.SpotifyDataSource;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;

/**
 * Class for TrackHandler - handles tracking/untracking artists!
 *      - a user tracks a new artist
 *          - the tracking table is updated with a new (user_id, artist_id) entry
 *      - a user untracks an artist
 *          - if the user is tracking that artist (a user_id, artist_id entry exists),
 *            that entry is removed from the table
 */
public class TrackHandler implements Route {
  //TODO: track some shit man
  // each call to this is either an query of, addition to, or deletion from the
  // user's currently tracked artists

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
  public TrackHandler() {
    try {
      this.db = new DropWatchDB(filepath);
    } catch (Exception e) {
      System.out.println("CRITICAL ERROR: COULD NOT SET UP CONNECTION TO DB '" + filepath + "', ABORTING TrackHandler()!\n");
      System.out.println(e.getMessage());
      exit(1);
    }
  }

  /***
   * Handles calls to track endpoint. adds or deletes a tracking entry from the db,
   * return a JSON with a "tracked artists" field which details the tracked artists
   * after the change!
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
      String artist_id = request.queryParams("artist_id");
      String operation = request.queryParams("operation");

      // number of params
      int nParams = request.queryParams().size();
      // error check params - we should have exactly 2
      if (nParams != 3) {
        // return error!
        return MoshiUtil.serialize(ret, "ERROR: /track endpoint requires exactly 3 params, but received " + nParams);
      }

      // error check params - we should have user_id, artist_id, and operation
      if (user_id == null || artist_id == null || operation == null) {
        // return error!
        return MoshiUtil.serialize(ret, "ERROR: /track endpoint requires params 'user_id', 'artist_id', and 'operation', but did not receive them both");
      }


      // figure out what our operation is and execute it / error checking operation
      boolean add = operation.equals("add");
      if (!add && !operation.equals("delete")) {
        return MoshiUtil.serialize(ret, "ERROR: /track endpoint requires param 'operation' to have value 'add' or 'delete', but received '" + operation + "'");
      }

      // find out what artists we're already tracking
      ArrayList<String> artist_ids = this.db.queryTracking(user_id, false);

      // check if we already track this artist!
      boolean alreadyTracking = artist_ids.contains(artist_id);

      if (alreadyTracking && add) {
        // add && alreadyTracking
        // shouldn't add an artist we're already tracking - just return our tracked artists
        ret.put("tracked artists", artist_ids);
        // return success!
        return MoshiUtil.serialize(ret, "success");
      } else if (add) {
        // add && !alreadyTracking
        // add to db
        this.db.addTracking(user_id, artist_id);
        // return success!
        ret.put("tracked artists", this.db.queryTracking(user_id, false));
        return MoshiUtil.serialize(ret, "success");
      } else if (alreadyTracking) {
        // !add && alreadyTracking
        // should delete!
        this.db.removeTracking(user_id, artist_id);
        // return success!
        ret.put("tracked artists", this.db.queryTracking(user_id, false));
        return MoshiUtil.serialize(ret, "success");
      } else {
        // !add && !alreadyTracking
        // shouldn't delete an artist we're not tracking - just return our tracked artists
        ret.put("tracked artists", artist_ids);
        return MoshiUtil.serialize(ret, "success");
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
  }
}
