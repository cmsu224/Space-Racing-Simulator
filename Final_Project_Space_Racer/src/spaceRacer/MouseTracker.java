/*****
 * MouseTracker.java
 * 
 * CS 335G Final Project
 * Casey Hutchinson and Chintan Suthar
 * 12/17/2015
 * 
 * Object to keep track of mouse data  
 *****/

package spaceRacer;

import java.awt.event.MouseEvent;

// MouseTracker class to make tracking mouse movements easier
public class MouseTracker {

	// MouseTracker class variables // 
	// current location of x and y pointer set with setPosition
	private float current_x = 0, current_y = 0;
	// The amount the mouse location has changed since the last
	//   setPosition - set by setPosition
	private float changed_x, changed_y;
	// window details to calculate click location
	int windowWidth, windowHeight;
	float orthoX=40;
		
	// Class functions // 

	// setWindow - called on window resize to get width and height
	public void setWindow(int width, int height) {
		windowWidth = width;
		windowHeight = height;
	} // setWindow()
	
	// setPosition - gets current location of mouse pointer and 
	//   calculates amount that has changed since last setPosition
	public void setPostion(MouseEvent e) {
		float XX = (e.getX()-windowWidth*0.5f)*orthoX/windowWidth;
		float YY = -(e.getY()-windowHeight*0.5f)*
				orthoX/windowWidth;
		changed_x = XX - current_x;
		changed_y = YY - current_y;
		current_x = XX;
		current_y = YY;
	} // setPostion()
	
	// change_x - returns the amount the x axis has changed
	public float change_x() {
		return changed_x;
	} // change_x()

	// change_y - returns the amount the y axis has changed
	public float change_y() {
		return changed_y;
	} // change_y()
}
