package edu.brown.cs.student.api.formats;

import com.squareup.moshi.Json;

/**
 * Record to hold information on an Artist
 * Note that when one of these objects is returned via search, all fields are present
 * REFERENCE: https://developer.spotify.com/documentation/web-api/reference/get-an-artist
 * @param name "The name of the artist."
 * @param id "The Spotify ID for the artist."
 * @param popularity "The popularity of the artist. The value will be
 *                   between 0 and 100, with 100 being the most popular.
 *                   The artist's popularity is calculated from the
 *                   popularity of all the artist's tracks."
 * @param href "A link to the Web API endpoint providing full details of the artist."
 * @param type "The object type." - Will always be "artist"
 * @param genres "A list of the genres the artist is associated with. If not yet classified, the array is empty."
 * @param images "Images of the artist in various sizes, widest first."
 */
public record ArtistRecord(
  @Json(name = "name") String name,
  @Json(name = "id") String id,
  @Json(name = "type") String type,
  @Json(name = "popularity") Integer popularity,
  @Json(name = "href") String href,
  @Json(name = "genres") String[] genres,
  @Json(name = "images") ImageRecord[] images
  // array of ImageObjects: string url, int pixel height, int pixel width
) {}