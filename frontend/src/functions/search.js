function sortPopularity(results) {
  const sorted = [...results]
  sorted.sort((a,b) => b.popularity - a.popularity)
  return(sorted)
}

export async function search(query) {
  let to_return = null
  fetch("http://localhost:3232/search?query=" + query + "&offset=0")
      .then(response => {
        if(!response.ok) {
          throw new Error('HTTP status for search' + response.status);
        }
        return response.json();
      })
        .then(results => {
          let all_results = results.data
          if(all_results !== undefined) {
            let artists = all_results.artists.items
            let albums = all_results.albums.items
            let tracks = all_results.tracks.items
            to_return = sortPopularity(artists.concat(tracks)) 
            console.log(to_return)
            // uncomment below when we fix the albums response
            // to_return = artists.concat(albums, tracks)
          }
        })
  return to_return
}
