function sortPopularity(results) {
  const sorted = [...results]
  sorted.sort((a,b) => b.popularity - a.popularity)
  return(sorted)
}

export default async function search(query) {
  return new Promise((resolve, reject) => {
        const data =
        fetch("http://localhost:3232/search?query=" + query + "&offset=0")
            .then(response => response.json())
              .then(results => { 
                console.log(results)
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
