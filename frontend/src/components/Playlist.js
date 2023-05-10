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
          <a href={props.playlist.external_urls.spotify} target="_blank">
            <p>{props.playlist.name}</p>
          </a>
              <p aria-label="playlist owner">{props.playlist.owner.display_name}</p>
          </Card>
        </Col>
      </Row>
    </Container>
    
  )
}

export default Playlist