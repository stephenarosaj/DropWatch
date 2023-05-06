export async function getFollowed(setFollowed) {
  let access_token = localStorage.getItem('access_token')
  let headers = {headers : {Authorization: 'Bearer ' + access_token}}
  fetch('https://api.spotify.com/v1/me/following?type=artist&limit=10', headers)
    .then(response => {
      if(!response.ok) {
        throw new Error('HTTP status' + response.status);
      }
      return response.json();
    })
      .then(data => {
        let artists = data.artists
        if(artists !== undefined) {
          console.log(artists)
          if(artists.items !== undefined) {
            console.log(artists.items)
            setFollowed(artists.items)
          }
        }
      })
}

// export async function getUserInfo(query, setStateVar) {
//   let access_token = localStorage.getItem('access_token')
//   let headers = {headers : {Authorization: 'Bearer ' + access_token}}
//   fetch('https://api.spotify.com/v1/me/' + query, headers)
//     .then(response => {
//       if(!response.ok) {
//         throw new Error('HTTP status' + response.status);
//       }
//       return response.json();
//     })
//       .then(data => {
//         if(data.items !== undefined) {
//           console.log(data.items)
//           setStateVar(data.items)
//         }
//       })
// }