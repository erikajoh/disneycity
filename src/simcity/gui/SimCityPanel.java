package simcity.gui;

import restaurant_rancho.gui.RestaurantRancho;
import simcity.PersonAgent;
import simcity.interfaces.Transportation_Douglass;
import simcity.test.mock.MockTransportation_Douglass;
import transportation.Transportation;
import transportation.TransportationPanel;
import housing.Housing;

import javax.swing.*;

import bank.gui.Bank;
import market.Market;

import java.awt.*;
import java.util.*;
import java.util.Timer;

public class SimCityPanel extends JPanel{
	
	Timer timer;
	Timer animationTimer;
	
	SimCityGui gui = null;
	RestaurantRancho restRancho;
	 	 
	ArrayList<PersonAgent> people = new ArrayList<PersonAgent>();
	ArrayList<Housing> housings = new ArrayList<Housing>();
	ArrayList<Market> markets = new ArrayList<Market>();
	
	ArrayList<JPanel> animationPanelsList = new ArrayList<JPanel>();
	
	Transportation transportation;
	
	private JPanel group = new JPanel();
	 
	public SimCityPanel(SimCityGui gui) {
		
		this.gui = gui;
		restRancho = gui.restRancho;
		
		Housing firstHousing = gui.hauntedMansion;
		Market firstMarket = gui.mickeysMarket;
		Bank firstBank = gui.pirateBank;
		String foodPreferenceMexican = "Mexican";
		
		Housing secondHousing = gui.mainStApts1;
		
		animationPanelsList = gui.animationPanelsList;
		transportation = gui.cityAniPanel.getTransportation();
		
		// All PersonAgents are instantiated here. Upon instantiation, we must pass
		// all pointers to all things (restaurants, markets, housings, banks) to the person as follows:
		PersonAgent firstHackedPerson = new PersonAgent("Narwhal Prime", firstHousing, 50, foodPreferenceMexican, "OwnerResident", transportation);
		firstHousing.setOwner(firstHackedPerson);
		firstHousing.addRenter(firstHackedPerson);
		firstHackedPerson.addRestaurant(restRancho, "Customer");
		firstHackedPerson.addMarket(firstMarket, "Customer");
		firstHackedPerson.addBank(firstBank, "Customer");
		people.add(firstHackedPerson);

		/*
		PersonAgent secondHackedPerson = new PersonAgent("Narwhal Secondary", secondHousing, 60, foodPreferenceMexican, "Renter", transportation);
		firstHousing.setOwner(firstHackedPerson);
		firstHousing.addRenter(secondHackedPerson);
		secondHackedPerson.addRestaurant(restRancho, "Customer");
		secondHackedPerson.addMarket(firstMarket, "Customer");
		secondHackedPerson.addBank(firstBank, "Customer");
		people.add(secondHackedPerson);
		*/

		// Alternatively, you can call the next line as a hack (in place of the previous three lines)
		//		 firstHousing.setOwner();
		 
		firstHackedPerson.startThread();
		//secondHackedPerson.startThread();

	    setLayout(new GridLayout());
	
		/* timing */
	    newDay();
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
		if(currTicks % 100 == 0)
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
			
			// body state signals
			// waking up and sleeping
			if(currTicks == START_OF_DAY) {
				person.msgWakeUp();
			}
			if(currTicks == END_OF_DAY) {
				person.msgGoToSleep();
			}
			// person maintenance signal
			// maintain house if it's Friday morning
			if(currTicks == MORNING && getCurrentDay().equals("Friday")) {
				person.msgNeedMaintenance();
			}
			
			// job signals
			// two constants, WORK_ONE and WORK_TWO, determine when to send the signals to go to work
			if(currTicks == WORK_ONE) {
				person.msgGoToWork(1);
			}
			if(currTicks == WORK_TWO) {
				person.msgGoToWork(2);
			}
		}
		
		// handle ticks for housing
		for(int i = 0; i < housings.size(); i++) {
			Housing theHousing = housings.get(i);
			// rent is due signal
			// rent is due at the start of every Saturday
			if(currTicks == START_OF_DAY && getCurrentDay().equals("Saturday")) {
				theHousing.msgRentDue();
			}
		}
	}
	
	/* all time-related variables and methods */
	public enum DayOfTheWeek { Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday };
	public DayOfTheWeek currentDay = DayOfTheWeek.Thursday;
	public long numTicks = 0;
	
	/* Time intervals */
	private static final int TICK_DELAY = 125; // every 1/8 second = one clock tick
	
	// these are start times for each of the day's phases
	private static final long START_OF_DAY = 1;
	private static final long MORNING = 30;
	private static final long WORK_ONE = 110;
	private static final long NOON = 200;
	private static final long WORK_TWO = 300;
	private static final long EVENING = 400;
	private static final long END_OF_DAY = 700;
	
	// for setting random delay for eating
	private static final int EAT_DELAY_MAX = 50;
	
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
			timer.cancel(); // TODO test that this stops entire simulation
			System.out.println("New day beginning soon");
			timer = new Timer();
			timer.schedule(new TimerTask(){
				public void run() {
					newDay();
				}
			}, 5000);
		}
	}
	
	public boolean allPeopleSleeping() {
		for(int i = 0; i < people.size(); i++)
			if(!people.get(i).getBodyState().equals("Asleep"))
				return false;
		return true;
	}
}
