package simcity.gui;

import restaurant_rancho.gui.RestaurantRancho;
import simcity.PersonAgent;
import simcity.interfaces.Transportation_Douglass;
import simcity.test.mock.MockTransportation_Douglass;
import housing.Housing;

import javax.swing.*;

import java.awt.*;
import java.util.*;
import java.util.Timer;

public class SimCityPanel extends JPanel{
	
	Timer timer;
	
	SimCityGui gui = null;
	RestaurantRancho restRancho;
	 	 
	ArrayList<PersonAgent> people = new ArrayList<PersonAgent>();
	ArrayList<Housing> housings = new ArrayList<Housing>();
	Transportation_Douglass transportation = new MockTransportation_Douglass("Mock Transportation");
	
	private JPanel group = new JPanel();
	 
	public SimCityPanel(SimCityGui gui) {
		
		
		this.gui = gui;
		restRancho = gui.restRancho;
		
		Housing firstHousing = new Housing(gui, "Haunted Mansion");
		String foodPreferenceMexican = "Mexican";
	 
		// All PersonAgents are instantiated here. Upon instantiation, we must pass
		// all pointers to all things (restaurants, markets, housings, banks) to the person as follows:
		PersonAgent firstHackedPerson = new PersonAgent("Narwhal Prime", firstHousing, foodPreferenceMexican, "OwnerResident", transportation);
		firstHousing.setOwner(firstHackedPerson);
		firstHousing.addRenter(firstHackedPerson);
		firstHackedPerson.addRestaurant(restRancho, "Customer");
		people.add(firstHackedPerson);
		
		// Alternatively, you can call the next line as a hack (in place of the previous three lines)
		//		 firstHousing.setOwner();
		 
		firstHackedPerson.startThread();
	
		/* timing */
	    setLayout(new GridLayout());
	    timer = new Timer();
	    timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				System.out.println("Timer has ticked: # ticks = " + getNumTicks());
				setNumTicks(getNumTicks() + 1);
				handleTick();
			}
		}, 0, TICK_DELAY);
	}
	
	public void handleTick() {
		for(int i = 0; i < people.size(); i++) {
			final PersonAgent person = people.get(i);
			if(getNumTicks() == NOON) {
				timer.schedule(new TimerTask() {
					public void run() {
						person.setIsNourished(false);
					}
				}, (int)(Math.random() * EAT_DELAY_MAX));
			}
			person.setIsNourished(false);
			
		}
	}
	
	/* all time-related variables and methods */
	public enum DayOfTheWeek { Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday };
	public DayOfTheWeek currentDay = DayOfTheWeek.Friday;
	public long numTicks = 0;
	
	/* Time intervals */
	private static final int TICK_DELAY = 250; // every quarter second = one clock tick
	
	// these are start times for each of the day's phases
	private static final long MORNING = 0;
	private static final long NOON = 180;
	private static final long EVENING = 360;
	private static final long END_OF_DAY = 540;
	
	// for setting random delay for eating
	private static final int EAT_DELAY_MAX = 40; // 10 seconds later at most when noon hits, for example
	
	public String getCurrentDay() {
		return currentDay.toString();
	}
	
	public long getNumTicks() {
		return numTicks;
	}
	
	public void setNumTicks(long numTicks) {
		this.numTicks = numTicks;
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
}
