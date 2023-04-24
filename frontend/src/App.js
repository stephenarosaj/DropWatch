import logo from './logo.svg';
import './App.css';
import React from "react"
import {clientID, clientSecretID } from './private/tokens';
import { requestAuthorization } from './authorization';


function App() {
  return (
    <div className="App">
      <button className= "authorize" onClick={() => requestAuthorization(clientID, "http://localhost:3000/")}>Request Authorization</button>
    </div>
  );
}

export default App;
