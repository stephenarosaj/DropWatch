import { Card, Col, Container, Image, Row, Button} from "react-bootstrap";
import track from "../functions/track";

/**
 * Function to create Artist component
 * @param {prop} props - props containing the artist JSON object and css class for the component
 * @returns an artist component that generates an image and name for an artist to be rendered
 */
function Artist(props) {
  async function handleTrack(operation) {
    track(props.id, operation)
      .then(response => {
        props.setArtists(response)
      }
      )
  }
  return (
    <Container fluid>
      <Row xs={2}>
        <Col className={props.class + '-image-col'}>
          <Image 
            className={props.class + '-image'} 
            src={props.image} 
            alt={"Artist" + props.name}
          />
        </Col>
        <Col className={props.class + '-name-col'}>
          <Card className={props.class + '-name'}>
            <Card.Text aria-label="Artist name">
              {props.name}
            </Card.Text>
            <Button onClick={() => handleTrack('delete')}>Untrack Artist</Button>
          </Card>
        </Col>
      </Row>
    </Container>
  )
}

export default Artist