# Find the Nearest Carpark App
This is a CZ2006 Software Engineering Course Project.
Target User: Car drivers
## Functional Requirements:
1. This application shall be able to obtain the current location of the user through GPS/geolocation and label the location on a map of Singapore.
* 1.1  This application shall have an interactive map UI. Interaction includes swipe to move the map, and pitch to zoom in and zoom out.
* 1.2  This application shall be able to allow the user to choose a destination location on a map of Singapore either by clicking on the map or typing in a location name.

2. This application shall be able to show the user a list of the nearest car park locations (within a certain distance) with free parking spaces on a map of Singapore with respect to:
* 2.1   The known current location obtained by GPS, or
* 2.2   The user’s selected destination location.
* 2.3   This application should be able to show all the car parks that meet the user’s location or selected location.
* 2.4   The application shall zoom into the selected carpark locations, with these locations labeled with their respective car park numbers.
* 2.5   This application shall allow the user to choose the distance range for showing the nearest car park locations.
* 2.6   This application should be able to show all car parks available on the map.

3. This application must allow the user to query any carpark locations in Singapore by keying in carpark number or address.
* 3.1   This application will display detailed information of a carpark location after the user clicks on one of the car park locations in the query result or one of the car park locations shown on a map,
* 3.2   The detailed information of a car park location includes:
  + 3.2.1       Address
  + 3.2.2       Car park type
  + 3.2.3       Type of parking system
  + 3.2.4       Whether or not short-term parking is supported
  + 3.2.5       Free parking time
  + 3.2.6       Whether or not night parking is supported
  + 3.2.7       Number of car park decks
  + 3.2.8       Gantry height
  + 3.2.9       Car park basement  
  + 3.2.10   Rates  
  + 3.2.11   Nearby car park locations.
* 3.3   The application will let the user choose to display the carpark location on a map or choose the carpark location as the destination carpark location.
* 3.4   This application will allow the user to put a filter on the car park locations the user is trying to query.
  + 3.4.1       The filter on car park locations shall include car park type, type of parking system, whether short-term parking is supported, whether free parking is applicable now, whether night parking is supported.
* 3.5   The application shall allow the user to select a carpark to drive to
  + 3.5.1       After the user determines the destination carpark location, this application shall be able to show the directions from the known current direction (default: GPS) to the carpark location selected by the user on an interactive map that updates in real-time.
  + 3.5.2       This application shall be able to show the estimated time to drive to the selected carpark location.

4. This application shall be able to allow the user to store a set of directions to a specific car park location
* 4.1   The user can set the time in a day and days in a week to follow the set of directions.
* 4.2   The application shall also allow the user to set the number of minutes before the specified time to push a notification.
* 4.3   This application shall push a notification to the user when the current time is the user-defined time to push a notification, which contains the real-time carpark availability information and estimated driving time.
* 4.4   This application shall be able to allow the user to comment on the car park that they have visited before and give it a rating based on the number of stars.
* 4.5   The application should have a “Favorite” feature to allow users to store their favorite car parks and get quick access to the detailed information of those car parks when needed.

5. This application shall query the Carpark Availability API (https://data.gov.sg/dataset/carpark-availability)  and cross-reference it with the HDB Carpark Information API (queried every 24 hours) (https://data.gov.sg/dataset/hdb-carpark-information?view_id=398e65ae-e2cb-4312-8651-6e65d6f19ed1&resource_id=139a3035-e624-4f56-b63f-89ae28d4ae4c).

6. This application shall query APIs provided by Google Maps Platform (https://developers.google.com/maps/documentation/).



## Non-Functional Requirements:
1. This application shall run on Android versions 6.0 and above.
2. This application shall process and show the first available carpark within 5 seconds of the user requesting it.
3. This application shall take no more than 2 minutes for 80% of first-time users to successfully find their nearest available carpark.

