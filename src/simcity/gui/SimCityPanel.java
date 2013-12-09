package simcity.gui;

import restaurant_bayou.gui.RestaurantBayou;
import restaurant_cafe.gui.RestaurantCafe;
import restaurant_haus.gui.HausAnimationPanel;
import restaurant_haus.gui.RestaurantHaus;
import restaurant_pizza.gui.RestaurantPizza;
import restaurant_rancho.gui.RestaurantRancho;
import simcity.PersonAgent;
import simcity.Restaurant;
import simcity.gui.trace.AlertLog;
import simcity.gui.trace.AlertTag;
import simcity.interfaces.Housing_Douglass;
import simcity.interfaces.Transportation_Douglass;
import transportation.Transportation;
import transportation.TransportationPanel;
import housing.Housing;

import javax.swing.*;

import bank.gui.Bank;
import market.Market;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.Timer;

public class SimCityPanel extends JPanel implements ActionListener {
	
	// Timer variables
	Timer timer;
	Timer animationTimer;

	// GUI and building variables
	SimCityGui gui = null;

	RestaurantRancho restRancho;
	RestaurantPizza restPizza;
	RestaurantBayou restBayou;
	RestaurantCafe restCafe;
	RestaurantHaus restHaus;
	
	Housing hauntedMansion;
	Housing mainStApts1;
	Housing mainStApts2;
	Housing mainStApts3;
	Housing mainStApts4;
	Housing mainStApts5;
	Housing mainStApts6;
	
	Bank pirateBank;
	
	Market mickeysMarket;

	Transportation transportation;
	
	public final static int NEW_DAY_DELAY = 3000;	 
	public final static int NUM_RESTAURANTS = 5;
	public final static int NUM_MARKETS = 1;
	
	public final static String MAIN_CONFIG_FILE = "simcity_config_v2_main.txt";

	// lists to hold all the different buildings
	ArrayList<PersonAgent> people = new ArrayList<PersonAgent>();
	ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
	ArrayList<Housing> housings = new ArrayList<Housing>();
	ArrayList<Market> markets = new ArrayList<Market>();
	
	ArrayList<JPanel> animationPanelsList = new ArrayList<JPanel>();
	 
	public SimCityPanel(SimCityGui gui) {
		
		this.gui = gui;
		
		/* Scenario panel */
		JPanel selection = new JPanel();
		String[] scenarios = { "Scenario 1", "Scenario 2", "Scenario 3", "Scenario 5", "Scenario 6", "Scenario 7", "Scenario 10" };
		// Create the combo box, select item at index 0.
		JComboBox scenarioList = new JComboBox(scenarios);
		scenarioList.setSelectedIndex(0);
		scenarioList.addActionListener(this);
		selection.setLayout(new FlowLayout());
		selection.add(new JLabel("Choose a scenario:"));
		selection.add(scenarioList);
		add(selection);
		
		String foodPrefMexican = "Mexican";
		String foodPrefItalian = "Italian";
		String foodPrefSouthern = "Southern";
		String foodPrefAmerican = "American";
		String foodPrefGerman = "German";

		restRancho = gui.restRancho;
		restPizza = gui.restPizza;
		restBayou = gui.restBayou;
		restCafe = gui.restCafe;
		restHaus = gui.restHaus;
		restaurants.add(restRancho);
		restaurants.add(restPizza);
		restaurants.add(restBayou);
		restaurants.add(restCafe);
		restaurants.add(restHaus);
		
		hauntedMansion = gui.hauntedMansion;
		mainStApts1 = gui.mainStApts1;
		mainStApts2 = gui.mainStApts2;
		mainStApts3 = gui.mainStApts3;
		mainStApts4 = gui.mainStApts4;
		mainStApts5 = gui.mainStApts5;
		mainStApts6 = gui.mainStApts6;
		housings.add(hauntedMansion);
		housings.add(mainStApts1);
		housings.add(mainStApts2);
		housings.add(mainStApts3);
		housings.add(mainStApts4);
		housings.add(mainStApts5);
		housings.add(mainStApts6);
		
		pirateBank = gui.pirateBank;
		
		mickeysMarket = gui.mickeysMarket;
		markets.add(mickeysMarket);
		
		transportation = gui.cityAniPanel.getTransportation();
		
		animationPanelsList = gui.animationPanelsList;
		
		initializeFromConfigFile("simcity_config.txt");
		
	    setLayout(new GridLayout());
	
		/* timing */
	    newDay();
	}
	
	public void addPerson(String aName, String housingName, double startMoney, String foodPreference,
			boolean preferEatAtHome, char commute, String personality) {
		
		Housing h = mapStringToHousing(housingName);
		if(h.getNumResidents() >= 4 && h.isApartment() || h.getNumResidents() >= 1 && !h.isApartment())
			AlertLog.getInstance().logInfo(AlertTag.CITY, "CITY", "Failed to create person; that housing has no more empty spots!");
		else if(nameExists(aName))
			AlertLog.getInstance().logInfo(AlertTag.CITY, "CITY", "Failed to create person; person name already exists!");
		else {
			PersonAgent personToAdd = new PersonAgent( aName, h, startMoney, foodPreference, preferEatAtHome,
				"Renter", transportation, commute);
			h.addRenter(personToAdd);
			
			for(int restInd = 0; restInd < NUM_RESTAURANTS; restInd++) {
				Restaurant r = restaurants.get(restInd);
				personToAdd.addRestaurant(r, "Customer", 0);
			}
			
			for(int marketInd = 0; marketInd < NUM_MARKETS; marketInd++) {
				Market m = markets.get(marketInd);
				personToAdd.addMarket(m, "Customer", 0);
			}
			
			personToAdd.setPersonality(personality);
			people.add(personToAdd);
			personToAdd.startThread();
			personToAdd.msgWakeUp();
			AlertLog.getInstance().logInfo(AlertTag.CITY, "CITY", "Created person: " + aName);
		}
	}
	
	// Configuration file parsing
	// Upon instantiation, pass all pointers to all things (restaurants, markets, housings, banks) to the person as follows:
	public void initializeFromConfigFile(String fileName) {
		resetAllElements(); // TODO: are we using this?
		
		try {			
			// begin new parser
			// from CSCI 201 website
			// Step 1: get all the names of the people so that:
				// a) the person knows which housing he lives
				// b) we know which properties files to use
			URL mainFileURL = getClass().getResource("/res/" + MAIN_CONFIG_FILE);
			URI mainFileURI = mainFileURL.toURI(); 
			BufferedReader br = new BufferedReader(new FileReader(new File(mainFileURI)));
			int numPeople = Integer.parseInt(br.readLine());
			for(int i = 0; i < numPeople; i++) {
				StringTokenizer st = new StringTokenizer(br.readLine(), "|");
				String theName = st.nextToken();
				String theHousingOwned = st.nextToken();
				String theRelationToHousing = st.nextToken();
				
				Properties props = new Properties();
				try {
					// TODO: test more people later: this is hacked
					URL personFileURL = getClass().getResource("/res/person-"+theName+".properties");
					URI personFileURI = personFileURL.toURI();
				    FileInputStream in = new FileInputStream(new File(personFileURI));
				    props.load(in);
				    in.close();
				} catch(IOException e) {
				    e.printStackTrace();
				} catch(IllegalArgumentException iae) {
				    iae.printStackTrace();
				}
			        
		        // Step 2: here goes the construction of person
					// a) basic person properties from top half of properties file
					// b) parse each restaurant and market
				// NOTE: owner of a building who doesn't live in said building -> person doesn't know he is owner, essentially!
		        String st_Name				= props.getProperty("name");
		        String st_HousingName		= theHousingOwned;
				double st_Money				= Double.parseDouble(props.getProperty("money"));
				String st_FoodPref			= props.getProperty("foodPreference");
				boolean st_PreferAtHome		= Boolean.parseBoolean(props.getProperty("preferAtHome"));
				String st_HousingRelation	= theRelationToHousing;
				char st_Commute				= props.getProperty("preferredCommute").charAt(0);
				
				Housing st_Housing = mapStringToHousing(st_HousingName);
				PersonAgent personToAdd = new PersonAgent(
						st_Name, st_Housing, st_Money, st_FoodPref, st_PreferAtHome,
						st_HousingRelation, transportation, st_Commute);
				
				// parsing restaurants
				for(int restInd = 1; restInd <= NUM_RESTAURANTS; restInd++) {
					String restName = props.getProperty("rest" + restInd + "_name");
					String restRole = props.getProperty("rest" + restInd + "_role");
					int restShift = Integer.parseInt(props.getProperty("rest" + restInd + "_shift"));
					
					Restaurant r = mapStringToRestaurant(restName);
					if(r != null)
						personToAdd.addRestaurant(r, restRole, restShift); // key step
				}
				
				// parsing markets
				for(int marketInd = 1; marketInd <= NUM_MARKETS; marketInd++) {
					String marketName = props.getProperty("market" + marketInd + "_name");
					String marketRole = props.getProperty("market" + marketInd + "_role");
					int marketShift = Integer.parseInt(props.getProperty("market" + marketInd + "_shift"));
					
					Market m = mapStringToMarket(marketName);
					if(m != null)
						personToAdd.addMarket(m, marketRole, marketShift); // key step
				}
				
				people.add(personToAdd);
			}
				
			int numRecords = Integer.parseInt(br.readLine());
			for(int recordInd = 0; recordInd < numRecords; recordInd++) {
				StringTokenizer st = new StringTokenizer(br.readLine(), "|");
				String st_HousingName = st.nextToken();
				String st_OwnerName = st.nextToken();

				Housing st_Housing = mapStringToHousing(st_HousingName);
				PersonAgent st_Owner = mapStringToPerson(st_OwnerName);
				st_Housing.setOwner(st_Owner); // key step
				
				while(st.hasMoreTokens()) {
					String st_RenterName = st.nextToken();
					PersonAgent st_Renter = mapStringToPerson(st_RenterName);
					st_Housing.addRenter(st_Renter); // key step
				}
			}
			
			// Extra setup variables at the end of file
			String theDay = br.readLine();
			setDay(theDay);
			
			

			// Only one bank so that's added in directly
			for(int personInd = 0; personInd < people.size(); personInd++) {
				PersonAgent currPerson = people.get(personInd);
				currPerson.addBank(pirateBank, "Customer");				
				currPerson.startThread();
			}
			// end new parser
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// TODO reset all elements
	public void resetAllElements() {
		
	}
	
	public Housing mapStringToHousing(String houseName) {
		if(houseName.equals("Haunted Mansion")){
			return hauntedMansion;
		}
		else if(houseName.equals("Main St Apartments #1")){
			return mainStApts1;
		}
		else if(houseName.equals("Main St Apartments #2")){
			return mainStApts2;
		}
		else if(houseName.equals("Main St Apartments #3")){
			return mainStApts3;
		}
		else if(houseName.equals("Main St Apartments #4")){
			return mainStApts4;
		}
		else if(houseName.equals("Main St Apartments #5")){
			return mainStApts5;
		}
		else if(houseName.equals("Main St Apartments #6")){
			return mainStApts6;
		}
		return null;
	}
	
	public PersonAgent mapStringToPerson(String personName) {
		for(int i = 0; i < people.size(); i++)
			if(personName.equals(people.get(i).getName()))
				return people.get(i);
		return null;
	}
	
	public Restaurant mapStringToRestaurant(String restName) {
		for(int i = 0; i < restaurants.size(); i++)
			if(restName.equals(restaurants.get(i).getRestaurantName()))
				return restaurants.get(i);
		return null;
	}
	
	public Market mapStringToMarket(String marketName) {
		for(int i = 0; i < markets.size(); i++)
			if(marketName.equals(markets.get(i).getName()))
				return markets.get(i);
		return null;
	}
	
	public void newDay() {
		timer = new Timer();
	    timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				incrementNumTicks();
				handleTick();
			}
		}, 0, TICK_DELAY);
	}
	
	public void handleTick() {
		long currTicks = getNumTicks();
		if(currTicks % 20 == 0)
			System.out.println("Timer has ticked: # ticks = " + currTicks);
		
		// handle ticks for people
		for(int i = 0; i < people.size(); i++) {
			final PersonAgent person = people.get(i);
			
			// hunger signals
			// there are three day phases: morning, noon, and evening.
			// At noon and evening, the person would eat.
			// The exact delay between change in day phase and becoming hungry is randomized as demonstrated below.
			if(currTicks == NOON || currTicks == EVENING) {
				timer.schedule(new TimerTask() {
					public void run() {
						System.out.println("Setting person to be hungry");
						person.msgSetHungry();
					}
				}, (int)(Math.random() * EAT_DELAY_MAX * TICK_DELAY));
			}
			
			// body state signals: waking up and sleeping
			if(currTicks == START_OF_DAY) {
				person.msgWakeUp();
			}
			if(currTicks == NIGHT) {
				person.msgGoToSleep();
			}
			// person maintenance signal: maintain house if it's Friday morning
			if(currTicks == MORNING && getCurrentDay().equals("Friday")) {
				person.msgNeedMaintenance();
			}
			
			// job signals: there are two possible times when people start working
			if(currTicks == WORK_ONE_START) {
				person.msgGoToWork(1);
			}
			if(currTicks == WORK_TWO_START) {
				person.msgGoToWork(2);
			}
		}
		
		// handle ticks for housing
		for(int i = 0; i < housings.size(); i++) {
			Housing theHousing = housings.get(i);
			// rent is due signal: at the start of every Saturday
			// TODO whole rent system needs to be tested with actual PersonAgents
			if(currTicks == START_OF_DAY && getCurrentDay().equals("Saturday")) {
				AlertLog.getInstance().logInfo(AlertTag.CITY, "CITY", "Today is " + getCurrentDay() + ", rent is due!");
				theHousing.msgRentDue();
			}
		}
		
		// handle ticks for restaurants
		for(int i = 0; i < restaurants.size(); i++) {
			Restaurant theRestaurant = restaurants.get(i);
			// rent is due signal: at the start of every Saturday
			// TODO whole rent system needs to be tested with actual PersonAgents
			if(currTicks == WORK_ONE_END || currTicks == WORK_TWO_END) {
				AlertLog.getInstance().logInfo(AlertTag.CITY, "CITY", "Work Shift End");
				theRestaurant.msgEndOfShift();
			}
		}
	}
	
	/* all time-related variables and methods */
	// TODO: initialize these in main config file
	public enum DayOfTheWeek { Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday };
	public DayOfTheWeek currentDay = DayOfTheWeek.Thursday; // default start day
	public long numTicks = 0;
	
	/* Time intervals */
	private static final int TICK_DELAY = 125; // one tick = 1/8 second
	
	// these are start times for each of the day phases
	private static final long START_OF_DAY		= 1;
	private static final long MORNING			= START_OF_DAY		+ 40; //41
	private static final long WORK_ONE_START	= MORNING			+ 150;//191
	private static final long NOON				= WORK_ONE_START	+ 150;//341
	private static final long WORK_ONE_END		= NOON				+ 150;//491
	private static final long WORK_TWO_START	= WORK_ONE_END		+ 160;//651
	private static final long EVENING			= WORK_TWO_START	+ 150;//801
	private static final long WORK_TWO_END		= EVENING			+ 150;//951
	private static final long NIGHT				= WORK_TWO_END		+ 160;//1111
	private static final long END_OF_DAY		= NIGHT				+ 800;//1911
	// length of day 1911 = appx. 4 minutes
	
	// for setting random delay for eating
	private static final int EAT_DELAY_MAX = 25;
	
	public String getCurrentDay() {
		return currentDay.toString();
	}

	public void setNextDay() {
		switch(currentDay) {
			case Sunday:
				currentDay = DayOfTheWeek.Monday; break;
			case Monday:
				currentDay = DayOfTheWeek.Tuesday; break;
			case Tuesday:
				currentDay = DayOfTheWeek.Wednesday; break;
			case Wednesday:
				currentDay = DayOfTheWeek.Thursday; break;
			case Thursday:
				currentDay = DayOfTheWeek.Friday; break;
			case Friday:
				currentDay = DayOfTheWeek.Saturday; break;
			case Saturday:
				currentDay = DayOfTheWeek.Sunday; break;
		}
	}

	public void setDay(String day) {
		if(day.equals("Sunday"))
			currentDay = DayOfTheWeek.Sunday;
		if(day.equals("Monday"))
			currentDay = DayOfTheWeek.Monday;
		if(day.equals("Tuesday"))
			currentDay = DayOfTheWeek.Tuesday;
		if(day.equals("Wednesday"))
			currentDay = DayOfTheWeek.Wednesday;
		if(day.equals("Thursday"))
			currentDay = DayOfTheWeek.Thursday;
		if(day.equals("Friday"))
			currentDay = DayOfTheWeek.Friday;
		if(day.equals("Saturday"))
			currentDay = DayOfTheWeek.Saturday;
	}
	
	public long getNumTicks() {
		return numTicks;
	}
	
	public void incrementNumTicks() {
		numTicks++;
		if(numTicks > END_OF_DAY) {
			numTicks = 0;
			setNextDay();
			timer.cancel();
			System.out.println("New day beginning soon");
			timer = new Timer();
			timer.schedule(new TimerTask(){
				public void run() {
					newDay();
				}
			}, NEW_DAY_DELAY);
		}
	}
	
	/* methods for accessing */
	
	public boolean nameExists(String name) {
		String[] people = getAllPeople();
		for(int i = 0; i < people.length; i++)
			if(people[i].equals(name))
				return true;
		return false;
	}
	
	public String[] getAllPeople() {
		ArrayList<String> names = new ArrayList<String>();
		for(int i = 0; i < people.size(); i++) {
			PersonAgent p = people.get(i);
			names.add(p.getName());
		}
		String[] namesArray = names.toArray(new String[0]);
		return namesArray;
	}
	
	public String[] getAllHousing() {
		ArrayList<String> locationNames = new ArrayList<String>();
		for(int i = 0; i < housings.size(); i++) {
			Housing h = housings.get(i);
			locationNames.add(h.getName());
		}
		String[] locationsArray = locationNames.toArray(new String[0]);
		return locationsArray;
	}
	
	public String[] getAllUnemployedPeople() {
		ArrayList<String> names = new ArrayList<String>();
		for(int i = 0; i < people.size(); i++) {
			PersonAgent p = people.get(i);
			if(!p.hasJob())
				names.add(p.getName());
		}
		String[] namesArray = names.toArray(new String[0]);
		return namesArray;
	}
	
	public boolean allPeopleSleeping() {
		for(int i = 0; i < people.size(); i++)
			if(!people.get(i).getBodyState().equals("Asleep"))
				return false;
		return true;
	}
	
	public 	ArrayList<PersonAgent> getPeople() {
		return people;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox)e.getSource();
        String scenarioName = (String)cb.getSelectedItem();
		AlertLog.getInstance().logInfo(AlertTag.CITY, "CITY", "Changed to "+scenarioName);
	}
}
