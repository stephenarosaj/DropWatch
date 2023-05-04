import { Card, Col, Container, Image, Row, Stack } from "react-bootstrap";

/*
  We need the artists' name and picture
*/
function Artist(props) {
  <Stack direction="horizontal" gap={0}>
    <Image className="artist-image" src={props.image}/>
    <Card>
      <Card.Text>
        <p>{props.name}</p>
      </Card.Text>
    </Card>
  </Stack>
}

export default Artist