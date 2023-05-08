package edu.brown.cs.student.api.endpointHandlers;

import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.database.DropWatchDB;
import edu.brown.cs.student.api.endpointHandlers.ExternalAPI.SpotifyDataSource;
import edu.brown.cs.student.api.formats.AlbumRecord;
import edu.brown.cs.student.api.formats.DateRecord;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.File;
import java.sql.SQLException;
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
  public TrackHandler() {
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
        return MoshiUtil.serialize(ret, "ERROR: /track endpoint requires params 'user_id', 'artist_id', and 'operation', but did not receive all of them");
      }

      // figure out what our operation is and execute it / error checking operation
      boolean add = operation.equals("add");
      boolean query = operation.equals("query");
      if (!add && !operation.equals("delete") && !query) {
        return MoshiUtil.serialize(ret, "ERROR: /track endpoint requires param 'operation' to have value 'add', 'delete', or 'query', but received '" + operation + "'");
      }

      // find out what artists we're already tracking
      ArrayList<String> currentArtist_ids = this.db.queryTracking(user_id, true);
      // map of (artist_id, [link, first image, name of artist]) pairs
      HashMap<String, ArrayList<String>> currentArtistInfo = this.db.queryMultipleArtists(currentArtist_ids);

      // if just querying, return these artists' info
      if (query) {
        ret.put("data", currentArtistInfo);
        return MoshiUtil.serialize(ret, "success");
      }

      // check if we already track this artist!
      boolean alreadyTracking = currentArtist_ids.contains(artist_id);

      if (alreadyTracking && add) {
        // add && alreadyTracking
        // shouldn't add an artist we're already tracking - just return our tracked artists
        ret.put("data", currentArtistInfo);
        // return success!
        return MoshiUtil.serialize(ret, "success");
      } else if (add) {
        // add && !alreadyTracking
        // add to db
        this.db.addTracking(user_id, artist_id);
        // now check new releases for this artist
        ArrayList<AlbumRecord> artistDrops = new ArrayList<>();
        if (this.db.queryArtists(artist_id).size() == 0) {
          // we don't have this artist in our db yet - need to look at all music as new
          artistDrops = util.checkNewRelease(artist_id, new DateRecord("0000-00-00", "day"));
        } else {
          // this artist already in our db - new music is only from after stored latest release
          artistDrops = util.checkNewRelease(artist_id, this.db.findLatestRelease(artist_id));
        }
        // if not empty, add their releases to our db!
        for (AlbumRecord drop: artistDrops) {
          this.db.addNewAlbum(drop.artists(), drop.id(), drop.release_date(), drop.release_date_precision(), drop.href(), (drop.images() == null || drop.images().length == 0 ? null : drop.images()[0].url()), drop.name(), drop.type());
        }
        // commit changes and return
        this.db.commit();
        // return success!
        ret.put("data", this.db.queryMultipleArtists(this.db.queryTracking(user_id, true)));
        return MoshiUtil.serialize(ret, "success");
      } else if (alreadyTracking) {
        // !add && alreadyTracking
        // should delete!
        this.db.removeTracking(user_id, artist_id);
        this.db.commit();
        // return success!

        ret.put("data", this.db.queryMultipleArtists(this.db.queryTracking(user_id, true)));
        return MoshiUtil.serialize(ret, "success");
      } else {
        // !add && !alreadyTracking
        // shouldn't delete an artist we're not tracking - just return our tracked artists
        ret.put("data", currentArtistInfo);
        return MoshiUtil.serialize(ret, "success");
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return MoshiUtil.serialize(ret, "ERROR: " + e.getMessage());
    }
  }
}
