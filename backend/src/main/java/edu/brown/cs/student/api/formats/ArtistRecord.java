package edu.brown.cs.student.api.formats;

import com.squareup.moshi.Json;

public record ArtistRecord() {
    public record Artist(
            @Json(name = "name") String name,
            @Json(name = "id") String id,
            @Json(name = "popularity") String popularity,
            @Json(name = "genres") String[] genres
            // image can be added here later
    ) {}
}
