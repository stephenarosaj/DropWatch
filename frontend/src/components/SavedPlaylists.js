import {Card, Button, Col, Row, Container} from 'react-bootstrap';
import Playlist from './Playlist';

function SavedPlaylists(props) {
  let middle = null
  if(!props.isLoggedIn) {
    return (
      <div>
      </div>
    )
  }
  if(props.playlists.length === 0) {
    middle =
      <Card aria-label='Saved Playlists' className='playlists'>
          <p>You don't have any playlists saved to your Spotify account.</p>
      </Card>
  } else {
    middle = 
    <Card aria-label='Saved Playlists' className='playlists'>
        <Container className='playlists-container'>
          <Row xs={1} md={2} className="g-4">
            {props.playlists.map((item, i) => (
              <Col key={i}>
                <Playlist playlist={item}/>
              </Col>
            ))}
          </Row>
        </Container>
    </Card>
  }
  
  return(
    <div className='saved-playlists'>
      <div className='saved-playlists-text'>
        <h1>Your Saved Playlists</h1>
        <p>These are playlists saved on your account!</p>
      </div>
      <div>
        {middle}
      </div>
    </div>
  )
}

export default SavedPlaylists