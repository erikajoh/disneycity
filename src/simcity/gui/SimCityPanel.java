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
	
	JComboBox scenarioList;
	JButton startButton = new JButton("Start");

	RestaurantRancho restRancho;
	RestaurantPizza restPizza;
	RestaurantBayou restBayou;
	RestaurantCafe restCafe;
	RestaurantHaus restHaus;
	
	Housing hauntedMansion;
	Housing cinderellaCastle;
	Housing rabbitHole;
	Housing pirateSuite;
	Housing spaceMountain;
	Housing tikiHut;
	Housing mainStApts1;
	Housing mainStApts2;
	Housing mainStApts3;
	Housing mainStApts4;
	Housing mainStApts5;
	Housing mainStApts6;
	Housing mainStApts7;
	Housing mainStApts8;
	Housing mainStApts9;
	Housing mainStApts10;
	Housing mainStApts11;
	
	Bank pirateBank;
	
	Market mickeysMarket;

	Transportation transportation;
	
	TransportationPanel transPanel; 
	
	public final static int NEW_DAY_DELAY = 3000;	 
	public final static int NUM_RESTAURANTS = 5;
	public final static int NUM_MARKETS = 1;
	public final static int NUM_BANKS = 1;
	
	public final static String MAIN_CONFIG_FILE = "simcity_config_v2_main.txt";

	// lists to hold all the different buildings
	ArrayList<PersonAgent> people = new ArrayList<PersonAgent>();
	ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
	ArrayList<Housing> housings = new ArrayList<Housing>();
	ArrayList<Market> markets = new ArrayList<Market>();
	ArrayList<Bank> banks = new ArrayList<Bank>();
	
	ArrayList<JPanel> animationPanelsList = new ArrayList<JPanel>();
	
	boolean isScenario7 = false;
	 
	public SimCityPanel(SimCityGui gui) {
		
		this.gui = gui;
		
		/* Scenario panel */
		JPanel selection = new JPanel();
		String[] scenarios = {	"1-One person go",
								"2-Three people go",
								"3-CookCashierMarket",
								"5-Bus stops",
								"6-Closed places",
								"7-Market deliver fail",
								"10-50 people",
								"DEBUG-eating",
								"DEBUG-CRASH"};
		// Create the combo box, select item at index 0.
		scenarioList = new JComboBox(scenarios);
		scenarioList.setSelectedIndex(0);
		scenarioList.addActionListener(this);
		selection.setLayout(new FlowLayout());
		selection.add(new JLabel(""));
		selection.add(scenarioList);
		startButton.addActionListener(this);
	    selection.add(startButton);
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
		cinderellaCastle = gui.cinderellaCastle;
		rabbitHole = gui.rabbitHole;
		pirateSuite = gui.pirateSuite;
		spaceMountain = gui.spaceMountain;
		tikiHut = gui.tikiHut;
		mainStApts1 = gui.mainStApts1;
		mainStApts2 = gui.mainStApts2;
		mainStApts3 = gui.mainStApts3;
		mainStApts4 = gui.mainStApts4;
		mainStApts5 = gui.mainStApts5;
		mainStApts6 = gui.mainStApts6;
		mainStApts7 = gui.mainStApts7;
		mainStApts8 = gui.mainStApts8;
		mainStApts9 = gui.mainStApts9;
		mainStApts10 = gui.mainStApts10;
		mainStApts11 = gui.mainStApts11;

		housings.add(hauntedMansion);
		housings.add(cinderellaCastle);
		housings.add(rabbitHole);
		housings.add(pirateSuite);
		housings.add(spaceMountain);
		housings.add(tikiHut);
		housings.add(mainStApts1);
		housings.add(mainStApts2);
		housings.add(mainStApts3);
		housings.add(mainStApts4);
		housings.add(mainStApts5);
		housings.add(mainStApts6);
		housings.add(mainStApts7);
		housings.add(mainStApts8);
		housings.add(mainStApts9);
		housings.add(mainStApts10);
		housings.add(mainStApts11);
		
		pirateBank = gui.pirateBank;
		
		mickeysMarket = gui.mickeysMarket;
		markets.add(mickeysMarket);
		
		banks.add(pirateBank);
		
		transportation = gui.cityAniPanel.getTransportation();
		
		animationPanelsList = gui.animationPanelsList;
		
	    setLayout(new GridLayout());
	    
	    //beginSimulation();
	}
	
	public void beginSimulation() {
		int scenarioInd = scenarioList.getSelectedIndex();
		String fileName = MAIN_CONFIG_FILE;
		
		if(scenarioInd == 0) {
			fileName = "config-file_scenario-1.txt"; // TODO
		}
		if(scenarioInd == 1) {
			fileName = "config-file_scenario-2.txt";
		}
		if(scenarioInd == 2) {
			fileName = "config-file_scenario-3.txt"; // TODO
		}
		if(scenarioInd == 3) {
			fileName = "config-file_scenario-5.txt"; // TODO
		}
		if(scenarioInd == 4) {
			fileName = "config-file_scenario-6.txt"; // TODO
		}
		if(scenarioInd == 5) {
			fileName = "config-file_scenario-7.txt";
			isScenario7 = true;
		}
		if(scenarioInd == 6) {
			fileName = "config-file_scenario-10.txt";
		}
		if(scenarioInd == 7) {
			
		}
		if(scenarioInd == 8) {
			fileName = "crash.txt";
			transportation.setCrashing();
		}
		initializeFromConfigFile(fileName);
		newDay();
	}
	
	public void setTransPanel(TransportationPanel tp) {
		transPanel = tp;
	}
	
	public void addPerson(String aName, String housingName, double startMoney, String foodPreference,
			boolean preferEatAtHome, char commute, String personality, String mickeyMarketRole, int mickeyMarketShift) {
		
		Housing h = mapStringToHousing(housingName);
		if(h.getNumResidents() >= 4 && h.isApartment() || h.getNumResidents() >= 1 && !h.isApartment())
			AlertLog.getInstance().logInfo(AlertTag.CITY, "CITY", "Failed to create person; that housing has no more empty spots!");
		else if(nameExists(aName))
			AlertLog.getInstance().logInfo(AlertTag.CITY, "CITY", "Failed to create person; person name already exists!");
		else {
			String type;
			if (h.getOwner() == null) {
				type = "OwnerResident";
			} else {
				type = "Renter";
			}
			PersonAgent personToAdd = new PersonAgent( aName, h, startMoney, foodPreference, preferEatAtHome,
				type, transportation, commute);
			
			if (type == "OwnerResident") {
				h.setOwner(personToAdd); // key step
			}
			h.addRenter(personToAdd);
			
			for(int restInd = 0; restInd < NUM_RESTAURANTS; restInd++) {
				Restaurant r = restaurants.get(restInd);
				personToAdd.addRestaurant(r, "Customer", 0);
			}
			
			for(int marketInd = 0; marketInd < NUM_MARKETS; marketInd++) {
				Market m = markets.get(marketInd);
				if(m.getName().equals("Mickey's Market"))
					personToAdd.addMarket(m, mickeyMarketRole, mickeyMarketShift);
				else
					personToAdd.addMarket(m, "Customer", 0);
			}
			
			personToAdd.setPersonality(personality);
			people.add(personToAdd);
			AlertLog.getInstance().logInfo(AlertTag.CITY, "CITY", "Created person: " + aName);
			personToAdd.startThread();
			personToAdd.msgWakeUp();
		}
	}
	
	// Configuration file parsing
	// Upon instantiation, pass all pointers to all things (restaurants, markets, housings, banks) to the person as follows:
	public void initializeFromConfigFile(String fileName) {
		try {			
			// begin new parser
			// from CSCI 201 website
			// Step 1: get all the names of the people so that:
				// a) the person knows which housing he lives
				// b) we know which properties files to use
			URL mainFileURL = getClass().getResource("/res/" + fileName);
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
				
				// parsing banks
				for(int bankInd = 1; bankInd <= NUM_BANKS; bankInd++) {
					String bankName = props.getProperty("bank" + bankInd + "_name");
					if(bankName == null) bankName = "Pirate Bank";
					
					String bankRole = props.getProperty("bank" + bankInd + "_role");
					if(bankRole == null) bankRole = "Customer";
					
					String bankShiftString = props.getProperty("bank" + bankInd + "_shift");
					int bankShift = 0;
					if(bankShiftString != null)
						bankShift = Integer.parseInt(bankShiftString);
					
					if(bankName != null) {
						Bank b = mapStringToBank(bankName);
						if(b != null)
							personToAdd.addBank(b, bankRole, bankShift); // key step
					}
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
			
			for(int personInd = 0; personInd < people.size(); personInd++) {
				PersonAgent currPerson = people.get(personInd);
				
				//currPerson.addBank(pirateBank, "Customer", 0);				
				currPerson.startThread();
			}
			// end new parser
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public Housing mapStringToHousing(String houseName) {
		if(houseName.equals("Haunted Mansion")){
			return hauntedMansion;
		}
		else if(houseName.equals("Cinderella Castle")){
			return cinderellaCastle;
		}
		else if(houseName.equals("Rabbit Hole")){
			return rabbitHole;
		}
		else if(houseName.equals("Pirate's Suite")){
			return pirateSuite;
		}
		else if(houseName.equals("Space Mountain")){
			return spaceMountain;
		}
		else if(houseName.equals("Tiki Hut")){
			return tikiHut;
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
		else if(houseName.equals("Main St Apartments #7")){
			return mainStApts7;
		}
		else if(houseName.equals("Main St Apartments #8")){
			return mainStApts8;
		}
		else if(houseName.equals("Main St Apartments #9")){
			return mainStApts9;
		}
		else if(houseName.equals("Main St Apartments #10")){
			return mainStApts10;
		}
		else if(houseName.equals("Main St Apartments #11")){
			return mainStApts11;
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
	
	public Bank mapStringToBank(String bankName) {
		for(int i = 0; i < banks.size(); i++)
			if(bankName.equals(banks.get(i).getBankName()))
				return banks.get(i);
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
	
	public void banksAreOpen(boolean open) {
		for(int i = 0; i < people.size(); i++) {
			PersonAgent person = people.get(i);
			person.msgSetBanksOpen(open);
		}
	}
	
	public void handleTick() {
		long currTicks = getNumTicks();

		if(currTicks % 20 == 0) {
			System.out.println("Timer has ticked: # ticks = " + currTicks);
		}
		
		if(currTicks % 10 == 0) {
			gui.updateDayInfo(currentDay.toString(), getDayPhase());
		}
		
		if(currTicks == START_OF_DAY) {
			if(currentDay == DayOfTheWeek.Sunday)
				AlertLog.getInstance().logInfo(AlertTag.CITY, "CITY", "It's " + currentDay.name() + "; banks are closed today.");
		}
		
		if(currTicks == WORK_ONE_START || currTicks == WORK_TWO_START) {
			AlertLog.getInstance().logInfo(AlertTag.CITY, "CITY", "Work Shift Start");
		}
		
		// handle ticks for people
		for(int i = 0; i < people.size(); i++) {
			final PersonAgent person = people.get(i);
			
			// closing times
			// people, not the banks themselves, know about whether banks are open at the start of the day
			// restaurants themselves will know whether they are open or not
			if(currTicks == START_OF_DAY) {
				if(currentDay == DayOfTheWeek.Sunday)
					person.msgIsSunday(true);
				else
					person.msgIsSunday(false);
			}
			
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
		if(currTicks == NIGHT) {
			System.out.println("CHANGING PANEL IMAGE FOR NIGHT");
			transPanel.changeDay();
		}
		if(currTicks == MORNING) {
			System.out.println("CHANGING PANEL IMAGE FOR MORNING");
			transPanel.changeDay();
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
			if(currTicks == WORK_ONE_START || currTicks == WORK_TWO_START) {
				theRestaurant.startOfShift();
			}
			if(currTicks == WORK_ONE_END || currTicks == WORK_TWO_END) {
				theRestaurant.endOfShift();
			}
		}
		
		// handle ticks for restaurants
		for(int i = 0; i < markets.size(); i++) {
			Market theMarket = markets.get(i);
			// rent is due signal: at the start of every Saturday
			// TODO whole rent system needs to be tested with actual PersonAgents
			if(currTicks == WORK_ONE_START || currTicks == WORK_TWO_START) {
				theMarket.startOfShift();
			}
			if(currTicks == WORK_ONE_END || currTicks == WORK_TWO_END) {
				theMarket.endOfShift();
			}
		}
		
		//handle ticks for banks
		for(int i = 0; i < banks.size(); i++) {
			Bank theBank = banks.get(i);
			// rent is due signal: at the start of every Saturday
			// TODO whole rent system needs to be tested with actual PersonAgents
			if(currTicks == WORK_ONE_END || currTicks == WORK_TWO_END) {
				theBank.msgClose();
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
	//timing
	private static final long START_OF_DAY		= 1;
	private static final long MORNING			= START_OF_DAY		+ 40; //41
	private static final long WORK_ONE_START	= MORNING			+ 80;//131
	private static final long NOON				= WORK_ONE_START	+ 240;//311
	private static final long WORK_ONE_END		= NOON				+ 240;//491
	private static final long WORK_TWO_START	= WORK_ONE_END		+ 250;//681
	private static final long EVENING			= WORK_TWO_START	+ 240;//861
	private static final long WORK_TWO_END		= EVENING			+ 240;//1041
	private static final long NIGHT				= WORK_TWO_END		+ 250;//1231
	private static final long END_OF_DAY		= NIGHT				+ 800;//2031
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
	
	public String getDayPhase() {
		if(numTicks >= END_OF_DAY)
			return "End of day";
		if(numTicks >= NIGHT)
			return "Night";
		if(numTicks >= WORK_TWO_END)
			return "End Phase 2";
		if(numTicks >= EVENING)
			return "evening";
		if(numTicks >= WORK_TWO_START)
			return "Work Phase 2";
		if(numTicks >= WORK_ONE_END)
			return "End Phase 1";
		if(numTicks >= NOON)
			return "noon";
		if(numTicks >= WORK_ONE_START)
			return "Work Phase 1";
		if(numTicks >= MORNING)
			return "morning";
		if(numTicks >= START_OF_DAY)
			return "a new day";
		return "ERROR";
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
	
	public ArrayList<PersonAgent> getPeople() {
		return people;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JComboBox) {
			JComboBox cb = (JComboBox)e.getSource();
	        String scenarioName = (String)cb.getSelectedItem();
			AlertLog.getInstance().logInfo(AlertTag.CITY, "CITY", "Changed to "+scenarioName);
		}
		if (e.getSource() instanceof JButton) {
			beginSimulation();
		}
	}
}
