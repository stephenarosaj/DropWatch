package edu.brown.cs.student.api.formats;

import com.squareup.moshi.Json;
import java.util.List;
public record SearchRecord(
    @Json(name = "artists") Artists artists
  ) {
    public record Artists(
        @Json(name = "total") Integer total,
        @Json(name = "items") List<Artist> items,

        @Json(name = "limit") Integer limit,
        @Json(name = "next") String next,
        @Json(name = "offset") Integer offset,
        @Json(name = "previous") String previous
    ) {
      public record Artist(
          @Json(name = "name") String name,
          @Json(name = "id") String id,
          @Json(name = "popularity") String popularity
          // can also add images later
      ) {}
    }

}
