import { clientID, clientSecretID} from "./private/tokens"
const redirect_uri = "http://localhost:3000/callback"
const client_id = clientID
const client_secret = clientSecretID
const AUTHORIZE = "https://accounts.spotify.com/authorize"
const TOKEN = "https://accounts.spotify.com/api/token"

export function requestAuthorization(){
  console.log("clicked")
  localStorage.setItem("client_id", client_id)
  // here is where we change the permissions of what we want to know about the user
  let scope = "user-read-private user-read-email"

  let url = AUTHORIZE;
  url += "?client_id="+client_id;
  url += "&response_type=code";
  url += "&redirect_uri="+redirect_uri;
  url += "&show_dialog=true";
  url += "&scope="+scope;

  window.location.href = url //redirect to spotify authorization screen
}

/**
 * Function to build the body of parameters to supply to get the access token
 * @param {*} code - the authorization code provdied to us from requestAuthorization
 */
export function fetchAccessToken(code) {
  let body = "grant_type=authorization_code";
  body += "&code=" + code;
  body += "&redirect_uri=" + redirect_uri;
  body += "&client_id-=" + client_id;
  body += "&client_secret=" + client_secret;
  callAuthorizationApi(body);
}

// callAuthorizationApi(body) {
//   console.log("hi");
// }

/**
 * Function to call the spotify api to get the access token
 * @param {*} body - the parameters to be provided to the api request
 */
function callAuthorizationApi(body) {
  const requestOpts = {
    method: 'POST',
    headers: {'Content-Type' : 'application/x-www-form-urlencoded', 
              'Authorization': 'Basic ' + btoa(client_id + ":" + client_secret)},
    body: body
  };
  fetch(TOKEN, requestOpts)
    .then(response => {
      if(!response.ok) {
        throw new Error('HTTP status' + response.status);
      }
      return response.json();
    })
    .then(data => {
      localStorage.setItem('access_token', data.access_token);
      localStorage.setItem('refresh_token', data.refresh_token);
    })
    .catch(error => {
      console.error('Error:', error)
    });
}

function handleAuthorizationResponse() {
  
}