package edu.brown.cs.student.api.endpointHandlers;

/**
 * Class for TrackHandler - handles tracking/untracking artists!
 *      - a user tracks a new artist
 *          - the user's tracking table is updated with the artists' spotify id
 *          - the artist's trackers table is updated with the user's spotify id
 *              - if the artist has not been tracked by anyone else before, a new
 *                table is made for that artist's trackers and the user is added to
 *                it. The artist and their latest release date is also added to the
 *                latest_releases table
 *              - if the artist has been tracked before, the user is added to that
 *                artists' trackers table
 *      - a user untracks an artist
 *          - if the user is tracking that artist (they are found in that artist's
 *            trackers table), they are removed from the table
 */
public class TrackHandler {
}
