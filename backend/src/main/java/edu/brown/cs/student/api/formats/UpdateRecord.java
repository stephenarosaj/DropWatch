// TODO: I think this class can be deleted - we have this covered by the other records

//package edu.brown.cs.student.api.formats;
//
//import com.squareup.moshi.Json;
//
//public record UpdateRecord(
//        @Json(name = "items") Object[] items) {
//
//    public record Album(
//            @Json(name = "album_type") String albumType,
//            @Json(name = "name") String name,
//            @Json(name = "id") String id,
//            @Json(name = "release_date") String releaseDate,
//            @Json(name = "release_date_precision") String releaseDatePrecision,
//            @Json(name = "genres") String[] genres,
//            @Json(name = "images") Object[] images
//            // array of ImageObjects: string url, int pixel height, int pixel width
//            ) {}
//}
