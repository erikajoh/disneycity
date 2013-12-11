SimCity201 Project Team 01: Just Push It
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
+ (1) Tiki Hut
+ (2) Haunted Mansion
+ (3) Pirate's Suite
+ (4) Space Mountain
+ (5) Cinderella Castle
+ (6) Rabbit Hole
+ (7) Mickey's Market
+ (8) Minnie's Market
+ (9) Rancho Del Zocalo
+ (10) Village Haus
+ (11) Carnation Cafe
+ (12) Blue Bayou
+ (13) Pizza Port
+ (14) Pirate Bank
+ (15) Main St Apartments #1
+ (16) Main St Apartments #6
+ (17) Main St Apartments #7
+ (18) Main St Apartments #11
+ (19) Main St Apartments #10
+ (20) Main St Apartments #4
+ (21) Main St Apartments #9
+ (22) Main St Apartments #8
+ (23) Main St Apartments #5
+ (24) Main St Apartments #3
+ (25) Main St Apartments #2
+ (26) Buccaneer Bank

##Description

+ **_Douglass (Person and Person Property Panel on the GUI):_**
	+ The entirety of the PersonAgent design - data, scheduler, messages, actions
	+ Since PersonAgent is the middleman of all the other buildings in the city, the PersonAgent is responsible for integrating all of the markets/restaurants/etc. together
	+ A Person tracks money on hand, hungry or not, whether he has jobs or not, preferred transportation method, etc.
	+ A Person has pointers to all other locations in the city and avoids shared data via wrapper classes (e.g. MyBank objects for each Bank)
	+ Our design philosophy is to make the PersonAgent class an abstraction that tracks individual data for each person in the city and holds the entire city system together. A Person does not "enter" a location (including Transportation) but rather spawns a copy of himself with the necessary data that serves to link the PersonAgent with any other class in the city. This serves to decouple our design and prevent sharing of data which would break our Agent methodology.
	+ Created central config file (for all people) and person properties files (for each individual person) that are read in at run time.
	+ Added Person Properties Panel on the GUI to add people to the simulation after the program has started.

	
+ **_Daron (Transportation and City Panel):_** 
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
	+ Unit tests for the animation system and the transportation controller are located in src/transportation/Tests/
	+ Redesigned city view for new buildings
	+ Created dynamic camera panning feature for city view

	
+ **_Dylan (Bank and Workplace Property Panel on the GUI):_** 
	+ Created the bank which includes BankCustomerAgents (spawned PersonAgents), TellerAgents and one ManagerAgent
	+ Also designed gui/characters/sprites/images for bank
	+ Redesigned GUI to include new panels and miscellaneous gui bug fixes
	+ Created and linked Workplace Properties panel (lower right side)
	+ Set base for switching job functions
	+ **The bank should function properly with the following assumptions:**
		+ A person will only make one action (new account, withdraw or deposit) per trip to the bank
		+ A person will never say they deposited more money than they actually did
		+ Thieves only attempt to rob the bank once (when they enter the simulation). They can succeed or fail the robbery (fall and drop the money) but they will return as normal customers after. The success/fail decision is randomized but can be set to always true or false in the WPP GUI.
		+ A loan automatically is generated when a person withdraws more money in the account than they have. Each time they revisit the bank for the SAME account, their time to pay back the loan decreases. Customers can take out multiple loans, but their time to pay them back won't reset. If they fail to pay back the loan in the number of visits they won't be able to take out another loan. Additionally they're loan will grow by increasing interest. After the first day of missing a loan they will owe an additional 25 dollars, then an additional 50 for the second day and so on. There are steep fines for missing payment on loans.
	
	+ **Unit Tests:** I designed a complete BankCustomerTest and ManagerTest. TellerTest was too repetitive and didn't prove anything since BankCustomerTest passed all of its tests. See the intro of BankCustomerTest.java for more details.
	

+ **_Erika (Markets, Housing):_** 
	+ Designed and implemented the Housing and Market components, incl. related agents, animations, graphics and tests; took over the Market component when a team member dropped out of the class a few days into the project.
	+ Housing serves as the "default" location for the Person, so it is unique in that it passes the control back to the Person each time it completes a task. Thus, the Person has control over the ResidentAgent's actions by sending messages to the Housing class. Housing provides a choice between a "house" which accomodates one Person, or an "apartment" which accomodates up to four Persons.
	+ Markets can serve in-person customers, "virtual" (i.e. home delivery) customers, and restaurant delivery customers. When Markets receive an in-person order, the Worker brings the item(s) to the Cashier station to give to the Customer. When Markets receive "virtual"/delivery orders, the Worker brings the item(s) to the truck loading zone, and the Market's truck is sent to deliver the item(s).
	+ Debugged the Bank component and fixed issues such as multiple bank customers being spawned.
	+ Added to the Restaurant and Transportation components (CookAgent, CashierAgent, TransportationController, TruckAgent) for the Restaurant-Market and Transportation-Market interactions.
	+ Contributed to the main GUI zoom panel creation.
	+ Created logger in bottom of GUI

+ **_Kelsey (Restaurants, Front-end Lead):_** 
	+ Integrated all 5 restaurants using a base Restaurant interface. 
	+ Updated and upgraded animations in all restaurants 
	+ Upgraded Douglass's restaurant fully (Pizza Port) in order to accept and release workers for shifts (see issues for glitch), and wrote framework to upgrade all restaurants to accept and release workers for shifts
	+ Designed and created all background images for restaurants 
	+ Created SimCity Disneyland Image and panel (Daron made the image clickable and therefore established the grid of the SimCity image)
	+ Designed layout and wrote all main GUI code
	+ Updated my restaurant (restaurant_rancho, or Rancho Del Zocalo) to include Producer-Consumer model, Bank integration, and new Market integration 
	+ (Messages to bank work but messages from bank do not work, as the team member who wrote the bank could not integrate fully with SimCity in time)
	+ Integrated all restaurants with market
	+ Miscellaneous fixes for all restaurants
	+ UNIT TESTING: I unit tested the restaurant integration with market and bank with a cashier test, as the cashier is the main point of contact for the bank and interacts with the market. I removed test cases 5 and 6 in the cashier test from v2.2 and added 6 more unit tests to fully test the market and bank integration with restaurant.

##Instructions
+ IMPORTANT: Please use Eclipse to compile and run the project. It was brought to attention by one of the teaching assistants that using the Ant build file would cause problems since we files of the same name in different files.
+ To import into Eclipse: (1) clone the repository, (2) File > Import > Existing Projects into Workspace and then navigate to the location of the cloned repository and import it, (3) right-click the root folder and select Build Path > Configure Build Path > Add External JARs... and then navigate to the Java file (typically rt.jar) on your computer and add it, (4) import JUnit by hovering over errors in any of the unit test files.
+ Locate the SimCityGui.java file in the simcity.gui package, and execute it as a Java Application in Eclipse by right-clicking the file and selecting Run As > Java Application.

###How to use configuration file
// TODO: Configuration file system has been revamped to use a main configuration file and several properties files. Each properties-file contains information about one person except any housing-related properties, and the main configuration file is responsible for assigning people to their homes and who owns what housings.

###Scenarios
+ Important: The basic time unit of the city is a "tick", or 1/8 of a second. In this version the day length is specified to at least 1200 ticks. Thus, if the simulation ever seems frozen, wait until the day ends. By then the people should be sleeping in their houses. The current # of ticks passed in the day should be displayed in console.
+ Different scenarioes can be loaded from configuration files located in src/res. See the above section on more information.

###Issues/problems
+ Currently we only have one restaurant (#14 in the city map) that has near complete job functionality - more specifically, they can enter, stay, and get paid, and if all necessary workers are there, they should be able to run the restaurant as normal. The problem currently is that the PersonAgent is released, but their spawned WaiterAgent role is not released when they leave (an issue that we are confident that we can resolve quickly after this due date). Over time we can not only make this restaurant fully functional but finish implementing the same feature in the others.


####Transportation
+ A* may cause agents to get caught in the awkward sidewalk dance of going back and forth without passing each other.
+ Currently no cars are shown in this implementation, though there are bus and truck systems.
+ Bus currently drops off everyone riding on the same tile instantly.

