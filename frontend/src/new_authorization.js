// import { clientID, clientSecretID } from "./private/tokens";
// const redirectUri = 'http://localhost:3000/callback';
// let codeVerifier = generateRandomString(128);

// function generateRandomString(length) {
//   let text = '';
//   let possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

//   for (let i = 0; i < length; i++) {
//     text += possible.charAt(Math.floor(Math.random() * possible.length));
//   }
//   return text;
// }

// // function sha256(plain) { 
// //   // returns promise ArrayBuffer
// //   const encoder = new TextEncoder();
// //   const data = encoder.encode(plain);
// //   return window.crypto.subtle.digest('SHA-256', data);
// // }

// // function base64urlencode(a) {
// //   // Convert the ArrayBuffer to string using Uint8 array.
// //   // btoa takes chars from 0-255 and base64 encodes.
// //   // Then convert the base64 encoded to base64url encoded.
// //   // (replace + with -, replace / with _, trim trailing =)
// //   return btoa(String.fromCharCode.apply(null, new Uint8Array(a)))
// //       .replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
// // }

// // async function pkce_challenge_from_verifier(v) {
// //   let hashed = await sha256(v);
// //   let base64encoded = base64urlencode(hashed);
// //   return base64encoded;
// // }


// // async function generateCodeChallenge(codeVerifier) {
// //   function base64encode(string) {
// //     return btoa(String.fromCharCode.apply(null, new Uint8Array(string)))
// //       .replace(/\+/g, '-')
// //       .replace(/\//g, '_')
// //       .replace(/=+$/, '');
// //   }

// //   const encoder = new TextEncoder();
// //   const data = encoder.encode(codeVerifier);
// //   const digest = await window.crypto.subtle.digest('SHA-256', data);

// //   return base64encode(digest);
// // }

// async function sha256(plain) {
//   const encoder = new TextEncoder()
//   const data = encoder.encode(plain)

//   return window.crypto.subtle.digest('SHA-256', data)
// }

// function base64urlencode(a) {
//   let arr = String.fromCharCode.apply(null, new Uint8Array(a))
//   let to_return = btoa(arr).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
//   return to_return
//   // return btoa()
//   //   String.fromCharCode.apply(null, new Uint8Array(a))
//   //   .replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '')
// }

// async function pkce_challenge_from_verifier(v) {
//   codeVerifier = await sha256(v)
//   return base64urlencode(codeVerifier)
// }




// export async function requestAuthorization() {
//   pkce_challenge_from_verifier(codeVerifier).then(codeChallenge => {
//     let state = generateRandomString(16);
//     let scope = 'user-read-private user-read-email';
  
//     localStorage.setItem('code_verifier', codeVerifier);

//     // build parameters for the query
//     let args = "response_type=code"
//     args += "&client_id=" + clientID
//     args += "&scope=" + scope
//     args += "&redirect_uri=" + redirectUri
//     args += "&state=" + state
//     args += "&code_challenge_method=S256"
//     args += "&code_challenge=" + codeChallenge
  
//     window.location = 'https://accounts.spotify.com/authorize?' + args;
//   });

//   // let state = generateRandomString(16);
//   //   let scope = 'user-read-private user-read-email';
  
//   //   localStorage.setItem('code_verifier', codeVerifier);

//   //   // build parameters for the query
//   //   let args = "response_type=code"
//   //   args += "&client_id=" + clientID
//   //   args += "&scope=" + scope
//   //   args += "&redirect_uri=" + redirectUri
//   //   args += "&state=" + state
//   //   args += "&code_challenge_method=S256"
//   //   args += "&code_challenge=" + pkce_challenge_from_verifier(codeVerifier)
  
//   //   window.location = 'https://accounts.spotify.com/authorize?' + args;
// }


// export async function requestAccessToken(code) {
//   let body = "grant_type=authorization_code"
//   body += "&code=" + code
//   body += "&redirect_uri=" + redirectUri
//   body += "&client_id=" + clientID
//   body += "&code_verifier=" + codeVerifier
//   const requestOpts = {
//     method : 'POST',
//     headers: {'Content-Type' : 'application/x-www-form-urlencoded'},
//     body : body
//   }

//   const response = await fetch('https://accounts.spotify.com/api/token?', requestOpts)
//   const data = await response.json()
//   console.log(data)
//   return JSON.stringify(data)
// }
