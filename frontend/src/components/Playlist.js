import { Card, Col, Container, Image, Row} from "react-bootstrap";


function Playlist(props) {
  return (
    <Container fluid>
      <Row xs={2}>
        <Col className='playlist-image-col'>
          <Image className="playlist-image" src={props.playlist.images[0].url}/>
        </Col>
        <Col className="playlist-name-col">
          <Card className="playlist-name">
            <Card.Text>
              {props.playlist.name} <br/>
              {props.playlist.owner.display_name}
            </Card.Text>
          </Card>
        </Col>
      </Row>
    </Container>
  )
}

export default Playlist