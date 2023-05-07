import { clientID, clientSecretID } from "../private/tokens";
import { getPlaylists } from "./playlists";
import { getFollowed, getUserInfo } from "./followed";
const redirect_uri = "http://localhost:3000/callback";
const client_id = clientID;
const client_secret = clientSecretID;
const AUTHORIZE = "https://accounts.spotify.com/authorize";
const TOKEN = "https://accounts.spotify.com/api/token";

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
 * Function to build the body of parameters to supply to get the access token
 * @param {*} code - the authorization code provdied to us from requestAuthorization
 */
// export function fetchAccessToken(code, setLogin, setRefreshToken, setUsername) {
//   let body = "grant_type=authorization_code";
//   body += "&code=" + code;
//   body += "&redirect_uri=" + redirect_uri;
//   body += "&client_id-=" + client_id;
//   body += "&client_secret=" + client_secret;
//   callAuthorizationApi(body, setLogin, setRefreshToken, setUsername);
// }

// callAuthorizationApi(body) {
//   console.log("hi");
// }

/**
 * Function to call the spotify api to get the access token
 * @param {*} body - the parameters to be provided to the api request
 */
export async function fetchAccessToken(
  code,
  setLogin,
  setRefreshToken,
  setUsername,
  setPlaylists,
  setFollowed
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
        setRefreshToken(refresh_token);
      }
      setLogin(true);
      // console.log("setPlaylists")
      // getUserInfo('playlists?limit=10', setPlaylists)
      // console.log("setFollowed")
      // getUserInfo('following?type=artist&limit=10', setFollowed)
      getUsername(setUsername);
      getPlaylists(setPlaylists);
      getFollowed(setFollowed);
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

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
