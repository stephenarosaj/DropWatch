import Card from 'react-bootstrap/Card'

function DropWatch(props) {
  let middle = null

  if(props.artists.length === 0) {
    middle =
      <Card className='artists'>
        <Card.Text>
          <p>You're not tracking any artists yet! Let's fix that.</p>
          <p>Click one of the buttons below and track some artists!</p>
        </Card.Text>
      </Card>
  }
  return(
    <div className='dropwatch'>
      <h1 >Your DropWatch</h1>
      <p>These are all the artists you're currently tracking</p>
      <div>
        {middle}
      </div>
      <div>
        
      </div>
    </div>
  )
}

export default DropWatch