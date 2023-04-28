import { clientID } from "./private/tokens";
import { useState } from "react";

export function generateRandomString(length) {
  let text = "";
  let possible =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  for (let i = 0; i < length; i++) {
    text += possible.charAt(Math.floor(Math.random() * possible.length));
  }
  return text;
}

//export const digest = await window.crypto.subtle.digest("SHA-256", data);

/** I thought the problem was the  encoding of the codeVerifier but i'm still getting the same errors */
export async function generateCodeChallenge(codeVerifier) {
  function base64encode(string) {
    return btoa(String.fromCharCode.apply(null, new Uint8Array(string)))
      .replace(/\+/g, "-")
      .replace(/\//g, "_")
      .replace(/=+$/, "");
  }

  // const encoder = new TextEncoder();
  // const data = encoder.encode(codeVerifier);
  const digest = await window.crypto.subtle.digest("SHA-256", codeVerifier);

  return base64encode(digest);
}

const encoder = new TextEncoder();
// const data = encoder.encode(codeVerifier);
const codeVerifier = encoder.encode(generateRandomString(128));


// const urlParams = new URLSearchParams(window.location.search);
// let code = urlParams.get("code");

export function fetchAccessToken(code, redirect_uri, client_id) {
  console.log("CODE: " + code)
  console.log("REDIRECT: " + redirect_uri)
  console.log("CLIENT ID: " +client_id)
  console.log("VERIFIER: " + codeVerifier)
  // const encoder = new TextEncoder();
  // const data = encoder.encode(codeVerifier);

  let body = new URLSearchParams({
    grant_type: "authorization_code",
    code: code,
    redirect_uri: redirect_uri,
    client_id: client_id,
    code_verifier: codeVerifier,
  })
  
  const response = fetch("https://accounts.spotify.com/api/token?", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: JSON.stringify(body),
  })
    .then((response) => {
      if (!response.ok) {
        console.log(response.json())
        throw new Error("HTTP status " + response.status);
      }
      return response.json();
    })
    .then((data) => {
      localStorage.setItem("access_token", data.access_token);
      // console.log(data)
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}



async function getProfile(accessToken) {
  let access_Token = localStorage.getItem("access_token");

  const response = await fetch("https://api.spotify.com/v1/me", {
    headers: {
      Authorization: "Bearer " + access_Token,
    },
  });

  const data = await response.json();
}

export function requestAuthorization(client_id, redirect_uri) {

//make the request to the spotify API for authorization
generateCodeChallenge(codeVerifier).then(codeChallenge => {
  let state = generateRandomString(16);
  let scope = 'user-read-private user-read-email';

  localStorage.setItem('code_verifier', codeVerifier);

  let args = new URLSearchParams({
    response_type: 'code',
    client_id: client_id,
    scope: scope,
    redirect_uri: redirect_uri,
    state: state,
    code_challenge_method: 'S256',
    code_challenge: codeChallenge
  });

  window.location = 'https://accounts.spotify.com/authorize?' + args;
});
}