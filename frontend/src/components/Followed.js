function Followed(props) {
  let middle = null
  if(!props.isLoggedIn) {
    return (
      <div>
      </div>
    )
  }
  if(props.followed_artists.length === 0) {
    middle =
      <Card className='followed-artists'>
          <p>You're not tracking any artists yet! Let's fix that.</p>
          <p>Click one of the buttons below and track some artists!</p>
      </Card>
  } else {
    middle = 
    <Card className='followed-artists'>
        <Container className='followed-artists-container'>
          <Row xs={1} md={2} className="g-4">
            {props.followed_artists.map((item, i) => (
              <Col key={i}>
                <FollowedArtist artist={item}/>
              </Col>
            ))}
          </Row>
        </Container>
    </Card>
  }
  
  return(
    <div className='followed'>
      <div className='followed-text'>
        <h1>Your Followed Artists</h1>
        <p>These are the artists you follow!</p>
      </div>
      <div>
        {middle}
      </div>
    </div>
  )
}

export default Followed