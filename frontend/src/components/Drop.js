import { Card, Col, Container, Image, Row} from "react-bootstrap";


function Drop(props) {
  /* 
    Change the following according to the Drop json properties from the backend:
      1. Track image src line 16
      2. artist name 
      3. track name
  */

  return (
    <Container fluid>
      <Row xs={2}>
        <Col className='drop-image-col'>
          <Image className="drop-image" src={'https://i.scdn.co/image/ab67616d0000b27337906edcfbfde42b203097f2'}/>
        </Col>
        <Col className="drop-name-col">
          <Card className="drop-name">
            <Card.Text>
              <p>Cherry Bomb</p>
              <p>Tyler, The Creator</p>
            </Card.Text>
          </Card>
        </Col>
      </Row>
    </Container>
  )
}

export default Drop