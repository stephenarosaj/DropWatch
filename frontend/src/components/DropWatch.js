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
        <Card.Text>
          <p>You're not tracking any artists yet! Let's fix that.</p>
          <p>Click one of the buttons below and track some artists!</p>
        </Card.Text>
      </Card>
  } else {
    middle = 
    <Container>
      <Row xs={1} md={2} lg={3} className="g-4">
        {props.artists.map((item) => (
          <Col>
            <Artist image={item.image} name={item.name}/>
          </Col>
        ))}
      </Row>
    </Container>
  }
  
  return(
    <div className='dropwatch'>
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
        <Card.Text>
        <Container >
          <Row>
            <Col>
              <Button className='search'>Search Songs and Artists</Button>
            </Col>
            <Col>
              <Button className='saved'>Your Saved Playlists</Button>
            </Col>
            <Col>
              <Button className='followed'>Your Followed Artists</Button>
            </Col>
          </Row>
        </Container>
        </Card.Text>
    </Card>
      </div>
    </div>
  )
}

export default DropWatch