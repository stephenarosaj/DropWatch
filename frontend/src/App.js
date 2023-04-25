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

import React, { useEffect, useState } from 'react';
import { Button } from 'antd'; // Importing a button from Ant Design, you can use any UI library or HTML

const CLIENT_ID = 'your_client_id'; // Replace with your client ID
const REDIRECT_URI = 'http://localhost:3000/callback'; // Replace with your redirect URI
const SPOTIFY_AUTH_URL = `https://accounts.spotify.com/authorize?response_type=code&client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&scope=user-read-private%20user-read-email&code_challenge_method=S256&code_challenge=`; // Spotify authorization URL with PKCE parameters
const BASE64URL_ENCODED_SHA256 = 'your_code_challenge'; // Replace with your code challenge

const App = () => {
  const [token, setToken] = useState('');

  useEffect(() => {
    const getToken = async () => {
      // Check if authorization code is present in URL params
      const urlParams = new URLSearchParams(window.location.search);
      const code = urlParams.get('code');
      if (code) {
        try {
          // Request access token from Spotify API
          const response = await fetch('https://accounts.spotify.com/api/token', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams({
              grant_type: 'authorization_code',
              code,
              redirect_uri: REDIRECT_URI,
              client_id: CLIENT_ID,
              code_verifier: BASE64URL_ENCODED_SHA256,
            }),
          });
          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }
          const data = await response.json();
          setToken(data.access_token);
        } catch (error) {
          console.error(error);
        }
      }
    };
    getToken();
  }, []);

  const handleAuthClick = () => {
    // Generate code verifier and code challenge
    const codeVerifier = base64URLEncode(crypto.getRandomValues(new Uint8Array(32)));
    const codeChallenge = base64URLEncode(crypto.subtle.digest('SHA-256', new TextEncoder().encode(codeVerifier)));

    // Store code verifier in local storage for later use
    localStorage.setItem('codeVerifier', codeVerifier);

    // Redirect user to Spotify authorization URL
    window.location.href = `${SPOTIFY_AUTH_URL}${codeChallenge}`;
  };

  return (
    <div>
      {token ? (
        <p>Access token: {token}</p>
      ) : (
        <Button onClick={handleAuthClick}>Authorize with Spotify</Button>
      )}
    </div>
  );
};

// Helper function to base64 URL encode a string
const base64URLEncode = (str) => {
  return btoa(String.fromCharCode.apply(null, str))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '');
};

export default App;