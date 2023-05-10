package edu.brown.cs.student.api.formats;

import com.squareup.moshi.Json;
import java.util.List;

/**
 * The record to hold information on the Tracks returned
 *
 * @param total "The total number of items available to return."
 * @param items "array of [Track]" - the tracks returned by this search
 * @param offset "The offset of the items returned (as set in the query or by default)"
 * @param limit "The maximum number of items in the response (as set in the query or by default
 *     (20))."
 * @param next "URL to the next page of items. (null if none)"
 * @param previous "URL to the previous page of items. (null if none)"
 */
public record Tracks(
    @Json(name = "total") Integer total,
    @Json(name = "items") List<TrackRecord> items,
    @Json(name = "offset") Integer offset,
    @Json(name = "limit") Integer limit,
    @Json(name = "next") String next,
    @Json(name = "previous") String previous) {}
