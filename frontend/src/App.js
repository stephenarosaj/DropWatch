// import logo from './logo.svg';
import './App.css';
import {useEffect, useState} from "react"
// import {clientID, clientSecretID } from './private/tokens';
import { requestAuthorization, fetchAccessToken} from './authorization';
import {Col, Row, Container, Image, Card, Button} from 'react-bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css';
import Welcome from './components/Welcome';
import DropWatch from './components/DropWatch';
import Drops from './components/Drops';
import Search from './components/Search';
import Playlists from './components/Playlists';
import Followed from './components/Followed';
import artistData from "./data/artists.json";

const redirect_uri = "http://localhost:3000/callback"

function getCode(query) {
  const urlParams = new URLSearchParams(query);
  let code = urlParams.get('code')
  return code
}
function App() {
  const [refreshToken, setRefreshToken] = useState(null)
  const [username, setUsername] = useState(null)
  const [artists, setArtists] = useState(artistData)
  const [drops, setDrops] = useState([])
  const [isLoggedIn, setLogin] = useState(false)

  function handleRedirect(query) {
    let code = getCode(query)
    console.log("code: " + code)
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
      <Welcome isLoggedIn={isLoggedIn} username={username}/>
      <DropWatch artists={artists} isLoggedIn={isLoggedIn}/>
      <Drops drops={drops} isLoggedIn={isLoggedIn}/>
      <Search isLoggedIn={isLoggedIn}/>
      <Playlists isLoggedIn={isLoggedIn} refreshToken={refreshToken} setRefreshToken={setRefreshToken}/>
      <Followed isLoggedIn={isLoggedIn} refreshToken={refreshToken} setRefreshToken={setRefreshToken}/>
    </div>

    
    
  );
}

export default App;