export async function getPlaylists(setPlaylists) {
  let access_token = localStorage.getItem('access_token')
  let headers = {headers : {Authorization: 'Bearer ' + access_token}}
  let to_return = []
  fetch('https://api.spotify.com/v1/me/playlists?limit=10', headers)
    .then(response => {
      if(!response.ok) {
        throw new Error('HTTP status' + response.status);
      }
      return response.json();
    })
      .then(data => {
        if(data.items !== undefined) {
          console.log(data.items)
          setPlaylists(data.items)
        }
      })
}