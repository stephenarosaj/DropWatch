import { Card, Col, Container, Image, Row} from "react-bootstrap";

/**
 * Function to create Artist component
 * @param {prop} props - props containing the artist JSON object and css class for the component
 * @returns an artist component that generates an image and name for an artist to be rendered
 */
function Artist(props) {
  return (
    <Container fluid>
      <Row xs={2}>
        <Col className={props.class + '-image-col'}>
          <Image 
            className={props.class + '-image'} 
            src={props.artist.images[0].url} 
            alt={"Artist" + props.artist.name}
          />
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