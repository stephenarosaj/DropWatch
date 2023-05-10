export default async function getDrops() {
  let url = "http://localhost:3232/drops?user_id=" + localStorage.getItem('user_id')
  return new Promise((resolve, reject) => {
        fetch(url)
            .then(response => response.json())
              .then(results => { 
                console.log(results)
                if (results.data !== undefined) {
                  let drops = results.data
                  resolve(drops)
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