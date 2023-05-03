package edu.brown.cs.student.api.formats;

import com.squareup.moshi.Json;
import java.util.List;

/**
 * A record to hold search results
 * REFERENCE: https://developer.spotify.com/documentation/web-api/reference/search
 * @param artists the artists returned
 * @param tracks the tracks returned
 * @param albums the albums returned
 */
public record SearchRecord(
  // the tracks returned
  @Json(name = "tracks") Tracks tracks,
  // the artists returned
  @Json(name = "artists") Artists artists,
  // the albums returned
  @Json(name = "albums") Albums albums
) {
  /**
   * The record to hold information on the Tracks returned
   * @param total "The total number of items available to return."
   * @param items "array of [Track]" - the tracks returned by this search
   * @param offset "The offset of the items returned (as set in the query or by default)"
   * @param limit "The maximum number of items in the response (as set in the query or by default (20))."
   * @param next "URL to the next page of items. (null if none)"
   * @param previous "URL to the previous page of items. (null if none)"
   */
  public record Tracks(
    @Json(name = "total") Integer total,
    @Json(name = "items") List<TrackRecord.Track> items,

    @Json(name = "offset") Integer offset,
    @Json(name = "limit") Integer limit,
    @Json(name = "next") String next,
    @Json(name = "previous") String previous
  ) {}

  /**
   * The record to hold information on the Artists returned
   * @param total "The total number of items available to return."
   * @param items "array of [Artist]" - the artists returned by this search
   * @param offset "The offset of the items returned (as set in the query or by default)"
   * @param limit "The maximum number of items in the response (as set in the query or by default (20))."
   * @param next "URL to the next page of items. (null if none)"
   * @param previous "URL to the previous page of items. (null if none)"
   */
  public record Artists(
    @Json(name = "total") Integer total,
    @Json(name = "items") List<ArtistRecord.Artist> items,

    @Json(name = "offset") Integer offset,
    @Json(name = "limit") Integer limit,
    @Json(name = "next") String next,
    @Json(name = "previous") String previous
  ) {}

  /**
   * The record to hold information on the Tracks returned
   * @param total "The total number of items available to return."
   * @param items "array of [Track]" - the tracks returned by this search
   * @param offset "The offset of the items returned (as set in the query or by default)"
   * @param limit "The maximum number of items in the response (as set in the query or by default (20))."
   * @param next "URL to the next page of items. (null if none)"
   * @param previous "URL to the previous page of items. (null if none)"
   */
  public record Albums (
    @Json(name = "total") Integer total,
    @Json(name = "items") List<ArtistRecord.Artist> items,

    @Json(name = "offset") Integer offset,
    @Json(name = "limit") Integer limit,
    @Json(name = "next") String next,
    @Json(name = "previous") String previous
  ) {}
}
