import {Card, Col, Row, Container} from 'react-bootstrap';
import Drop from './Drop';

function RecentDrops(props) {
  let middle = <div></div>
  if(!props.isLoggedIn) {
    return (
      <div>
      </div>
    )
  }
  if(props.drops.length === 0) {
    middle =
      <Card className='drops'>
          <p>You don't have any recent drops.</p>
      </Card>
  } 
  // else {
  //   middle = 
  //   <Card className='drops'>
  //       <Container className='drops-container'>
  //         <Row xs={1} md={2} className="g-4">
  //           {props.artists.map((item, i) => (
  //             <Col key={i}>
  //               <Drop 
  //                 image={item[1]} 
  //                 name={item[2]} 
  //                 id={item[0].substring(35)} 
  //                 class='drop' 
  //                 setArtists={props.setArtists}/>
  //             </Col>
  //           ))}
  //         </Row>
  //       </Container>
  //   </Card>
  // }
  
  return(
    <div className='recent-drops' aria-label='Recent Drops section'>
      <div className='recent-drops-text'>
        <h1 >Your Recent Drops</h1>
        <p> These are the songs that have Dropped from your Watched Artists since you started watching them!  </p>
      </div>
      <div>
        {middle}
      </div>
    </div>
  )
}

export default RecentDrops