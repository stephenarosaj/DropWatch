import { clientID, clientSecretID } from "../private/tokens";
import { getPlaylists } from "./playlists";
import { getFollowed} from "./followed";
import track from "./track";
const redirect_uri = "http://localhost:3000/callback";
const client_id = clientID;
const client_secret = clientSecretID;
const AUTHORIZE = "https://accounts.spotify.com/authorize";
const TOKEN = "https://accounts.spotify.com/api/token";


/**
 * Function to request authorization for accessing user data from the Spotify API
 */
export function requestAuthorization() {
  console.log("clicked");
  localStorage.setItem("client_id", client_id);

  // here is where we change the permissions of what we want to know about the user
  let scope =
    "user-read-private user-follow-read playlist-read-collaborative playlist-read-private";

  let url = AUTHORIZE;
  url += "?client_id=" + client_id;
  url += "&response_type=code";
  url += "&redirect_uri=" + redirect_uri;
  url += "&show_dialog=true";
  url += "&scope=" + scope;

  window.location.href = url; //redirect to spotify authorization screen
}
/**
 * Function to retrieve the access token for the user from the spotify API and set the necessary
 * state variables upon login
 * @param {String} code - the access code recieved from requesting authorization in requestAuthorization()
 * @param {function} setLogin - the function to update the isLoggedIn state variable
 * @param {function} setRefreshToken - the function to update the refreshToken state variable
 * @param {function} setUsername - the function to update the username state variable
 * @param {function} setPlaylists - the function to update the playlists state variable
 * @param {function} setFollowed - the function to update the followed state variable
 */
export async function fetchAccessToken(
  code,
  setLogin,
  setUsername,
  setPlaylists,
  setFollowed,
  setArtists
) {
  // builds the body of the Spotify api request
  let body = "grant_type=authorization_code";
  body += "&code=" + code;
  body += "&redirect_uri=" + redirect_uri;
  body += "&client_id-=" + client_id;
  body += "&client_secret=" + client_secret;

  const requestOpts = {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
      Authorization: "Basic " + btoa(client_id + ":" + client_secret),
    },
    body: body,
  };

  fetch(TOKEN, requestOpts)
    .then((response) => {
      if (!response.ok) {
        throw new Error("HTTP status" + response.status);
      }
      return response.json();
    })
    .then((data) => {
      let refresh_token = data.refresh_token;
      let access_token = data.access_token;
      if (access_token !== undefined) {
        localStorage.setItem("access_token", access_token);
      }
      if (refresh_token !== undefined) {
        localStorage.setItem("refresh_token", refresh_token);
      }
      setLogin(true);
      getUsername(setUsername);
      getPlaylists(setPlaylists);
      getFollowed(setFollowed);
      track("random", "query")
        .then(response => {
          console.log("LOGIN: ")
          console.log(response)
          setArtists([...response])
          
        }
        )
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

/**
 * Function to retrieve the logged in user's Spotify username from the API
 * @param {function} setUsername - function to update the username state variable
 */
async function getUsername(setUsername) {
  let access_token = localStorage.getItem("access_token");
  let headers = { headers: { Authorization: "Bearer " + access_token } };

  fetch("https://api.spotify.com/v1/me", headers)
    .then((response) => {
      console.log("status: " + response.status);
      if (!response.ok) {
        throw new Error("HTTP status" + response.status);
      }
      return response.json();
    })
    .then((data) => {
      if (data.display_name !== undefined) {
        setUsername(data.display_name);
      } else {
        console.log(data.json());
      }

      if (data.id !== undefined) {
        localStorage.setItem("user_id", data.id);
        console.log("id: " + data.id);
      } else {
        console.log("could not find user_id!");
      }
    });
}
