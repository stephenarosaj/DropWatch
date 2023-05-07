import { Card, Col, Container, Image, Row} from "react-bootstrap";


function Playlist(props) {
  return (
    <Container fluid>
      <Row xs={2}>
        <Col className='playlist-image-col'>
          <Image alt={props.playlist.name + "playlist"} className="playlist-image" src={props.playlist.images[0].url}/>
        </Col>
        <Col className="playlist-name-col">
          <Card className="playlist-name">
              <p aria-label="playlist name">{props.playlist.name}</p>
              <p aria-label="playlist owner">{props.playlist.owner.display_name}</p>
          </Card>
        </Col>
      </Row>
    </Container>
  )
}

export default Playlist