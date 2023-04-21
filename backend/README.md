# Capstone: Maps

##### An API and frontend for redlining data filtering and overlaying in the US.

##### mmongado and bmaizes

##### Estimated Time: 15 hours

##### Repo Link: https://github.com/cs0320-s2023/sprint-5-bmaizes-mmongado

### Design Choices

We have Handler classes for each endpoint of the Server in the "endpointHandlers" package. We have
two Record classes to store deserialized JSONS - one for Location, one for Hourly Forecast. We have
a wrapper class for Coordinates that determines if two points are close enough together by
overriding the equals() method. We have a wrapper class for API requests and a WeatherProxy class
that wraps a Cache in between the WeatherHandler and API requests. We have a moshiUtil class for
serializing / deserializing. We also have APIRequestExceptions thrown when API connections fail,
BadColumnExceptions thrown when invalid columns are requested, and DeserializeExceptions thrown when
moshi deserializing fails.

### Errors and Bugs

No known errors or bugs.

CheckStyle notes that the package names don't follow the preferred format, but we didn't want to
change them since they are connected to the class repo. It also noted that in some cases the
variable names didn't follow naming convention since it had consecutive capitals, but we didn't
change them as they represented real life acronyms like CSV and JSON.

### Tests

The tests are in the src/test/java/edu/brown/cs/student/api file. They reflect the structure of the
main package - all classes are tested except for the Record format classes.

##### LoadHandler Tests

##### SearchHandler Tests

*File Not Found:* Tests that the SearchHandler correctly gives an error_datasource when the file is
not found.

*Bad Column Exception:* Tests that the SearchHandler correctly gives data about invalid columns
passed in.

*Valid Search:* Tests that the SearchHandler populates all fields correctly for a valid search.

##### ViewHandler Tests

*No Loaded File:* Tests view without a file loaded

*Valid file:* Tests view with a valid file

*Multiple Files* Tests view on multiple files

##### WeatherHandler Tests

*Valid Request:* Tests that the WeatherHandler can properly handle / parse a valid coordinate
request.

*Invalid Coordinates:* Tests that the WeatherHandler gives a error_data_retrieval error and response
code for coordinates that don't exist.

*Missing Params:* Tests that the WeatherHandler gives an error_bad_request when there is no latitude
or longitude.

*Non-Numerical Params:* Tests that the WeatherHandler gives an error_bad_json for non-numerical
latitudes and longitudes.

##### Coordinates Tests

*Close Equals:* Tests that two Coordinates that are closer than some specified distance are
registered as equal.

*Hash Code:* Tests that two different Coordinate objects representing the same lat/long map to the
same hashCode.

##### RequestAPI Tests

*Valid Connection:* Tests that a 200 response code is returned for a valid connection.

*Invalid Coordinates Throws Exception:* Tests that non-existent coordinates throw a BadAPIException
with a 400 error code.

*Valid Outside US Throws Exception:* Tests that coordinates outside the U.S. throw a BadAPIException
with a 404 error code.

##### WeatherRequestProxy Tests

*Cache Hit and Miss:* Tests that the cache properly registers hits and misses.

*Cache Eviction:* Tests that the cache evicts entries when its max_size is exceeded.

##### MoshiUtilTest

*Serialize:* Tests serializing a response map into a string.

*Location Deserialize:* Tests proper deserializing of a JSON Buffer Location into a Location record.

*Hourly Forecast Deserialize:* Tests proper deserializing of a JSON Buffer Hourly Forecast into an
HourlyForecast record.

*Throws Deserialize Exception:* Tests that deserializeLocation and deserializeForecast throw a
DeserializeException when given a nonsensical JSON.

##### GeoHandler Tests

*Unit Tests:* We perform mock unit testing to ensure that every backend function in GeoHandler is
operating correctly. We test valid coordinates, invalid coordinates as well as searching. This is 
also the location of our fuzz testing where we test randomly generated bounding boxes. We ensure that
the product is completely enclosed in the box as well. 

*Integration Tests:* Here we test the entire API. We adequately start and teardown the server, and test
completely valid filter requests. We then also examine improper inputs for non-numerical inserts, and 
then verify the functionality of backend persistence for searches. 

##### Frontend Tests
*Render Tests:* We test the frontend app to make sure everything renders.

*User Input Tests:* We test the frontend app to make sure the textbox / submit button change with user input.

### Running Program

##### Program

You can run ./run in the command line or on IntelliJ; enter browser requests like this with the
following parameters:

You also can just run the server file. 

http://localhost:3232/loadcsv?filepath=/Users/maiamongado/cs32/sprint-3-mmongado-rornelas/data/pets.csv

http://localhost:3232/viewcsv

http://localhost:3232/searchcsv?target=fido&has_headers=true&column=1

http://localhost:3232/weather?lat=29.4252&lon=-98.4946

http://localhost:3232/weather?lat=29.4252&lon=-98.4946

http://localhost:3232/geo?latMin=24&latMax=48&lonMin=-160&lonMax=-60 
OR
http://localhost:3232/geo?latMin=24&latMax=48&lonMin=-160&lonMax=-60&keyword=<keyword you want> 


### Whose labor?

Languages: HTML, TypeScript, CSS, Java
Libraries/Packages: Spark, Moshi, Mapbox, Java Built-Ins, React, JavaScript Built-Ins
Formats: JSON, GeoJSON
Development Environments: IntelliJ, VSCode, GitHub, Maven, NPM