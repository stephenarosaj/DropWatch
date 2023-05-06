import { Card, Col, Container, Image, Row} from "react-bootstrap";


function Artist(props) {
  return (
    <Container fluid>
      <Row xs={2}>
        <Col className={props.class + '-image-col'}>
          <Image className={props.class + '-image'} src={props.artist.images[0].url}/>
        </Col>
        <Col className={props.class + '-name-col'}>
          <Card className={props.class + '-name'}>
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