// import logo from './logo.svg';
// import './App.css';
// import React from "react"
// import {clientID, clientSecretID } from './private/tokens';
// import { requestAuthorization, fetchAccessToken } from './authorization';
// import { getAccessToken, requestOAuth } from './new_authorization';

// const redirect_uri = "http://localhost:3000/callback"

// function handleRedirect(query) {
//   let code = getCode(query)
//   fetchAccessToken(code, redirect_uri, clientID)
//   // getAccessToken(code)
//   // window.history.pushState("", "", redirect_uri)
// }

// //AQBqff_vdV3ErtEF8dodQ6MRYtw66ItIWCQqFs1tSZV0M3KNl4TfWZzOdPWG4yAQ7pd0uXnhBh6g3o7M0nJLDu1BwmJvN0yQYjsInGgOH3HWYw322iG8mHoczLltUdZ-RvcvWiqvVDW5hn_2QBPrwMUIVFWVwn1vvcmIPKYL_V-yRD1p7Ot1YhPsFa17z7R_lDoWbMZ-GH2dPMHw0Vb9zmCgN1wEYw6_YEGKa_gtm7EsqFj2zhyu6_KAW_4Izx8HVVTlX1pHddtrQuTc_JJsLL7511xBSTtmVQ&state=JThm6QyPkWEAKIVz

// function getCode(query) {
//   const urlParams = new URLSearchParams(query);
//   let code = urlParams.get('code')
//   return code
// }
// function App() {
//   const [token, setToken] = React.useState("")

// function onLoad() {
//   let query = window.location.search
//   if (query.length > 0) {
//     handleRedirect(query);
//   }
// }

//   React.useEffect(() => {
//     onLoad()
//   });

//   return (
//     <div className="App">
//       {/* <button className= "authorize" onClick={() => requestAuthorization(clientID,redirect_uri)}>Request Authorization</button> */}
//       <button className= "authorize" onClick={() => requestAuthorization(clientID, redirect_uri)}>Request Authorization</button>
//     </div>
    
//   );
// }

// export default App;
import React, { useState, useEffect } from "react";
import queryString from "query-string";
import { clientID } from "./private/tokens";

const client_id = clientID;
const redirect_uri = "http://localhost:3000/callback";

function App() {
  const [accessToken, setAccessToken] = useState("");

  useEffect(() => {
    const parsed = queryString.parse(window.location.search);
    const code = parsed.code;

    if (code) {
      requestAccessToken(code);
    }
  }, []);

  const requestAuthorization = () => {
    const state = generateRandomString(16);
    const codeVerifier = generateRandomString(64);
    const codeChallenge = base64URLEncode(sha256(codeVerifier));
  
    const authorizationUrl =
      "https://accounts.spotify.com/authorize?" +
      new URLSearchParams({
        response_type: "code",
        client_id: "YOUR_CLIENT_ID",
        scope: "YOUR_SCOPES",
        redirect_uri: "YOUR_REDIRECT_URI",
        state: state,
        code_challenge_method: "S256",
        code_challenge: codeChallenge,
      });
  
    window.location.href = authorizationUrl;
  };

  const fetchAccessToken = (code, redirectUri, clientId, codeVerifier) => {
    const requestBody = new URLSearchParams({
      grant_type: "authorization_code",
      code: code,
      redirect_uri: redirectUri,
      client_id: clientId,
      code_verifier: codeVerifier,
    });
  
    fetch("https://accounts.spotify.com/api/token", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: requestBody,
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
      })
      .then((responseJson) => {
        console.log(responseJson);
        localStorage.setItem("access_token", responseJson.access_token);
        localStorage.setItem("refresh_token", responseJson.refresh_token);
        localStorage.setItem("token_expiration", Date.now() + responseJson.expires_in * 1000);
      })
      .catch((error) => {
        console.error(error);
      });
  };

  const generateRandomString = (length) => {
    let text = "";
    const possible =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for (let i = 0; i < length; i++) {
      text += possible.charAt(Math.floor(Math.random() * possible.length));
    }

    return text;
  };

  const base64URLEncode = (str) => {
    return btoa(str)
      .replace(/\+/g, "-")
      .replace(/\//g, "_")
      .replace(/=+$/, "");
  };

  const sha256 = (buffer) => {
    return crypto.subtle
      .digest("SHA-256", new TextEncoder().encode(buffer))
      .then((hash) => {
        return base64URLEncode(String.fromCharCode(...new Uint8Array(hash)));
      });
  };

  return (
    <div>
      <h1>Spotify Authorization Example</h1>
      {!accessToken ? (
        <button onClick={requestAuthorization}>Authorize with Spotify</button>
      ) : (
        <p>Access token: {accessToken}</p>
      )}
    </div>
  );
}

export default App;


