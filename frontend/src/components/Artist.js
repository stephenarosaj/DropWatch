import { Card, Col, Container, Image, Row, Stack } from "react-bootstrap";


function Artist(props) {
  console.log(props.artist.images[2].url)
  console.log(props.artist.name)
  return (
    <Container fluid>
      <Row xs={2}>
        <Col className='dropwatch-img'>
          <Image className="artist-image" src={props.artist.images[0].url}/>
        </Col>
        <Col className="dropwatch-col">
          <Card className="dropwatch-name">
            <Card.Text>
              <p>{props.artist.name}</p>
            </Card.Text>
          </Card>
        </Col>
      </Row>
    </Container>
  )
}

export default Artist