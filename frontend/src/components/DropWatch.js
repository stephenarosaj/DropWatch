import {Card, Button, Col, Row, Container} from 'react-bootstrap';
import Artist from './Artist';

/**
 * Component to display the artists that a user is tracking
 * @param {*} props 
 * @returns a div that will render the 
 */
function DropWatch(props) {
  let middle = null
  if(!props.isLoggedIn) {
    return (
      <div>
      </div>
    )
  }
  if(props.artists.length === 0) {
    middle =
      <Card className='artists'>
          <p>You're not tracking any artists yet! Let's fix that.</p>
          <p>Click one of the buttons below and track some artists!</p>
      </Card>
  } else {
    middle = 
    <Card className='artists'>
        <Container className='artists-container'>
          <Row xs={1} md={2} className="g-4">
            {props.artists.map((item, i) => (
              <Col key={i}>
                <Artist artist={item} class='artist'/>
              </Col>
            ))}
          </Row>
        </Container>
    </Card>
  }
  
  return(
    <div className='dropwatch' aria-label='DropWatch Section'>
      <div className='dropwatch-text'>
        <h1 >Your DropWatch</h1>
        <p> These are all the artists you're currently tracking </p>
      </div>
      <div>
        {middle}
      </div>
      <div>
      <Card className='bottom'>
        <Card.Title className='add-artists'>
          Missing someone? Add them in one of 3 ways:
        </Card.Title>
        <Container>
          <Row>
            <Col>
              <Button className='search-button'>Search Songs and Artists</Button>
            </Col>
            <Col>
              <Button className='saved-button'>Your Saved Playlists</Button>
            </Col>
            <Col>
              <Button className='followed-button'>Your Followed Artists</Button>
            </Col>
          </Row>
        </Container>
    </Card>
      </div>
    </div>
  )
}

export default DropWatch