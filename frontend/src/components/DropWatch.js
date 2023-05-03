import Card from 'react-bootstrap/Card'

function DropWatch(props) {
  let middle = null

  if(length(props.artists) === 0) {
    middle = 
    <div>
      <Card>
        <Card.Text>
          <p>You're not tracking any artists yet! Let's fix that.</p>
          <p>Click one of the buttons below and track some artists!</p>
        </Card.Text>
      </Card>
    </div>
  }
  return(
    <div>
      <h1 className="dropwatch">Your DropWatch</h1>
      <p>These are all the artists you're currently tracking</p>
      <div>
        {middle}
      </div>
      <div>
        
      </div>
    </div>
  )
}