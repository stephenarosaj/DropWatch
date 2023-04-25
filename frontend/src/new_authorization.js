import { clientID, clientSecretID } from "./private/tokens";
const AUTHORIZE = 'https://accounts.spotify.com/authorize'
const REDIRECT_URI = 'http://localhost:3000/callback'
export function requestOAuth() {
  let url = AUTHORIZE;
  url += "?client_id=" + clientID;
  url += "&response_type=code";
  url += "&redirect_uri=" + REDIRECT_URI;
  url += "&show_dialog=true";
  url += "&scope=user-read-private user-read-email";
  window.location.href = url
}

export function getAccessToken(code) {
  let body = 'grant_type=authorization_code';
  body += '&redirect_uri=' + REDIRECT_URI
  body += '&client_id=' + clientID
  body += '&client_secret=' + clientSecretID
  callAuthorizationApi(body)
}

function callAuthorizationApi(body) {
  const response = fetch("https://accounts.spotify.com/api/token", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
      'Authorization': 'Basic' + btoa(clientID + ":" + clientSecretID)
    },
    body: body,
  })
    .then((response) => {
      if (!response.ok) {
        console.log("RESPONSE:" + JSON.stringify(response))
        throw new Error("HTTP status " + response.status);
      }
      return response.json();
    })
    .then((data) => {
      localStorage.setItem("access_token", data.access_token);
      console.log(data)
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}
