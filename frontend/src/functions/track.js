export default async function track(artist_id, operation) {
  let url = "http://localhost:3232/track?user_id=" + localStorage.getItem('user_id')
  url += "&artist_id=" + artist_id
  url += "&operation=" + operation
  return new Promise((resolve, reject) => {
        fetch(url)
            .then(response => response.json())
              .then(results => { 
                if (results.data !== undefined) {
                  let artists = Object.values(results.data)
                  resolve(artists)
                  // let artists = 
                  // resolve(artists)
              } else {
                resolve("Results undefined")
              }
            })
            .catch(e => {
                resolve(e.message)
            });
    }
  )
}