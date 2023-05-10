import {Card, Col, Row, Container} from 'react-bootstrap';
import FollowedArtist from './FollowedArtist';

/**
 * Component to generate the section for Followed artists
 * @param {prop} props - props containing a boolean to indicate login status and the list of followed artist 
 * @returns 
 */

// TRACK: ONLY RENDER UNTRACK BUTTON IF THE ARTIST IS IN OUR TRACKED ARTISTS ALREADY
// ONLY RENDER TRACK BUTTON IF THE ARTIST IS NOT IN OUR TRACKED ARTISTS


function Followed(props) {

  function isTracked(id) {
    let tracked = false
    let tracked_artists = props.trackedArtists
    let i = 0
    while(!tracked && i < tracked_artists.length) {
      tracked = props.trackedArtists[i][0].includes(id)
      i+=1
    }
    return tracked
  }

  let middle = null

  if(!props.isLoggedIn) {
    return (
      <div>
      </div>
    )
  }
  if(props.followed_artists.length === 0) {
    middle =
      <Card aria-label='Followed Artists' className='followed-artists'>
          <p>You're not following any artists on your Spotify account.</p>
      </Card>
  } else {
    middle = 
    <Card aria-label='Followed Artists' className='followed-artists'>
        <Container className='followed-artists-container'>
          <Row xs={1} md={2} className="g-4">
            {props.followed_artists.map((item, i) => (
              <Col key={i}>
                <FollowedArtist 
                  name={item.name} 
                  image={item.images[0].url}
                  id={item.id}
                  isTracked={isTracked(item.id)}
                  setArtists={props.setArtists}
                  class='followed-artist'/>
              </Col>
            ))}
          </Row>
        </Container>
    </Card>
  }
  
  return(
    <div className='followed'>
      <div className='followed-text'>
        <h1>Your Followed Artists</h1>
        <p>These are the artists you follow!</p>
      </div>
      <div>
        {middle}
      </div>
    </div>
  )
}

export default Followed