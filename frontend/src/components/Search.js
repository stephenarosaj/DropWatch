import { Button, Card, Col, Container, Row} from "react-bootstrap";
import SearchResult from "./SearchResult";
import { useState } from "react";
import { search } from "../functions/search";

function Search(props) {
  const [textBox, setTextBox] = useState("")
  let middle = null

  if(!props.isLoggedIn) {
    return (
      <div></div>
    )
  }
  
  async function handleSubmit() {
    console.log("textBox: " + textBox)
    let results = await search(textBox)
    console.log(results)
    renderResults(results)
    setTextBox("")
  }

  function renderResults(results) {
    if(results.length === 0) {
      middle =
        <Card className='search-results'>
            <p>No results found D:</p>
        </Card>
    } else {
      middle = 
      <Card className='search-results'>
          <Container className='search-results-container'>
            <Row xs={1} md={2} className="g-4">
              {props.artists.map((item, i) => (
                <Col key={i}>
                  <SearchResult 
                  image={item.images[0].url} 
                  artist={item.artists[0].name}
                  type={item.type}
                  name={item.name}
                  />
                </Col>
              ))}
            </Row>
          </Container>
      </Card>
    }
  }



  return(
    <div className='search'>
      <div className='search-text'>
        <h1>Search Songs and Artists</h1>
        <p>Search for music and artists to track!</p>
      </div>
      <div>
        <input
          aria-description={"Search for songs and artists here!"}
          aria-live={"off"}
          role={"textbox"}
          type="text"
          placeholder={"Search"}
          className="search-input-box" 
          value={textBox}
          id="repl-input" 
          onChange={(e => setTextBox(e.target.value))}
          onKeyUp={(e) => {
            if (e.key === "Enter") {
              handleSubmit()
            }
          }}
        />
        <Button className='search-section-button' onClick={() => handleSubmit()}>Search</Button>
      </div>
      <div>
        {middle}
      </div>
    </div>
  )
}

export default Search