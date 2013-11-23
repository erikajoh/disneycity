package simcity.interfaces;

import simcity.PersonAgent;

public interface Transportation_Douglass {
	public void msgWantToGo(String startLocation, String endLocation, PersonAgent person, String method);
	public void msgPayFare(PersonAgent p, double money);
}
