SimCity201 Project Team 01: Edgar in Disneyland
===============================================

All sprites are the property of their respective owners.
Thanks to the [Spriter's Resource](http://www.spriters-resource.com/) for the sprite sheets.
Map images taken from [the map of Disneyland](http://disney.wikia.com/wiki/File:Disneyland_map_2011.jpg).

##Team Members
Douglass Chen (Team Lead) <br>
Daron Lee <br>
Dylan Eirinberg <br>
Erika Johnson <br>
Kelsey Rose <br>

##Map legend (useful for tracking locations)
+ 1 = Market (Mickey's Market)
+ 2, 4, 9, 12 = Restaurants (in this version 9 and 14 demonstrate key functionality)
+ 3, 6, 7, 10, 11, 13 = Apartments
+ 5 = House
+ 8 = Bank

##Description

+ Douglass (Person):
	+ The entirety of the PersonAgent design - data, scheduler, messages, actions
	+ Since PersonAgent is the middleman of all the other buildings in the city, the PersonAgent is responsible for integrating all of the markets/restaurants/etc. together
	+ A Person tracks money on hand, hungry or not, whether he has jobs or not, preferred transportation method, etc.
	+ A Person has pointers to all other locations in the city and avoids shared data via wrapper classes (e.g. MyBank objects for each Bank)
	+ Our design philosophy is to make the PersonAgent class an abstraction that tracks individual data for each person in the city and holds the entire city system together. A Person does not "enter" a location (including Transportation) but rather spawns a copy of himself with the necessary data that serves to link the PersonAgent with any other class in the city. This serves to decouple our design and prevent sharing of data which would break our Agent methodology.
+ Daron (Transportation and City Panel): 
	+ The locations in the City Panel (top-left panel) are all clickable on the locations to change the building window.
	+ Transportation spawns transportation agents when receiving messages from people to go somewhere.
	+ A* is implemented for each type of transportation agent so that each only moves on certain tiles and prevent (almost all) collisions.
	+ Roads are constrained to where cars may go. This simulates real lanes of traffic. U-turns are allowed in some places.
	+ The bus is our own personal Chocobus! (The yellow flightless bird on the roads)
	+ The bus uses a queue that is updated to determine its route. This route is set at the beginning and cannot be changed. Nor will the bus change course for another vehicle.
	+ If a person wants to get off at the same stop he gets on, he just walks to his destination.
	+ The truck is a flying Pelipper (white bird with huge beak). As such, the truck may fly over any tile but still responds to collisions with other agents.
	+ The truck goes to an idle position next to the market.
	+ Everything is animated. People have an animation to show that they are entering a building. Cars sadly do not.
+ Dylan (Bank): 
	+ 
+ Erika (Markets, Housing): 
	+ 
+ Kelsey (Restaurants, Front-end design): 
	+ 

##Instructions
+ IMPORTANT: Please use Eclipse to compile and run the project. It was brought to attention by one of the teaching assistants that using the Ant build file would cause problems since we files of the same name in different files.
+ To import into Eclipse: (1) clone the repository, (2) File > Import > Existing Projects into Workspace and then navigate to the location of the cloned repository and import it, (3) right-click the root folder and select Build Path > Configure Build Path > Add External JARs... and then navigate to the Java file (typically rt.jar) on your computer and add it, (4) import JUnit by hovering over errors in any of the unit test files.
+ Locate the SimCityGui.java file in the simcity.gui package, and execute it as a Java Application in Eclipse by right-clicking the file and selecting Run As > Java Application.

###How to use configuration file
+ simcity_config.txt, located in src/res
+ Though the configuration system is not fully optimized, it nonetheless allows great flexibility with setting the initial properties of the city.
	+ There are three sections ot the file, each of which is headed by an integer N on each section. All other lines are essentially different properties separated by vertical bar '|' characters.
	+ First section: N = number of people in the city
		+ PersonName|HousingName|StartMoney|FoodPreference|PreferEatingAtHomeAtStart|HousingRelationWithSpecifiedHousing|PreferredTransportationMethodAsSingleChar
	+ Second section: N = number of housing relatiosn in the city
		+ HousingName|Owner|Renter[|OptionalAdditionalRenter|...]
	+ Third section: N = 5 (# of restaurants) * P (# of people), for specifying jobs
		+ PersonName|RestaurantName|RelationWithRestaurant|WorkSession

###Scenarioes
+ Important: The basic time unit of the city is a "tick", or 1/8 of a second. In this version the day length is specified to at least 1200 ticks. Thus, if the simulation ever seems frozen, wait until the day ends. By then the people should be sleeping in their houses. The current # of ticks passed in the day should be displayed in console.
+ The simcity_config.txt file located in src/res is the configuration file to modify initial people. The 2nd and 3rd line are important: they initialize the people with various parameters that may be modified.

###Issues/problems
+ Currently we only have one restaurant (#14 in the city map) that has partial job functionality - more specifically, they can enter, stay, and get paid, but testing for whether they can serve customers and finish their tasks before leaving has not been implemented yet. Over time we believe we can not only make this restaurant fully functional but use this restaurant to help implement the features in the others.
	+ Furthermore, #14 does not get rid of WaiterAgent icons in the restaurant when the waiter finishes work and leaves.

####Transportation
+ A* may cause agents to get caught in the awkward sidewalk dance of going back and forth without passing each other.
+ Currently no cars are shown in this implementation, though there are bus and truck systems.
+ Bus currently drops off everyone riding on the same tile instantly.
