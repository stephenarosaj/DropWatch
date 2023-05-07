export default async function track(query) {
  return new Promise((resolve, reject) => {
        const data =
        fetch("http://localhost:3232/search?query=" + query + "&offset=0")
            .then(response => response.json())
              .then(results => { 
                if (results.data !== undefined) {
                  let artists = results.data.artists.items
                  let tracks = results.data.tracks.items
                  resolve(sortPopularity(artists.concat(tracks)))
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