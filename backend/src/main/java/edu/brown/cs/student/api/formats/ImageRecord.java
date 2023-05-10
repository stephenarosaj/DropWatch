package edu.brown.cs.student.api.formats;

import com.squareup.moshi.Json;

/**
 * Record to hold image information REFERENCE:
 * https://developer.spotify.com/documentation/web-api/reference/get-an-album
 *
 * @param url "The source URL of the image."
 * @param height "The image height in pixels."
 * @param width "The image width in pixels."
 */
public record ImageRecord(
    @Json(name = "url") String url,
    @Json(name = "height") Integer height,
    @Json(name = "width") Integer width) {}
