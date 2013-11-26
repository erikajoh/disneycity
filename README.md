SimCity201 Project Team 01: Edgar in Disneyland
===============================================

All sprites are the property of their respective owners.
Thanks to the [Spriter's Resource](http://www.spriters-resource.com/) for the sprite sheets.

##Team Members
Douglass Chen (Team Lead) <br>
Daron Lee <br>
Dylan Eirinberg <br>
Erika Johnson <br>
Kelsey Rose <br>

##Description

+ Douglass:
	+  
+ Daron (Transportation and City Panel): 
	+ City Panel is clickable on the locations to change the building window.
	+ Transportation spawns transportation agents when receiving messages from people to go somewhere.
	+ A* is implemented per type of transportation agent so that each only moves on certain tiles.
	+ Roads are constrained to where cars may go. This simulates real lanes of traffic. U-turns are allowed in some places.
	+ The bus uses a queue that is updated to determine its route. This route is set at the beginning and cannot be changed. Nor will the bus change course for another vehicle.
	+ The truck is a flying Pelipper. As such, the truck may fly over any time but still responds to collisions with other agents.
	+ The truck goes to an idle position next to the market.
	+ Everything is animated. People have an animation to show that they are entering a building. Cars sadly do not.
+ Dylan: 
	+ 
+ Erika: 
	+ 
+ Kelsey: 
	+ 

##Instructions
+ To import into Eclipse: (1) clone the repository, (2) File > Import > Existing Projects into Workspace and then navigate to the location of the cloned repository and import it, (3) right-click the root folder and select Build Path > Configure Build Path > Add External JARs... and then navigate to the Java file on your computer and add it, (4) import JUnit by hovering over errors in any of the unit test files.
+ Locate the SimCityGui.java file in the simcity.gui package, and execute it as a Java Application in Eclipse by right-clicking the file and selecting Run As > Java Application.

###Scenarios
+ Currently one person ("Narwhal Prime") spawns in his own house...

###Issues/problems
+ Currently we only have one restaurant that has full job functionality. Over time we believe we can do the same for the other restaurants.

####Transportation
+ A* may cause agents to get caught in the awkward sidewalk dance of going back and forth without passing each other.