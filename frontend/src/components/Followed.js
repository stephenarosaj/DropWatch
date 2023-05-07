import {Card, Col, Row, Container} from 'react-bootstrap';
import Artist from './Artist';

/**
 * Component to generate the section for Followed artists
 * @param {prop} props - props containing a boolean to indicate login status and the list of followed artist 
 * @returns 
 */

// TRACK: ONLY RENDER UNTRACK BUTTON IF THE ARTIST IS IN OUR TRACKED ARTISTS ALREADY
// ONLY RENDER TRACK BUTTON IF THE ARTIST IS NOT IN OUR TRACKED ARTISTS

function Followed(props) {
  let middle = null
  if(!props.isLoggedIn) {
    return (
      <div>
      </div>
    )
  }
  if(props.followed_artists.length === 0) {
    middle =
      <Card className='followed-artists'>
          <p>You're not tracking any artists yet! Let's fix that.</p>
          <p>Click one of the buttons below and track some artists!</p>
      </Card>
  } else {
    middle = 
    <Card className='followed-artists'>
        <Container className='followed-artists-container'>
          <Row xs={1} md={2} className="g-4">
            {props.followed_artists.map((item, i) => (
              <Col key={i}>
                <Artist artist={item} class='followed-artist'/>
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