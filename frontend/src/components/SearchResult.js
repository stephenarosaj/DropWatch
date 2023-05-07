import { Card, Col, Container, Image, Row} from "react-bootstrap";


function SearchResult(props) {
  /* 
    Change the following according to the Drop json properties from the backend:
      1. Track image src line 16
      2. artist name 
      3. track name
  */

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
              <p>{props.artist}</p>
          </Card>
        </Col>
      </Row>
    </Container>
  )
}

export default SearchResult