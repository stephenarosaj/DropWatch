# DropWatch: A Spotify WebApp
###_**Never miss your favorite artists' next drop with DropWatch!**_

###### A final project for CSCI 0320: Introduction to Software Engineering @ Brown University
###### Made with <3 by Stephen Rosa, Bryanna Pajotte, Aku Anasi, and Maia Mongado

**Link to GitHub Repo:** https://github.com/stephenarosaj/DropWatch.git

**Team members:**
- Rosa (`srosa5`)
- Bryanna (`bpajotte`)
- Maia (`mmongado`)
- Aku (`canasi`)

## How to Get Started:
This app allows a user to login with their Spotify account and track/untrack artists. When a user is tracking an artist, they can see any of that artist's most recent drops in the app. They can also search for artists to track, and can see their followed playlists in the UI.

In order to run the backend, navigate to Dropwatch/backend and enter ./run in a terminal (make sure to run mvn package first!) The backend will run on https://localhost:3232.

In order to run the frontend, navigate to Dropwatch/frontend and enter npm start in a terminal. The frontend will run on https://localhost:3000. 

## Design Choices:
Our design is composed of three major components: the frontend, the backend, and the database.

The frontend deals with visual UI, as well as OAuth authorization from Spotify. Once the user has accepted the OAuth request, it recieves an access_token that it calls our backend with, and displays the data it gets in the UI.
Our backend deals with API requests to Spotify; using the proper access token, it calls the Spotify API, deserializes the information, and either serves it to the frontend or uses it to update the database.
The database is a Sequel DB that allows us to store persistent info even when our Server is not running, such as the artists users are tracking, and what the most recent drop of an artist was, in order to determine when they have dropped something new.

## Accessibility:
Our app aims to be accessible by having appropriate ARIA labels that assist with screenreading every aspect of the page,
and Zoom that makes the font larger but doesn't make anything out of the scope of the page.
 
## Our Tests:
DropWatchDBUnitTests: Unit testing the capability of the DropWatch database.
SQLiteDBUnitTest: Unit testing the SQLite database.


**A Note for Non-U.S. Users:**
Currently, our app is focused on music denoted as a part of the U.S. market by the Spotify API. 
Future updates to this project beyond the scope of this CSCI course could include an expansion to markets outside the U.S.
