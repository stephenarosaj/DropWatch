import { Card, Col, Container, Image, Row, Button} from "react-bootstrap";
import track from "../functions/track";
import { useState } from "react";


function SearchResult(props) {

  async function handleTrack(operation) {
    track(props.artist.id, operation)
      .then(response => {
        console.log("id: " + props.artist.id)
        console.log("operation: " +operation)
        console.log(response)
        props.setArtists(response)
      })
  }

  let button = <div></div>
  if(props.type === 'artist') {
    button = <div>
      <Button className="track-button" onClick={() => handleTrack('delete')}>Untrack Artist</Button>
      <Button onClick={() => handleTrack('add')}>Track Artist</Button>
    </div>
    // if(props.isTracked) {
    //   setButton(<Button onClick={() => handleTrack('delete')}>Untrack Artist</Button>)
    // } else {
    //   setButton(<Button onClick={() => handleTrack('add')}>Track Artist</Button>)
    // }
  }

  return (
    <Container fluid>
      <Row xs={2}>
        <Col className='search-result-image-col'>
          <Image alt={props.name} className="search-result-image" src={props.image}/>
        </Col>
        <Col className="search-result-name-col">
          <Card className="search-result-name">
              <p>{props.name}</p>
              <p>{props.type}</p>
              <p>{props.artist.name}</p>
              {button}
          </Card>
        </Col>
      </Row>
    </Container>
  )
}

export default SearchResult