import logo from './logo.svg';
import './App.css';
import React from "react"
import {clientID, clientSecretID } from './private/tokens';
// import { requestAuthorization, fetchAccessToken } from './authorization';
import { requestAuthorization, requestAccessToken } from './new_authorization';
// import { requestAuthorization } from './new_authorization';
import 'bootstrap/dist/css/bootstrap.min.css';
import Col from 'react-bootstrap/Col';
import Row from 'react-bootstrap/Row';
import Container from 'react-bootstrap/Container';
import Image from 'react-bootstrap/Image'
import Card from 'react-bootstrap/Card'
import Button from 'react-bootstrap/Button';
import Dropdown from 'react-bootstrap/Dropdown';

const redirect_uri = "http://localhost:3000/callback"



//AQBqff_vdV3ErtEF8dodQ6MRYtw66ItIWCQqFs1tSZV0M3KNl4TfWZzOdPWG4yAQ7pd0uXnhBh6g3o7M0nJLDu1BwmJvN0yQYjsInGgOH3HWYw322iG8mHoczLltUdZ-RvcvWiqvVDW5hn_2QBPrwMUIVFWVwn1vvcmIPKYL_V-yRD1p7Ot1YhPsFa17z7R_lDoWbMZ-GH2dPMHw0Vb9zmCgN1wEYw6_YEGKa_gtm7EsqFj2zhyu6_KAW_4Izx8HVVTlX1pHddtrQuTc_JJsLL7511xBSTtmVQ&state=JThm6QyPkWEAKIVz

function getCode(query) {
  const urlParams = new URLSearchParams(query);
  let code = urlParams.get('code')
  return code
}
function App() {
  const [token, setToken] = React.useState("No token yet")
  async function handleRedirect(query) {
    let code = getCode(query)
    // setToken(fetchAccessToken(code, redirect_uri, clientID))
    let access_token = await requestAccessToken(code)
    setToken(access_token)
    // getAccessToken(code)
    // window.history.pushState("", "", redirect_uri)
  }
function onLoad() {
  let query = window.location.search
  if (query.length > 0) {
    handleRedirect(query);
  }
}

  // React.useEffect(() => {
  //   onLoad()
  // });

  return (
    <div className="App">
      {/* <button className= "authorize" onClick={() => requestAuthorization()}>Request Authorization</button>
      <p>{token}</p> */}

      {/* <div className="home-page">

      </div> */}

      <Container className="login-page">
        <Row>
          <Col xs={1}>
            <div>
              <Image className="logo" src= {require("./images/placeholder.png")}/>
            </div>
          </Col>
          <Col xs={8}>
            <div className="middle-section">
              <p> Welcome to <span style={{color: '#00C437'}}>DropWatch</span> &lt;3</p>
            </div>
          </Col>
          <Col xs={4}>
            <Card className="auth-card">
              <Card.Body>
                <Card.Title>Connect your Spotify account to get started</Card.Title>
                <Button>Login with Spotify</Button>
                <Card.Text>
                  What data does DropWatch collect? <br/>

                  DropWatch asks you to log into spotify so that... <br /> 
                  We collect your...<br /> 
                  We do not collect your...<br /> 
                  We will never share your...
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>

    
    
  );
}

export default App;