// import logo from './logo.svg';
import './App.css';
import {useEffect, useState} from "react"
// import {clientID, clientSecretID } from './private/tokens';
import { requestAuthorization, fetchAccessToken} from './functions/authorization';
import {Col, Row, Container, Image, Card, Button} from 'react-bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css';
import Welcome from './components/Welcome';
import DropWatch from './components/DropWatch';
import RecentDrops from './components/RecentDrops';
import Search from './components/Search';
import SavedPlaylists from './components/SavedPlaylists';
import Followed from './components/Followed';
import artistData from "./data/artists.json";
import track from './functions/track';

const redirect_uri = "http://localhost:3000/callback"



/**
 * @function getCode - a function to get the authorization code returned from Spotify API upon authorization
 * @param {String} query 
 * @returns The code provided by the Spotify API upon authorization
 */
function getCode(query) {
  const urlParams = new URLSearchParams(query);
  let code = urlParams.get('code')
  return code
}

function App() {
  // const [refreshToken, setRefreshToken] = useState(null)
  const [username, setUsername] = useState(null)
  const [artists, setArtists] = useState([])
  const [isLoggedIn, setLogin] = useState(false)
  const[playlists, setPlaylists] = useState([])
  const [followed, setFollowed] = useState([])

  /**
   * @function handleRedirect - function to retrieve the Spotify authorization code and generate
   * the access token needed for accessing Spotify user information.
   * @param {String} query - the query parameters in the current url
   */
  function handleRedirect(query) {
    let code = getCode(query)
    console.log("code: " + code)
    fetchAccessToken(code, setLogin, setUsername, setPlaylists, setFollowed, setArtists)
    window.history.pushState("","", redirect_uri)
  }

  /**
   * @function onLoad - Function to check if authorization has been performed yet and handle the steps 
   * required after user authorization.
   */
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
                  <p className='subtitle'>
                    DropWatch is a webapp that allows you to stay up to date with your favorite artists' new music. 
                    Through DropWatch, you can choose artists to track, and DropWatch will provide a centralized place to let you know about all the latest releases from these artists. 
                  </p>
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

                    DropWatch asks you to log into Spotify so that you can browse your playlists and followed artists to choose new artists to track <br /> 
                    We collect your username, saved playlists, and followed artists associated with your Spotify account.<br /> 
                    We do not collect your email, password, or other personal data stored in your account. <br /> 
                    We will never share this collected information with any other platforms. This is collected solely to display the information to you.
                  </Card.Text>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        </Container>
      </div>
      <Welcome isLoggedIn={isLoggedIn} username={username}/>
      <DropWatch artists={artists} isLoggedIn={isLoggedIn} setArtists={setArtists}/>
      {/* <RecentDrops drops={drops} isLoggedIn={isLoggedIn}/> */}
      <Search isLoggedIn={isLoggedIn} trackedArtists={artists} setArtists={setArtists}/>
      <SavedPlaylists isLoggedIn={isLoggedIn} playlists={playlists}/>
      <Followed 
        isLoggedIn={isLoggedIn} 
        followed_artists={followed} 
        trackedArtists={artists} 
        setArtists={setArtists}/>
    </div>

    
    
  );
}

export default App;