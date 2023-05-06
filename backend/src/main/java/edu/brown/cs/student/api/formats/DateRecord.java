package edu.brown.cs.student.api.formats;

import com.squareup.moshi.Json;

/***
 * Record to hold date information (for Tracks and Albums).
 * REFERENCE: https://developer.spotify.com/documentation/web-api/reference/get-track
 * @param release_date "The date the album was first released."
 * @param release_date_precision "The precision with which release_date value is known."
 */
public record DateRecord(
        @Json(name = "release_date") String release_date,
        @Json(name = "release_date_precision") String release_date_precision) {

    // note: precision is the format of the string release_date, it can be "day/month/year", "month/year", "year"
    // so, with real dates that looks like: "4/20/2023", "4/20", "23"

    // TODO: should there be a consideration between the dates of Tracks and Albums? Tracks are apart of albums,
    //  but can be released separately and thus have different release dates right? ...I could be overthinking


}
