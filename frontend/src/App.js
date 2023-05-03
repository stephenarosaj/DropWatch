// import logo from './logo.svg';
import './App.css';
import {useEffect, useState} from "react"
// import {clientID, clientSecretID } from './private/tokens';
import { requestAuthorization, fetchAccessToken} from './authorization';
import 'bootstrap/dist/css/bootstrap.min.css';
import Col from 'react-bootstrap/Col';
import Row from 'react-bootstrap/Row';
import Container from 'react-bootstrap/Container';
import Image from 'react-bootstrap/Image'
import Card from 'react-bootstrap/Card'
import Button from 'react-bootstrap/Button';
import Welcome from './components/Welcome';
import DropWatch from './components/DropWatch';

const redirect_uri = "http://localhost:3000/callback"



//AQBqff_vdV3ErtEF8dodQ6MRYtw66ItIWCQqFs1tSZV0M3KNl4TfWZzOdPWG4yAQ7pd0uXnhBh6g3o7M0nJLDu1BwmJvN0yQYjsInGgOH3HWYw322iG8mHoczLltUdZ-RvcvWiqvVDW5hn_2QBPrwMUIVFWVwn1vvcmIPKYL_V-yRD1p7Ot1YhPsFa17z7R_lDoWbMZ-GH2dPMHw0Vb9zmCgN1wEYw6_YEGKa_gtm7EsqFj2zhyu6_KAW_4Izx8HVVTlX1pHddtrQuTc_JJsLL7511xBSTtmVQ&state=JThm6QyPkWEAKIVz

function getCode(query) {
  const urlParams = new URLSearchParams(query);
  let code = urlParams.get('code')
  return code
}
function App() {
  const [refreshToken, setRefreshToken] = useState(null)
  const [username, setUsername] = useState(null)
  const [artists, setArtists] = useState([])
  // const [drops, setDrops] = useState([])
  const [isLoggedIn, setLogin] = useState(false)

  function handleRedirect(query) {
    let code = getCode(query)
    console.log("code: "+code)
    fetchAccessToken(code, setLogin, setRefreshToken, setUsername)
    window.history.pushState("","", redirect_uri)
  }

  function onLoad() {
    let query = window.location.search
    if (query.length > 0) {
      handleRedirect(query);
    }
}

  useEffect(() => {
    onLoad()
  });

  return (
    // need to turn this stuff into a component so we can have access to the state variables
    <div className="App">
      {/* <button className= "authorize" onClick={() => requestAuthorization()}>Request Authorization</button>
      <p>{token}</p> */}

      {/* <div className="home-page">

      </div> */}
      <div>
      <Container fluid className="login-page">
        <Row>
          <Col>
            <div>
              <Image className="logo" src= {require("./images/dropwatch_logo.png")} alt='Green and Black DropWatch logo'/>
            </div>
          </Col>
          <Col xs={6}>
            <div className='middle-section'>
              <h1 className='middle-section'> Welcome to <span style={{color: '#00C437'}}>DropWatch</span> &lt;3</h1>
              <div>
                <h3 style={{marginBottom:0}}>You love music. We do too.</h3>
                <h3>That's why we made DropWatch.</h3>
                <p className='subtitle' style={{marginBottom:0}}>No more checking your favorite artists' pages every day for new music.</p>
                <p className='subtitle'>Let us do the checking for you ;&#41;</p>
                <hr/>
                <h3>What is DropWatch?</h3>
                <p className='subtitle'>DropWatch is a...</p>
              </div>
            </div>
          </Col>
          <Col xs={4}>
            <Card className="auth-card">
              <Card.Body style={{textAlign: 'center'}}>
                <Card.Title className='auth-card-title'>Connect your Spotify account to get started</Card.Title>
                <Button className='auth-button' onClick={() => requestAuthorization()}>Login with Spotify</Button>
                <Card.Text style={{textAlign: 'left'}}>
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
      <div>
        <Welcome isLoggedIn={isLoggedIn} username={username}/>
      </div>
      <div>
        <DropWatch artists={artists}/>
      </div>
    </div>

    
    
  );
}

export default App;