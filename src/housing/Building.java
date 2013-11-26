package housing;

import housing.ResidentAgent.State;
import housing.test.mock.LoggedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class Building {
	int START_INVENTORY = 1;
	String type;
	Timer timer = new Timer();
	ResidentAgent resident;
	private HashMap<String, Integer> inventory = new HashMap<String, Integer>();
	Building(String t) {
		type = t;
		// TODO: This be hacked
		inventory.put("Mexican", START_INVENTORY);
		inventory.put("Southern", START_INVENTORY);
		inventory.put("Italian", START_INVENTORY);
		inventory.put("German", START_INVENTORY);
		inventory.put("American", START_INVENTORY);
	}
	private boolean getFood(String f) {
		if (inventory.get(f) != 0) {
			inventory.put(f, inventory.get(f)-1);
			return true;
		} else {
			return false;
		}
	}
	public void cookFood(ResidentAgent r, String food) {
		System.out.println("called cookfood");
		resident = r;
		if (getFood(food)) {
			timer.schedule(new TimerTask() {
				public void run() {
					System.out.println("food is done");
					resident.log.add(new LoggedEvent("Food is done"));
					resident.msgDoneCooking(true);
				}
			},
			1000);
		} else {
			System.out.println("no food");
			resident.log.add(new LoggedEvent("No food"));
			resident.msgDoneCooking(false);
			return;
		}
	}
	public void addItems(Map<String, Integer> items) {
		Set<String> keySet = items.keySet();
		String[] keyArray = keySet.toArray(new String[keySet.size()]);
		for(int i = 0; i < keyArray.length; i++) {
			if(inventory.get(keyArray[i]) != null) {
				Integer aQuantity = items.get(keyArray[i]);
				inventory.put(keyArray[i], aQuantity + inventory.get(keyArray[i]));
			}
		}
	}
}
