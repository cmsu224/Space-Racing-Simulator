/*****
 * Timer.java
 * 
 * CS 335G Final Project
 * Casey Hutchinson and Chintan Suthar
 * 12/17/2015
 * 
 * Timer class for keeping track of timer information
 *****/

package spaceRacer;

import java.util.Calendar;

public class Timer {
	
	Calendar currentTime;		// the current time from system
	long accumulatedTime;		// to allow timer to stop
	long startTime;				// start time for time calculation
	boolean running;			// is this running or paused?
	
	// default constructor
	Timer() {
		reset();
		running = true;
	} // Timer()
	
	// reset all values
	public void reset() {
		currentTime = Calendar.getInstance();
		startTime = currentTime.getTimeInMillis();
		accumulatedTime = 0;
	} // reset()

	// return total time in milliseconds
	public long time() {
		if (running) {
			currentTime = Calendar.getInstance();
			return (currentTime.getTimeInMillis() - startTime) + accumulatedTime;
		} else {
			return accumulatedTime;
		}
	} // time()
	
	// stop this's clock
	public void stop() {
		currentTime = Calendar.getInstance();
		accumulatedTime = (currentTime.getTimeInMillis() - startTime) + accumulatedTime;
		startTime = currentTime.getTimeInMillis();
		running = false;
	} // stop()

	// start this's clock
	public void start() {
		currentTime = Calendar.getInstance();
		startTime = currentTime.getTimeInMillis();
		running = true;
	} // start()

	// answer if this is running
	public boolean isRunning() {
		return running;
	} // isRunning()

} // class
