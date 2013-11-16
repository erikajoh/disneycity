package simcity.interfaces;

import simcity.PersonAgent;

public interface Transportation {
	public void msgGoTo(String startLocation, String endLocation, PersonAgent person, String method);
	public void msgPayFare(PersonAgent p, double money);
}
