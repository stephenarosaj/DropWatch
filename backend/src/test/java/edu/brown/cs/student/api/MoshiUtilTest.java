package edu.brown.cs.student.api;

public class MoshiUtilTest {

  //  /** Tests serializing a Map with moshi. */
  //  @Test
  //  public void testSerialize() {
  //    String expected =
  //        "{\"result\":\"success\",\"test_list\":[[\"henry\",\"spot\"],[\"camilla\",\"fido\"]]}";
  //
  //    Map<String, Object> responseMap = new HashMap<>();
  //    List<List<String>> list = List.of(List.of("henry", "spot"), List.of("camilla", "fido"));
  //
  //    responseMap.put("test_list", list);
  //    assertEquals(expected, MoshiUtil.serialize(responseMap, "success"));
  //  }
  //
  //  /**
  //   * Tests deseralizing a location JSON buffer.
  //   *
  //   * @throws IOException
  //   * @throws DeserializeException
  //   */
  //  @Test
  //  public void testLocationDeserialize() throws IOException, DeserializeException {
  //    String mockedString =
  //        "{\n"
  //            + "    \"@context\": [\n"
  //            + "        \"https://geojson.org/geojson-ld/geojson-context.jsonld\",\n"
  //            + "        {\n"
  //            + "            \"@version\": \"1.1\",\n"
  //            + "            \"wx\": \"https://api.weather.gov/ontology#\",\n"
  //            + "            \"s\": \"https://schema.org/\",\n"
  //            + "            \"geo\": \"http://www.opengis.net/ont/geosparql#\",\n"
  //            + "            \"unit\": \"http://codes.wmo.int/common/unit/\",\n"
  //            + "            \"@vocab\": \"https://api.weather.gov/ontology#\",\n"
  //            + "            \"geometry\": {\n"
  //            + "                \"@id\": \"s:GeoCoordinates\",\n"
  //            + "                \"@type\": \"geo:wktLiteral\"\n"
  //            + "            },\n"
  //            + "            \"city\": \"s:addressLocality\",\n"
  //            + "            \"state\": \"s:addressRegion\",\n"
  //            + "            \"distance\": {\n"
  //            + "                \"@id\": \"s:Distance\",\n"
  //            + "                \"@type\": \"s:QuantitativeValue\"\n"
  //            + "            },\n"
  //            + "            \"bearing\": {\n"
  //            + "                \"@type\": \"s:QuantitativeValue\"\n"
  //            + "            },\n"
  //            + "            \"value\": {\n"
  //            + "                \"@id\": \"s:value\"\n"
  //            + "            },\n"
  //            + "            \"unitCode\": {\n"
  //            + "                \"@id\": \"s:unitCode\",\n"
  //            + "                \"@type\": \"@id\"\n"
  //            + "            },\n"
  //            + "            \"forecastOffice\": {\n"
  //            + "                \"@type\": \"@id\"\n"
  //            + "            },\n"
  //            + "            \"forecastGridData\": {\n"
  //            + "                \"@type\": \"@id\"\n"
  //            + "            },\n"
  //            + "            \"publicZone\": {\n"
  //            + "                \"@type\": \"@id\"\n"
  //            + "            },\n"
  //            + "            \"county\": {\n"
  //            + "                \"@type\": \"@id\"\n"
  //            + "            }\n"
  //            + "        }\n"
  //            + "    ],\n"
  //            + "    \"id\": \"https://api.weather.gov/points/34.0522,-118.2437\",\n"
  //            + "    \"type\": \"Feature\",\n"
  //            + "    \"geometry\": {\n"
  //            + "        \"type\": \"Point\",\n"
  //            + "        \"coordinates\": [\n"
  //            + "            -118.2437,\n"
  //            + "            34.052199999999999\n"
  //            + "        ]\n"
  //            + "    },\n"
  //            + "    \"properties\": {\n"
  //            + "        \"@id\": \"https://api.weather.gov/points/34.0522,-118.2437\",\n"
  //            + "        \"@type\": \"wx:Point\",\n"
  //            + "        \"cwa\": \"LOX\",\n"
  //            + "        \"forecastOffice\": \"https://api.weather.gov/offices/LOX\",\n"
  //            + "        \"gridId\": \"LOX\",\n"
  //            + "        \"gridX\": 154,\n"
  //            + "        \"gridY\": 44,\n"
  //            + "        \"forecast\":
  // \"https://api.weather.gov/gridpoints/LOX/154,44/forecast\",\n"
  //            + "        \"forecastHourly\":
  // \"https://api.weather.gov/gridpoints/LOX/154,44/forecast/hourly\",\n"
  //            + "        \"forecastGridData\":
  // \"https://api.weather.gov/gridpoints/LOX/154,44\",\n"
  //            + "        \"observationStations\":
  // \"https://api.weather.gov/gridpoints/LOX/154,44/stations\",\n"
  //            + "        \"relativeLocation\": {\n"
  //            + "            \"type\": \"Feature\",\n"
  //            + "            \"geometry\": {\n"
  //            + "                \"type\": \"Point\",\n"
  //            + "                \"coordinates\": [\n"
  //            + "                    -118.210869,\n"
  //            + "                    34.001123\n"
  //            + "                ]\n"
  //            + "            },\n"
  //            + "            \"properties\": {\n"
  //            + "                \"city\": \"Vernon\",\n"
  //            + "                \"state\": \"CA\",\n"
  //            + "                \"distance\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:m\",\n"
  //            + "                    \"value\": 6435.1328292857997\n"
  //            + "                },\n"
  //            + "                \"bearing\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:degree_(angle)\",\n"
  //            + "                    \"value\": 331\n"
  //            + "                }\n"
  //            + "            }\n"
  //            + "        },\n"
  //            + "        \"forecastZone\": \"https://api.weather.gov/zones/forecast/CAZ365\",\n"
  //            + "        \"county\": \"https://api.weather.gov/zones/county/CAC037\",\n"
  //            + "        \"fireWeatherZone\": \"https://api.weather.gov/zones/fire/CAZ365\",\n"
  //            + "        \"timeZone\": \"America/Los_Angeles\",\n"
  //            + "        \"radarStation\": \"KSOX\"\n"
  //            + "    }\n"
  //            + "}";
  //    Buffer mockedBuffer = new Buffer().readFrom(new
  // ByteArrayInputStream(mockedString.getBytes()));
  //    Location location = MoshiUtil.deserializeLocation(mockedBuffer);
  //    assertTrue(location.type().equals("Feature"));
  //    assertTrue(
  //        location
  //            .property()
  //            .hourlyForecastURL()
  //            .equals("https://api.weather.gov/gridpoints/LOX/154,44/forecast/hourly"));
  //  }
  //
  //  /**
  //   * Tests deserializing a forecast JSON buffer.
  //   *
  //   * @throws IOException
  //   * @throws DeserializeException
  //   */
  //  @Test
  //  public void testForecastDeserialize() throws IOException, DeserializeException {
  //    String mockedString =
  //        "{\n"
  //            + "    \"type\": \"Feature\",\n"
  //            + "    \"properties\": {\n"
  //            + "        \"updated\": \"2023-03-04T11:12:58+00:00\",\n"
  //            + "        \"units\": \"us\",\n"
  //            + "        \"forecastGenerator\": \"HourlyForecastGenerator\",\n"
  //            + "        \"generatedAt\": \"2023-03-04T15:22:25+00:00\",\n"
  //            + "        \"updateTime\": \"2023-03-04T11:12:58+00:00\",\n"
  //            + "        \"validTimes\": \"2023-03-04T05:00:00+00:00/P7DT20H\",\n"
  //            + "        \"elevation\": {\n"
  //            + "            \"unitCode\": \"wmoUnit:m\",\n"
  //            + "            \"value\": 114.9096\n"
  //            + "        },\n"
  //            + "        \"periods\": [\n"
  //            + "            {\n"
  //            + "                \"number\": 1,\n"
  //            + "                \"name\": \"\",\n"
  //            + "                \"startTime\": \"2023-03-04T07:00:00-08:00\",\n"
  //            + "                \"endTime\": \"2023-03-04T08:00:00-08:00\",\n"
  //            + "                \"isDaytime\": true,\n"
  //            + "                \"temperature\": 45,\n"
  //            + "                \"temperatureUnit\": \"F\",\n"
  //            + "                \"temperatureTrend\": null,\n"
  //            + "                \"probabilityOfPrecipitation\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 1\n"
  //            + "                },\n"
  //            + "                \"dewpoint\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:degC\",\n"
  //            + "                    \"value\": 6.666666666666667\n"
  //            + "                },\n"
  //            + "                \"relativeHumidity\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 96\n"
  //            + "                },\n"
  //            + "                \"windSpeed\": \"5 mph\",\n"
  //            + "                \"windDirection\": \"ESE\",\n"
  //            + "                \"icon\":
  // \"https://api.weather.gov/icons/land/day/fog,1?size=small\",\n"
  //            + "                \"shortForecast\": \"Patchy Fog\",\n"
  //            + "                \"detailedForecast\": \"\"\n"
  //            + "            },\n"
  //            + "            {\n"
  //            + "                \"number\": 2,\n"
  //            + "                \"name\": \"\",\n"
  //            + "                \"startTime\": \"2023-03-04T08:00:00-08:00\",\n"
  //            + "                \"endTime\": \"2023-03-04T09:00:00-08:00\",\n"
  //            + "                \"isDaytime\": true,\n"
  //            + "                \"temperature\": 48,\n"
  //            + "                \"temperatureUnit\": \"F\",\n"
  //            + "                \"temperatureTrend\": null,\n"
  //            + "                \"probabilityOfPrecipitation\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 1\n"
  //            + "                },\n"
  //            + "                \"dewpoint\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:degC\",\n"
  //            + "                    \"value\": 6.666666666666667\n"
  //            + "                },\n"
  //            + "                \"relativeHumidity\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 86\n"
  //            + "                },\n"
  //            + "                \"windSpeed\": \"5 mph\",\n"
  //            + "                \"windDirection\": \"ESE\",\n"
  //            + "                \"icon\":
  // \"https://api.weather.gov/icons/land/day/fog,1?size=small\",\n"
  //            + "                \"shortForecast\": \"Patchy Fog\",\n"
  //            + "                \"detailedForecast\": \"\"\n"
  //            + "            },\n"
  //            + "            {\n"
  //            + "                \"number\": 3,\n"
  //            + "                \"name\": \"\",\n"
  //            + "                \"startTime\": \"2023-03-04T09:00:00-08:00\",\n"
  //            + "                \"endTime\": \"2023-03-04T10:00:00-08:00\",\n"
  //            + "                \"isDaytime\": true,\n"
  //            + "                \"temperature\": 51,\n"
  //            + "                \"temperatureUnit\": \"F\",\n"
  //            + "                \"temperatureTrend\": null,\n"
  //            + "                \"probabilityOfPrecipitation\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 1\n"
  //            + "                },\n"
  //            + "                \"dewpoint\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:degC\",\n"
  //            + "                    \"value\": 6.666666666666667\n"
  //            + "                },\n"
  //            + "                \"relativeHumidity\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 77\n"
  //            + "                },\n"
  //            + "                \"windSpeed\": \"5 mph\",\n"
  //            + "                \"windDirection\": \"ESE\",\n"
  //            + "                \"icon\":
  // \"https://api.weather.gov/icons/land/day/fog,1?size=small\",\n"
  //            + "                \"shortForecast\": \"Patchy Fog\",\n"
  //            + "                \"detailedForecast\": \"\"\n"
  //            + "            },\n"
  //            + "            {\n"
  //            + "                \"number\": 4,\n"
  //            + "                \"name\": \"\",\n"
  //            + "                \"startTime\": \"2023-03-04T10:00:00-08:00\",\n"
  //            + "                \"endTime\": \"2023-03-04T11:00:00-08:00\",\n"
  //            + "                \"isDaytime\": true,\n"
  //            + "                \"temperature\": 52,\n"
  //            + "                \"temperatureUnit\": \"F\",\n"
  //            + "                \"temperatureTrend\": null,\n"
  //            + "                \"probabilityOfPrecipitation\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 16\n"
  //            + "                },\n"
  //            + "                \"dewpoint\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:degC\",\n"
  //            + "                    \"value\": 6.666666666666667\n"
  //            + "                },\n"
  //            + "                \"relativeHumidity\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 74\n"
  //            + "                },\n"
  //            + "                \"windSpeed\": \"10 mph\",\n"
  //            + "                \"windDirection\": \"SSE\",\n"
  //            + "                \"icon\":
  // \"https://api.weather.gov/icons/land/day/rain,16?size=small\",\n"
  //            + "                \"shortForecast\": \"Slight Chance Light Rain\",\n"
  //            + "                \"detailedForecast\": \"\"\n"
  //            + "            },\n"
  //            + "            {\n"
  //            + "                \"number\": 5,\n"
  //            + "                \"name\": \"\",\n"
  //            + "                \"startTime\": \"2023-03-04T11:00:00-08:00\",\n"
  //            + "                \"endTime\": \"2023-03-04T12:00:00-08:00\",\n"
  //            + "                \"isDaytime\": true,\n"
  //            + "                \"temperature\": 53,\n"
  //            + "                \"temperatureUnit\": \"F\",\n"
  //            + "                \"temperatureTrend\": null,\n"
  //            + "                \"probabilityOfPrecipitation\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 16\n"
  //            + "                },\n"
  //            + "                \"dewpoint\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:degC\",\n"
  //            + "                    \"value\": 6.666666666666667\n"
  //            + "                },\n"
  //            + "                \"relativeHumidity\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 71\n"
  //            + "                },\n"
  //            + "                \"windSpeed\": \"10 mph\",\n"
  //            + "                \"windDirection\": \"SSE\",\n"
  //            + "                \"icon\":
  // \"https://api.weather.gov/icons/land/day/rain,16?size=small\",\n"
  //            + "                \"shortForecast\": \"Slight Chance Light Rain\",\n"
  //            + "                \"detailedForecast\": \"\"\n"
  //            + "            },\n"
  //            + "            {\n"
  //            + "                \"number\": 6,\n"
  //            + "                \"name\": \"\",\n"
  //            + "                \"startTime\": \"2023-03-04T12:00:00-08:00\",\n"
  //            + "                \"endTime\": \"2023-03-04T13:00:00-08:00\",\n"
  //            + "                \"isDaytime\": true,\n"
  //            + "                \"temperature\": 54,\n"
  //            + "                \"temperatureUnit\": \"F\",\n"
  //            + "                \"temperatureTrend\": null,\n"
  //            + "                \"probabilityOfPrecipitation\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 16\n"
  //            + "                },\n"
  //            + "                \"dewpoint\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:degC\",\n"
  //            + "                    \"value\": 6.666666666666667\n"
  //            + "                },\n"
  //            + "                \"relativeHumidity\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 69\n"
  //            + "                },\n"
  //            + "                \"windSpeed\": \"10 mph\",\n"
  //            + "                \"windDirection\": \"SSE\",\n"
  //            + "                \"icon\":
  // \"https://api.weather.gov/icons/land/day/rain,16?size=small\",\n"
  //            + "                \"shortForecast\": \"Slight Chance Light Rain\",\n"
  //            + "                \"detailedForecast\": \"\"\n"
  //            + "            },\n"
  //            + "            {\n"
  //            + "                \"number\": 7,\n"
  //            + "                \"name\": \"\",\n"
  //            + "                \"startTime\": \"2023-03-04T13:00:00-08:00\",\n"
  //            + "                \"endTime\": \"2023-03-04T14:00:00-08:00\",\n"
  //            + "                \"isDaytime\": true,\n"
  //            + "                \"temperature\": 55,\n"
  //            + "                \"temperatureUnit\": \"F\",\n"
  //            + "                \"temperatureTrend\": null,\n"
  //            + "                \"probabilityOfPrecipitation\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 16\n"
  //            + "                },\n"
  //            + "                \"dewpoint\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:degC\",\n"
  //            + "                    \"value\": 6.666666666666667\n"
  //            + "                },\n"
  //            + "                \"relativeHumidity\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 66\n"
  //            + "                },\n"
  //            + "                \"windSpeed\": \"5 mph\",\n"
  //            + "                \"windDirection\": \"SSE\",\n"
  //            + "                \"icon\":
  // \"https://api.weather.gov/icons/land/day/rain,16?size=small\",\n"
  //            + "                \"shortForecast\": \"Slight Chance Light Rain\",\n"
  //            + "                \"detailedForecast\": \"\"\n"
  //            + "            },\n"
  //            + "            {\n"
  //            + "                \"number\": 8,\n"
  //            + "                \"name\": \"\",\n"
  //            + "                \"startTime\": \"2023-03-04T14:00:00-08:00\",\n"
  //            + "                \"endTime\": \"2023-03-04T15:00:00-08:00\",\n"
  //            + "                \"isDaytime\": true,\n"
  //            + "                \"temperature\": 55,\n"
  //            + "                \"temperatureUnit\": \"F\",\n"
  //            + "                \"temperatureTrend\": null,\n"
  //            + "                \"probabilityOfPrecipitation\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 16\n"
  //            + "                },\n"
  //            + "                \"dewpoint\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:degC\",\n"
  //            + "                    \"value\": 6.666666666666667\n"
  //            + "                },\n"
  //            + "                \"relativeHumidity\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 66\n"
  //            + "                },\n"
  //            + "                \"windSpeed\": \"5 mph\",\n"
  //            + "                \"windDirection\": \"SSE\",\n"
  //            + "                \"icon\":
  // \"https://api.weather.gov/icons/land/day/rain,16?size=small\",\n"
  //            + "                \"shortForecast\": \"Slight Chance Light Rain\",\n"
  //            + "                \"detailedForecast\": \"\"\n"
  //            + "            },\n"
  //            + "            {\n"
  //            + "                \"number\": 9,\n"
  //            + "                \"name\": \"\",\n"
  //            + "                \"startTime\": \"2023-03-04T15:00:00-08:00\",\n"
  //            + "                \"endTime\": \"2023-03-04T16:00:00-08:00\",\n"
  //            + "                \"isDaytime\": true,\n"
  //            + "                \"temperature\": 54,\n"
  //            + "                \"temperatureUnit\": \"F\",\n"
  //            + "                \"temperatureTrend\": null,\n"
  //            + "                \"probabilityOfPrecipitation\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 16\n"
  //            + "                },\n"
  //            + "                \"dewpoint\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:degC\",\n"
  //            + "                    \"value\": 6.666666666666667\n"
  //            + "                },\n"
  //            + "                \"relativeHumidity\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 69\n"
  //            + "                },\n"
  //            + "                \"windSpeed\": \"5 mph\",\n"
  //            + "                \"windDirection\": \"SSE\",\n"
  //            + "                \"icon\":
  // \"https://api.weather.gov/icons/land/day/rain,16?size=small\",\n"
  //            + "                \"shortForecast\": \"Slight Chance Light Rain\",\n"
  //            + "                \"detailedForecast\": \"\"\n"
  //            + "            },\n"
  //            + "            {\n"
  //            + "                \"number\": 10,\n"
  //            + "                \"name\": \"\",\n"
  //            + "                \"startTime\": \"2023-03-04T16:00:00-08:00\",\n"
  //            + "                \"endTime\": \"2023-03-04T17:00:00-08:00\",\n"
  //            + "                \"isDaytime\": true,\n"
  //            + "                \"temperature\": 53,\n"
  //            + "                \"temperatureUnit\": \"F\",\n"
  //            + "                \"temperatureTrend\": null,\n"
  //            + "                \"probabilityOfPrecipitation\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 16\n"
  //            + "                },\n"
  //            + "                \"dewpoint\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:degC\",\n"
  //            + "                    \"value\": 6.666666666666667\n"
  //            + "                },\n"
  //            + "                \"relativeHumidity\": {\n"
  //            + "                    \"unitCode\": \"wmoUnit:percent\",\n"
  //            + "                    \"value\": 71\n"
  //            + "                },\n"
  //            + "                \"windSpeed\": \"5 mph\",\n"
  //            + "                \"windDirection\": \"S\",\n"
  //            + "                \"icon\":
  // \"https://api.weather.gov/icons/land/day/rain,16?size=small\",\n"
  //            + "                \"shortForecast\": \"Slight Chance Light Rain\",\n"
  //            + "                \"detailedForecast\": \"\"\n"
  //            + "            }\n"
  //            + "        ]\n"
  //            + "    }\n"
  //            + "}";
  //    Buffer mockedBuffer = new Buffer().readFrom(new
  // ByteArrayInputStream(mockedString.getBytes()));
  //    HourlyForecast forecast = MoshiUtil.deserializeForecast(mockedBuffer);
  //    assertTrue(forecast.type().equals("Feature"));
  //    assertTrue(forecast.property().updated().equals("2023-03-04T11:12:58+00:00"));
  //    assertTrue(forecast.property().periods().get(0).temperature().equals("45"));
  //    assertTrue(forecast.property().periods().get(3).temperature().equals("52"));
  //    assertTrue(forecast.property().periods().size() == 10);
  //  }
  //
  //  /**
  //   * Tests that the DeserializeException is thrown if an error occurs while deserializing.
  //   *
  //   * @throws IOException
  //   */
  //  @Test
  //  public void testThrowsDeserializeException() throws IOException {
  //    String mockedString = "{bleep bloop nonsensical json}";
  //    Buffer mockedBuffer = new Buffer().readFrom(new
  // ByteArrayInputStream(mockedString.getBytes()));
  //
  //    assertThrows(
  //        DeserializeException.class,
  //        () -> {
  //          MoshiUtil.deserializeLocation(mockedBuffer);
  //        });
  //
  //    assertThrows(
  //        DeserializeException.class,
  //        () -> {
  //          MoshiUtil.deserializeForecast(mockedBuffer);
  //        });
  //  }
}
