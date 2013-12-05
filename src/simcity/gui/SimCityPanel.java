package simcity.gui;

import restaurant_bayou.gui.RestaurantBayou;
import restaurant_cafe.gui.RestaurantCafe;
import restaurant_haus.gui.HausAnimationPanel;
import restaurant_haus.gui.RestaurantHaus;
import restaurant_pizza.gui.RestaurantPizza;
import restaurant_rancho.gui.RestaurantRancho;
import simcity.PersonAgent;
import simcity.Restaurant;
import simcity.interfaces.Transportation_Douglass;
import simcity.test.mock.MockTransportation_Douglass;
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

public class SimCityPanel extends JPanel implements ActionListener{
	
	Timer timer;
	Timer animationTimer;
	
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
	
	public final static int NEW_DAY_DELAY = 3000;
	 	 
	ArrayList<PersonAgent> people = new ArrayList<PersonAgent>();
	ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
	ArrayList<Housing> housings = new ArrayList<Housing>();
	ArrayList<Market> markets = new ArrayList<Market>();
	ArrayList<JPanel> animationPanelsList = new ArrayList<JPanel>();
	
	Transportation transportation;
	 
	public SimCityPanel(SimCityGui gui) {
		
		this.gui = gui;
		
		JPanel selection = new JPanel();
		String[] scenarios = { "Scenario 1", "Scenario 2", "Scenario 3", "Scenario 4", "Scenario 5", "Scenario 6", "Scenario 7", "Scenario 10" };
		//Create the combo box, select item at index 0.
		JComboBox scenarioList = new JComboBox(scenarios);
		scenarioList.setSelectedIndex(0);
		scenarioList.addActionListener(this);
		selection.setLayout(new FlowLayout());
		selection.add(new JLabel("Choose a scenario:"));
		selection.add(scenarioList);
		add(selection);
		
		String foodPreferenceMexican = "Mexican";
		String foodPreferenceItalian = "Italian";

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
		Market firstMarket = gui.mickeysMarket;
		Bank firstBank = gui.pirateBank;
		
		animationPanelsList = gui.animationPanelsList;
		transportation = gui.cityAniPanel.getTransportation();
		
		// All PersonAgents are instantiated here. Upon instantiation, we must pass
		// all pointers to all things (restaurants, markets, housings, banks) to the person as follows:
		
		try {
			URL fileURL = getClass().getResource("/res/simcity_config.txt");
			URI fileURI = fileURL.toURI();
			BufferedReader br = new BufferedReader(new FileReader(new File(fileURI)));
			String what = br.readLine();
			int numRecords = Integer.parseInt(what);
			for(int i = 0; i < numRecords; i++) {
				StringTokenizer st = new StringTokenizer(br.readLine(), "|");
				String st_Name = st.nextToken();
				String st_HousingName = st.nextToken();
				double st_Money = Double.parseDouble(st.nextToken());
				String st_FoodPref = st.nextToken();
				String st_AtHome = st.nextToken();
					boolean st_preferAtHome = st_AtHome.charAt(0) == 't' ? true : false;
				String st_HousingRelation = st.nextToken();
				String st_Commute = st.nextToken();
				
				Housing st_Housing = mapStringToHousing(st_HousingName);
				PersonAgent personToAdd = new PersonAgent(
						st_Name, st_Housing, st_Money, st_FoodPref, st_preferAtHome,
						st_HousingRelation, transportation, st_Commute.charAt(0));
				personToAdd.startThread();
				people.add(personToAdd);
			}
			numRecords = Integer.parseInt(br.readLine());
			for(int i = 0; i < numRecords; i++) {
				StringTokenizer st = new StringTokenizer(br.readLine(), "|");
				String st_HousingName = st.nextToken();
				Housing st_Housing = mapStringToHousing(st_HousingName);
				String st_OwnerName = st.nextToken();
				PersonAgent st_Owner = mapStringToPerson(st_OwnerName);
				
				st_Housing.setOwner(st_Owner);
				while(st.hasMoreTokens()) {
					String st_RenterName = st.nextToken();
					PersonAgent st_Renter = mapStringToPerson(st_RenterName);
					st_Housing.addRenter(st_Renter);
				}
			}
			numRecords = Integer.parseInt(br.readLine());
			for(int i = 0; i < numRecords; i++) {
				StringTokenizer st = new StringTokenizer(br.readLine(), "|");
				String st_PersonName = st.nextToken();
				String st_RestName = st.nextToken();
				String st_RelationName = st.nextToken();
				int st_session = Integer.parseInt(st.nextToken());
				
				PersonAgent st_Person = mapStringToPerson(st_PersonName);
				Restaurant r = mapStringToRestaurant(st_RestName);
				st_Person.addRestaurant(r, st_RelationName, st_session);
			}
			
			for(int i = 0; i < people.size(); i++) {
				PersonAgent currPerson = people.get(i);
				
				currPerson.addMarket(firstMarket, "Customer");
				currPerson.addBank(firstBank, "Customer");
				
				currPerson.startThread();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	    setLayout(new GridLayout());
	
		/* timing */
	    newDay();
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
			// Once each one passes, the person would eat.
			// The exact delay between change in day phase and becoming hungry is randomized as demonstrated below.
			if(currTicks == MORNING || currTicks == NOON || currTicks == EVENING) {
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
				theHousing.msgRentDue();
			}
		}
		
		// handle ticks for restaurants
		for(int i = 0; i < restaurants.size(); i++) {
			Restaurant theRestaurant = restaurants.get(i);
			// rent is due signal: at the start of every Saturday
			// TODO whole rent system needs to be tested with actual PersonAgents
			if(currTicks == WORK_ONE_END || currTicks == WORK_TWO_END) {
				theRestaurant.msgEndOfShift();
			}
		}
	}
	
	/* all time-related variables and methods */
	public enum DayOfTheWeek { Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday };
	public DayOfTheWeek currentDay = DayOfTheWeek.Thursday;
	public long numTicks = 0;
	
	/* Time intervals */
	private static final int TICK_DELAY = 125; // every 1/8 second = one clock tick
	
	// these are start times for each of the day phases
	private static final long START_OF_DAY		= 1;
	private static final long MORNING			= START_OF_DAY + 40; //41
	private static final long WORK_ONE_START	= MORNING + 160;//201
	private static final long NOON				= WORK_ONE_START + 160;//361
	private static final long WORK_ONE_END		= NOON + 160;//521
	private static final long WORK_TWO_START	= WORK_ONE_END + 60;//561
	private static final long EVENING			= WORK_TWO_START + 160;//721
	private static final long WORK_TWO_END		= EVENING + 160;//881
	private static final long NIGHT				= WORK_TWO_END + 60;//921
	private static final long END_OF_DAY		= NIGHT + 800;//1271
	// length of day 1231
	
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
	
	public boolean allPeopleSleeping() {
		for(int i = 0; i < people.size(); i++)
			if(!people.get(i).getBodyState().equals("Asleep"))
				return false;
		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
//		JComboBox cb = (JComboBox)e.getSource();
//        String scenarioName = (String)cb.getSelectedItem();
	}
}
