package edu.brown.cs.student.api.formats;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Record to hold information on an Album
 */
public record AlbumRecord() {
  /**
   * Note that when one of these objects is returned via search, all fields are present (EXCEPT FOR "tracks",
   * since the object returned there is "SimplifiedAlbumObject"!)
   * REFERENCE: https://developer.spotify.com/documentation/web-api/reference/get-an-album
   * @param name "The name of the album. In case of an album takedown, the value may be an empty string."
   * @param id "The Spotify ID for the album."
   * @param popularity "The popularity of the album. The value will be
   *                   between 0 and 100, with 100 being the most popular."
   * @param href "A link to the Web API endpoint providing full details of the album."
   * @param type "The object type." - Will always be "artist"
   * @param genres "A list of the genres the album is associated with. If not yet classified, the array is empty."
   * @param images "The cover art for the album in various sizes, widest first."
   * @param total_tracks "The number of tracks in the album."
   * @param release_date "The date the album was first released."
   * @param release_date_precision "The precision with which release_date value is known."
   *                               Allowed values: "year", "month", or "day"
   * @param restrictions "Included in the response when a content restriction is applied."
   *                     Allowed values:"market - The content item is not available in the given market.
   *                                    "product" - The content item is not available for the user's subscription type.
   *                                    "explicit" - The content item is explicit and the user's account is set to not play explicit content.
   * @param album_type "The type of the album."
   *                   Allowed values: "album", "single", "compilation"
   * @param artists "The artists of the album." - Array of ArtistObject
   * @param tracks "The tracks of the album."
   */
  public record Album(
    @Json(name = "name") String name,
    @Json(name = "id") String id,
    @Json(name = "type") String type,
    @Json(name = "popularity") String popularity,
    @Json(name = "href") String href,
    @Json(name = "genres") String[] genres,
    @Json(name = "images") Object[] images,
    // array of ImageObjects: string url, int pixel height, int pixel width
    @Json(name = "total_tracks") Integer total_tracks,
    @Json(name = "release_date") String release_date,
    @Json(name = "release_date_precision") String release_date_precision,
    @Json(name = "restrictions") String restrictions,
    @Json(name = "album_type") String album_type,
    @Json(name = "artists") List<Album> artists,
    @Json(name = "tracks") List<TrackRecord.Track> tracks // NOT returned by search!!!!

  ) {}
}
