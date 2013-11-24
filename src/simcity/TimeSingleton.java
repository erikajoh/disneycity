package simcity;

public enum TimeSingleton {
	INSTANCE;
	
	public enum DayOfTheWeek { Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday };
	public DayOfTheWeek currentDay;
	public long numTicks = 0;
	
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
