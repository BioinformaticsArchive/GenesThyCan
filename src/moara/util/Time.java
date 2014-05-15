package moara.util;

public class Time {

	public Time() {
		
	}
	
	public String getTime(long difference) {
		String elapsedTime = new String();
		int totalSec = (int)difference/1000;
		int totalMin = totalSec/60;
		int sec = totalSec%60;
		int totalHour = totalMin/60;
		int min = totalMin%60;
		elapsedTime = totalHour + ":" + min + ":" + sec;
		return elapsedTime;
	}
	
}
