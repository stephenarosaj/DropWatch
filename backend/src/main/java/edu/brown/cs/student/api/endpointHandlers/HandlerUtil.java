package edu.brown.cs.student.api.endpointHandlers;

import edu.brown.cs.student.api.MoshiUtil;
import edu.brown.cs.student.api.database.DropWatchDB;
import edu.brown.cs.student.api.endpointHandlers.ExternalAPI.SpotifyAPIRequester;
import edu.brown.cs.student.api.endpointHandlers.ExternalAPI.SpotifyDataSource;
import edu.brown.cs.student.api.exceptions.APIRequestException;
import edu.brown.cs.student.api.exceptions.DeserializeException;
import edu.brown.cs.student.api.formats.*;
import okio.Buffer;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.lang.System.exit;

public class HandlerUtil {


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
  public HandlerUtil(DropWatchDB db) {
    try {
      this.SpotifyAPIRequester = new SpotifyAPIRequester();
      this.db = db;
    } catch (Exception e) {
      System.out.println("CRITICAL ERROR: COULD NOT SET UP CONNECTION TO DB '" + filepath + "', ABORTING HandlerUtil() creation!\n");
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
  public ArrayList<AlbumRecord> checkNewRelease(String artist_id, DateRecord storedDate) throws APIRequestException, DeserializeException, SQLException, ClassNotFoundException {
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
    // NOTE: only check for first 2 because the other two bring in such bs results...
    for (int i = 0; i < 2; i++) {
      String urlString = "https://api.spotify.com/v1/artists/"
        + artist_id + "/albums?"
        + "&market=US"
        + "&limit=" + 4
        + "&offset=0"
        + "&include_groups=" + groups[i];
      // make the request and grab results
      Buffer buf = this.SpotifyAPIRequester.getData(urlString);
      Albums albums = MoshiUtil.deserializeUpdate(buf);

      // for each album in the results, if the release date is newer, add it to our db and artistNewReleases!
      for (AlbumRecord album: albums.items()) {
        // grab release date
        DateRecord fetchedDate = new DateRecord(album.release_date(), album.release_date_precision());
        // compare fetched and stored dates!
        if (DateRecord.compareDates(fetchedDate, storedDate) > 0) {
          // fetched date is more recent than stored date!
          try {
            // add image to our album!
            album = this.populateAlbumArtistImages(album);
            // add to our list
            artistNewReleases.add(album);
            // add it to our db
            this.db.addNewAlbum(album.artists(), album.id(), album.release_date(), album.release_date_precision(), album.href(), (album.images() == null || album.images().length == 0 ? null : album.images()[0].url()), album.artists().get(0).name(), album.album_group());
          } catch (Exception e) {
            // remove the latest album, continue!
            artistNewReleases.remove(artistNewReleases.size() - 1);
          }
        }
      }
    }
    return artistNewReleases;
  }

  /**
   * Helper function that makes request to grab artist images and fill them into
   * the album object supplied, since spotify's api doesn't give images sometimes
   * @param album the album whose artist images we want to fill in
   * @return the same AlbumRecord, but with the images filled in
   */
  public AlbumRecord populateAlbumArtistImages(AlbumRecord album) throws APIRequestException, DeserializeException {
    // collect fixed artists in a List
    ArrayList<ArtistRecord> fullArtists = new ArrayList<>();
    // for each artist, make request to their link and grab image!
    for (int i = 0; i < album.artists().size(); i++) {
      // make the request and grab results
      Buffer buf = this.SpotifyAPIRequester.getData(album.artists().get(i).href());
      ArtistRecord fullArtist = MoshiUtil.deserializeArtistRecord(buf);
      fullArtists.add(fullArtist);
    }
    return new AlbumRecord(album.name(), album.id(), album.type(), album.popularity(), album.href(), album.genres(), album.images(), album.total_tracks(), album.release_date(), album.release_date_precision(), album.restrictions(), album.album_type(), album.album_group(), fullArtists, album.tracks());
  }
}
