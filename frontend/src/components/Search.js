// import { Card, Col, Container, Row} from "react-bootstrap";
// import SearchResult from "./SearchResult";

// function Search(props) {
//   let middle = <div>
//     hi
//   </div>
//   if(!props.isLoggedIn) {
//     return (
//       <div>
//       </div>
//     )
//   }
//   if(props.drops.length === 0) {
//     middle =
//       <Card className='search-results'>
//         <Card.Text>
//           <p>Your tracked artists haven't dropped anything new since you started tracking them... &lt;/3</p>
//         </Card.Text>
//       </Card>
//   } else {
//     middle = 
//     <Card className='search-results'>
//       <Card.Text>
//         <Container className='search-results-container'>
//           <Row xs={1} md={2} className="g-4">
//             {props.drops.map((item) => (
//               <Col>
//                 <SearchResult artist={item}/>
//               </Col>
//             ))}
//           </Row>
//         </Container>
//       </Card.Text>
//     </Card>
//   }
  
//   return(
//     <div className='search'>
//       <div className='search-text'>
//         <h1 >Your Recent Drops</h1>
//         <p> These are the songs that have dropped from your Watched Artists since you started watching them! </p>
//       </div>
//       <div>
//         {middle}
//       </div>
//     </div>
//   )
// }

// export default Search