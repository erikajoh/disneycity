package housing.test;
import java.util.HashMap;
import java.util.Map;

import simcity.PersonAgent;
import housing.Housing;
import housing.ResidentAgent;
import housing.gui.HousingAnimationPanel;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the ResidentAgent's basic interactions.
 *
 * @author Erika Johnson
 */
public class HousingTest extends TestCase
{
	HashMap<String,Integer> dummyList = new HashMap<String,Integer>();
	HousingAnimationPanel hPanel = new HousingAnimationPanel();
	Housing housing = new Housing(hPanel, "housing");
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();
	}
	
	public void testHousingEnteringHouse()
	{
		//setUp() runs first before this test!
		
		PersonAgent rp = new PersonAgent("person", housing, 100.0, "", false, "", null, 'W');
		ResidentAgent ra = new ResidentAgent("renter", "house", 0);
		ra.setHousing(housing);

		housing.msgIAmHome(rp, dummyList);
		assertTrue("Housing should have logged \"Home\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Home"));
		
		housing.msgEntered(ra);
		assertTrue("Housing should have logged \"Entered\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Entered"));		
		
	}
	public void testHousingRentDueAtHome()
	{
		//setUp() runs first before this test!

		PersonAgent rp = new PersonAgent("person", housing, 100.0, "", false, "", null, 'W');
		ResidentAgent ra = new ResidentAgent("renter", "house", 0);
		ra.setHousing(housing);

		housing.msgRentDue();
		assertTrue("Housing should have logged \"Rent is due\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Rent is due"));
		
		housing.msgHereIsRent(rp, 5);
		assertTrue("Housing should have logged \"Here is rent\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Here is rent"));
	
	}
	public void testHousingEatAtHome()
	{
		//setUp() runs first before this test!
		
		PersonAgent rp = new PersonAgent("person", housing, 100.0, "", false, "", null, 'W');
		ResidentAgent ra = new ResidentAgent("renter", "house", 0);
		ra.setHousing(housing);

		housing.msgPrepareToCookAtHome(rp, "American");
		assertTrue("Housing should have logged \"Cooking\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Cooking"));
		
		housing.msgFoodDone(ra, true);
		assertTrue("Housing should have logged \"Food done\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Food done"));

	}
	public void testHousingNoFoodAtHome()
	{
		//setUp() runs first before this test!
		
		PersonAgent rp = new PersonAgent("person", housing, 100.0, "", false, "", null, 'W');
		ResidentAgent ra = new ResidentAgent("renter", "house", 0);
		ra.setHousing(housing);

		housing.msgPrepareToCookAtHome(rp, "American");
		assertTrue("Housing should have logged \"Cooking\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Cooking"));
		
		housing.msgFoodDone(ra, false);
		assertTrue("Housing should have logged \"Food done\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Food done"));

	}
	
	public void testHousingDoMaintenanceAtHome()
	{
		//setUp() runs first before this test!
		
		PersonAgent rp = new PersonAgent("person", housing, 100.0, "", false, "", null, 'W');
		ResidentAgent ra = new ResidentAgent("renter", "house", 0);
		ra.setHousing(housing);

		housing.msgDoMaintenance(rp);
		assertTrue("Housing should have logged \"Do maintenance\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Do maintenance"));
		
		housing.msgFinishedMaintenance(ra);
		assertTrue("Housing should have logged \"Finished maintenance\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Finished maintenance"));
		

	}
	public void testHousingGoToBedAtHome()
	{
		//setUp() runs first before this test!
		
		PersonAgent rp = new PersonAgent("person", housing, 100.0, "", false, "", null, 'W');
		ResidentAgent ra = new ResidentAgent("renter", "house", 0);
		ra.setHousing(housing);

		housing.msgGoToBed(rp);
		assertTrue("Housing should have logged \"Go to bed\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Go to bed"));

	}
	public void testHousingLeavingHouse()
	{
		//setUp() runs first before this test!
		
		PersonAgent rp = new PersonAgent("person", housing, 100.0, "", false, "", null, 'W');
		ResidentAgent ra = new ResidentAgent("renter", "house", 0);
		ra.setHousing(housing);

		housing.msgIAmLeaving(rp);
		assertTrue("Housing should have logged \"Leaving\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Leaving"));

		housing.msgLeft(ra);
		assertTrue("Housing should have logged \"Left\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Left"));
	
	}
	
	public void testHousingEnteringApt()
	{
		//setUp() runs first before this test!
		
		PersonAgent rp = new PersonAgent("person", housing, 100.0, "", false, "", null, 'W');
		ResidentAgent ra = new ResidentAgent("renter", "apt", 0);
		ra.setHousing(housing);

		housing.msgIAmHome(rp, dummyList);
		assertTrue("Housing should have logged \"Home\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Home"));
		
		housing.msgEntered(ra);
		assertTrue("Housing should have logged \"Entered\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Entered"));		
		

	}
	public void testHousingEatAtApt()
	{
		//setUp() runs first before this test!
		
		PersonAgent rp = new PersonAgent("person", housing, 100.0, "", false, "", null, 'W');
		ResidentAgent ra = new ResidentAgent("renter", "apt", 0);
		ra.setHousing(housing);

		housing.msgPrepareToCookAtHome(rp, "American");
		assertTrue("Housing should have logged \"Cooking\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Cooking"));
		
		housing.msgFoodDone(ra, true);
		assertTrue("Housing should have logged \"Food done\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Food done"));


	}
	public void testHousingNoFoodAtApt()
	{
		//setUp() runs first before this test!
		
		PersonAgent rp = new PersonAgent("person", housing, 100.0, "", false, "", null, 'W');
		ResidentAgent ra = new ResidentAgent("renter", "apt", 0);
		ra.setHousing(housing);

		housing.msgPrepareToCookAtHome(rp, "American");
		assertTrue("Housing should have logged \"Cooking\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Cooking"));
		
		housing.msgFoodDone(ra, false);
		assertTrue("Housing should have logged \"Food done\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Food done"));


	}
	public void testHousingDoMaintenanceAtApt()
	{
		//setUp() runs first before this test!
		PersonAgent rp = new PersonAgent("person", housing, 100.0, "", false, "", null, 'W');
		ResidentAgent ra = new ResidentAgent("renter", "apt", 0);
		ra.setHousing(housing);

		housing.msgDoMaintenance(rp);
		assertTrue("Housing should have logged \"Do maintenance\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Do maintenance"));
		
		housing.msgFinishedMaintenance(ra);
		assertTrue("Housing should have logged \"Finished maintenance\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Finished maintenance"));
		

	}
	public void testHousingGoToBedAtApt()
	{
		//setUp() runs first before this test!
		
		PersonAgent rp = new PersonAgent("person", housing, 100.0, "", false, "", null, 'W');
		ResidentAgent ra = new ResidentAgent("renter", "apt", 0);
		ra.setHousing(housing);

		housing.msgGoToBed(rp);
		assertTrue("Housing should have logged \"Go to bed\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Go to bed"));


	}
	public void testHousingLeavingApt()
	{
		//setUp() runs first before this test!
		
		PersonAgent rp = new PersonAgent("person", housing, 100.0, "", false, "", null, 'W');
		ResidentAgent ra = new ResidentAgent("renter", "apt", 0);
		ra.setHousing(housing);
		
		housing.msgIAmLeaving(rp);
		assertTrue("Housing should have logged \"Leaving\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Leaving"));

		housing.msgLeft(ra);
		assertTrue("Housing should have logged \"Left\" but didn't. His log reads instead: " + housing.log.getLastLoggedEvent().toString(), housing.log.containsString("Left"));

	}
	
}