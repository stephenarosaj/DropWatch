function Welcome(props) {
  if(!props.isLoggedIn) {
    return (
      <div className='welcome-section'>
        <h1>You're not logged in yet!</h1>
        <p>Click 'Login with Spotify' to get started!</p>
      </div>
    )
  }

  return (
    <div className='welcome-section'>
        <h1>Hello, <span style={{color: '#00C437'}}>{props.username}</span></h1>
        <p>Scroll to see your artists' new drops and track more!</p>
    </div>
  )
}

export default Welcome