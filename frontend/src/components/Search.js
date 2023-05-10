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

  function isTracked(id) {
    let tracked = false
    let tracked_artists = props.trackedArtists
    let i = 0
    while(!tracked && i < tracked_artists.length) {
      tracked = props.trackedArtists[i][0].includes(id)
      i+=1
    }
    return tracked
  }
  
  async function handleSubmit() {

    search(textBox).then(response => {
        renderResults(response)
      }
    )
    setTextBox("")
  }

  function generateColumn(item, i) {
    let image = null
    let artist= null 
    let type = item.type
    let name = null
    let artistTracked = false
    if(type === "track") {
      if(item.album.images.length !== 0) {
        image = item.album.images[0].url
      }
      artist = item.artists[0]
      name = item.name
    } else {
      if(item.images.length !== 0) {
        image = item.images[0].url
      }
      artist = item
      artistTracked = isTracked(artist.id)
    }
    return (
      <Col key={i}>
        <SearchResult
          image={image}
          artist={artist}
          type={type}
          name={name}
          trackedArtists={props.trackedArtists}
          setArtists={props.setArtists}
          isTracked={artistTracked}
        />
      </Col>
    )
  }

  function renderResults(results) {
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
          aria-live={"off"}
          aria-label="Search for artists and songs here!"
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