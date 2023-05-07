import { Button, Card, Col, Container, Row} from "react-bootstrap";
import SearchResult from "./SearchResult";
import { useState } from "react";
import search from "../functions/search";

function Search(props) {
  const [textBox, setTextBox] = useState("")
  const [middle, setMiddle] = useState(
    <Card className='search-results'/>
  )
  // let middle = null

  if(!props.isLoggedIn) {
    return (
      <div></div>
    )
  }
  
  async function handleSubmit() {
    console.log("textBox: " + textBox)

    search(textBox).then(response => {
        console.log(response)
        renderResults(response)
      }
    )
    // let results = await search(textBox)
    // console.log(results)
    // renderResults(results)
    setTextBox("")
  }

  function generateColumn(item, i) {
    let image = null
    let artist= null 
    let type = item.type
    let name = null
    if(type === "track") {
      console.log("length: " + item.album.images.length)
      if(item.album.images.length !== 0) {
        image = item.album.images[0].url
      }
      artist = item.artists[0].name
      name = item.name
    } else {
      console.log(item)
      if(item.images.length !== 0) {
        image = item.images[0].url
      }
      artist = item.name
    }
    return (
      <Col key={i}>
        <SearchResult
          image={image}
          artist={artist}
          type={type}
          name={name}
        />
      </Col>
    )
  }

  function renderResults(results) {
    console.log(results)
    if(results !== undefined) {
      if(results.length === 0) {
        setMiddle(
        <Card aria-label='Search Results' className='search-results'>
          <p>No results found D:</p>
        </Card>)
      } else {
        setMiddle(
          <Card aria-label='Search Results' className='search-results'>
            <Container className='search-results-container'>
              <Row xs={1} md={2} className="g-4">
                {results.map((item, i) => (
                  generateColumn(item,i)
                )
                )}
              </Row>
            </Container>
          </Card>
        )
      }
    } else {
      console.log("results is undefined")
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