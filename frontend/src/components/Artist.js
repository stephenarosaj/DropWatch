import { Card, Col, Container, Image, Row} from "react-bootstrap";


function Artist(props) {
  return (
    <Container fluid>
      <Row xs={2}>
        <Col className='artist-image-col'>
          <Image className="artist-image" src={props.artist.images[0].url}/>
        </Col>
        <Col className="artist-name-col">
          <Card className="artist-name">
            <Card.Text>
              {props.artist.name}
            </Card.Text>
          </Card>
        </Col>
      </Row>
    </Container>
  )
}

export default Artist