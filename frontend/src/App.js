import logo from './logo.svg';
import './App.css';
import React from "react"
import {clientID, clientSecretID } from './private/tokens';
import { requestAuthorization, fetchAccessToken } from './authorization';

const redirect_uri = "http://localhost:3000/"

function handleRedirect(query) {
  let code = getCode(query)
  // console.log(code)
  fetchAccessToken(code, redirect_uri, clientID)
  // window.history.pushState("", "", redirect_uri)
}

function getCode(query) {
  // let code = null
  const urlParams = new URLSearchParams(query);
  let code = urlParams.get('code')
  return code
}
function App() {
  const [token, setToken] = React.useState("")

function onLoad() {
  let query = window.location.search
  if (query.length > 0) {
    handleRedirect(query);
  }
}

  React.useEffect(() => {
    onLoad()
  });

  return (
    <div className="App">
      <button className= "authorize" onClick={() => requestAuthorization(clientID, "http://localhost:3000/")}>Request Authorization</button>
      <p>{token}</p>
    </div>
    
  );
}

export default App;
