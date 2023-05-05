package edu.brown.cs.student.api.formats;

import com.squareup.moshi.Json;
import java.util.List;

/**
 * Record to hold information on a Track
 * Note that when one of these objects is returned via search, all fields are present (EXCEPT FOR "tracks",
 * since the object returned there is "SimplifiedAlbumObject"!)
 * REFERENCE: https://developer.spotify.com/documentation/web-api/reference/get-track
 * @param name "The name of the track."
 * @param id "The Spotify ID for the track."
 * @param popularity "The popularity of the track. The value will be
 *                   between 0 and 100, with 100 being the most popular."
 * @param href "A link to the Web API endpoint providing full details of the track."
 * @param type "The object type." - Will always be "track"
 * @param restrictions "Included in the response when a content restriction is applied."
 *                     Allowed values:"market - The content item is not available in the given market.
 *                                    "product" - The content item is not available for the user's subscription type.
 *                                    "explicit" - The content item is explicit and the user's account is set to not play explicit content.
 * @param artists "The artists of the album." - Array of ArtistObject
 * @param album "The album on which the track appears." - AlbumObject (AlbumRecord.Album)
 */
public record TrackRecord(
  @Json(name = "name") String name,
  @Json(name = "id") String id,
  @Json(name = "popularity") Integer popularity,
  @Json(name = "href") String href,
  @Json(name = "type") String type,
  @Json(name = "restrictions") String restrictions,
  @Json(name = "artists") List<ArtistRecord> artists,
  @Json(name = "album") AlbumRecord album

) {}
