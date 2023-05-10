package edu.brown.cs.student.api.formats;

import com.squareup.moshi.Json;

/**
 * A record to hold search results REFERENCE:
 * https://developer.spotify.com/documentation/web-api/reference/search
 *
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
    @Json(name = "albums") Albums albums) {}
