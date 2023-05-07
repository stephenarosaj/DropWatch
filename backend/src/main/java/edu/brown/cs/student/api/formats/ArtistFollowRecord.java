package edu.brown.cs.student.api.formats;

import com.squareup.moshi.Json;

import java.util.List;

public record ArtistFollowRecord(
      @Json(name = "artists") ArtistsJSON artistsJSON
) {
    public record ArtistsJSON(
            @Json(name = "items") List<ArtistRecord> artists
    ) {}
}
