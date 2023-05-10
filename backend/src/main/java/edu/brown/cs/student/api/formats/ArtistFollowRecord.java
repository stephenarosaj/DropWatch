package edu.brown.cs.student.api.formats;

import com.squareup.moshi.Json;
import java.util.List;

/**
 * The record that stores info about which artists a user follows from Spotify.
 *
 * @param artistsJSON The inner artistsJSON that stores user info about which artists they follow.
 */
public record ArtistFollowRecord(@Json(name = "artists") ArtistsJSON artistsJSON) {
  public record ArtistsJSON(@Json(name = "items") List<ArtistRecord> artists) {}
}
