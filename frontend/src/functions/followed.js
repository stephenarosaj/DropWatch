
/**
 * Function to retrieve the current user's followed artists from the backend API
 * @param {function} setFollowed - function to update the followed state variable
 */
export async function getFollowed(setFollowed) {
  let access_token = localStorage.getItem("access_token");
  fetch("http://localhost:3232/user_data?user_token=" + access_token)
    .then((response) => {
      if (!response.ok) {
        throw new Error("HTTP status" + response.status);
      }
      return response.json();
    })
    .then((followingResponse) => {
      if (followingResponse.result === "success") {
        let artists = followingResponse.data;
        if (artists !== undefined) {
          console.log(artists);
          setFollowed(artists);
        }
      } else {
        console.log(followingResponse.result);
      }
    });
}
