/*****
 * CameraTracker.java
 * 
 * CS 335G Final Project
 * Casey Hutchinson and Chintan Suthar
 * 12/17/2015
 * 
 * CameraTracker for storing and manipulating calendar data
 *****/

package spaceRacer;

import com.jogamp.opengl.glu.GLU;

public class CameraTracker {
	protected final int x = 0;		// allows array[x] or [y] instead of [0]
	protected final int y = 1;		// same
	protected final int z = 2;		// same
	double lookAtCameraPosition[];	// camera current position [x, y, z]
	double lookAtTargetPosition[];	// camera look at point [x, y, z]
	double lookAtUpVector[];		// camera up vector [x, y, z]

	// default initializer
	public CameraTracker() {
		lookAtCameraPosition = new double[3];
		lookAtTargetPosition = new double[3];
		lookAtUpVector = new double[3];
		
		setCamera(0, 0, 0,
				0, 0, 0,
				0, 0, 0);
	} // CameraTracker()
	
	// initializer with parameters
	public CameraTracker(double position_x, 
			double position_y, 
			double position_z, 
			double target_x,
			double target_y,
			double target_z,
			double up_x,
			double up_y,
			double up_z) {
		
		lookAtCameraPosition = new double[3];
		lookAtTargetPosition = new double[3];
		lookAtUpVector = new double[3];
		
		setCamera(position_x, position_y, position_z,
				target_x, target_y,  target_z,
				up_x, up_y, up_z);
	} // CameraTracker()
	
	// initializer with other CameraTracker to copy
	public CameraTracker(CameraTracker that) {
		this.lookAtCameraPosition = new double[3];
		this.lookAtTargetPosition = new double[3];
		this.lookAtUpVector = new double[3];
		
		setCamera(that.lookAtCameraPosition[x],
				that.lookAtCameraPosition[y],
				that.lookAtCameraPosition[z],

				that.lookAtTargetPosition[x],
				that.lookAtTargetPosition[y],
				that.lookAtTargetPosition[z],
				
				that.lookAtUpVector[x],
				that.lookAtUpVector[y],
				that.lookAtUpVector[z]);
	} // CameraTracker()
	
	// private procedure to set internal variables  
	private void setCamera(double position_x, 
			double position_y, 
			double position_z, 
			double target_x,
			double target_y,
			double target_z,
			double up_x,
			double up_y,
			double up_z) {

		lookAtCameraPosition[x] = position_x;
		lookAtCameraPosition[y] = position_y;
		lookAtCameraPosition[z] = position_z;
		
		lookAtTargetPosition[x] = target_x;
		lookAtTargetPosition[y] = target_y;
		lookAtTargetPosition[z] = target_z;
		
		lookAtUpVector[x] = up_x;
		lookAtUpVector[y] = up_y;
		lookAtUpVector[z] = up_z;		
	} // setCamera()
	
	// calling the GLU lookat with internal parameters
	public void lookAt(GLU glu) {
        glu.gluLookAt(
        		lookAtCameraPosition[x],
        		lookAtCameraPosition[y],
        		lookAtCameraPosition[z], 
        		lookAtTargetPosition[x],
        		lookAtTargetPosition[y],
        		lookAtTargetPosition[z],
        		lookAtUpVector[x],
        		lookAtUpVector[y],
        		lookAtUpVector[z]);
	} // lookAt()
	
	// set this look at target with parameters
	public void setTarget(double target_x, double target_y, double target_z) {
		lookAtTargetPosition[x] = target_x;
		lookAtTargetPosition[y] = target_y;
		lookAtTargetPosition[z] = target_z;
	} // setTarget
	
	// transform (move) the camera
	public void transform(double trans_x, double trans_y, double trans_z) {
		lookAtCameraPosition[x] = lookAtCameraPosition[x] + trans_x;
		lookAtCameraPosition[y] = lookAtCameraPosition[y] + trans_y;
		lookAtCameraPosition[z] = lookAtCameraPosition[z] + trans_z;
	} // transform()
	
	// returns camera position x coordinate
	public double position_x() {
		return lookAtCameraPosition[x];
	} // position_x()
	
	// returns camera position y coordinate
	public double position_y() {
		return lookAtCameraPosition[y];
	} // position_y()

	// returns camera position z coordinate
	public double position_z() {
		return lookAtCameraPosition[z];
	} // position_z()
	
	// sets the camera position to the parameter Tuple 
	public void position(Tuple position) {
		position.set(lookAtCameraPosition[x], lookAtCameraPosition[y], lookAtCameraPosition[z]);
	} // position()
	
	// sets the camera look at target to the parameter Tuple 
	public void lookAt(Tuple lookAt) {
		lookAt.set(lookAtTargetPosition[x], lookAtTargetPosition[y], lookAtTargetPosition[z]);
	} // lookAt()

	// sets the parameter Tuple as a directional vector between the 
	//   current camera position and the look at position
	public void getVector(Tuple request) {
		request.set(
				lookAtTargetPosition[x] - lookAtCameraPosition[x],
				lookAtTargetPosition[y] - lookAtCameraPosition[y],
				lookAtTargetPosition[z] - lookAtCameraPosition[z]);
		request.normalize();
	} // getVector()
	
	// sets the up vector to the coordinates in the Tuple parameter
	public void setUpVector(Tuple upVector) {
		lookAtUpVector[x] = upVector.x();
		lookAtUpVector[y] = upVector.y();
		lookAtUpVector[z] = upVector.z();
	} // setUpVector
	
	// sets the camera position to the coordinates in the Tuple parameter
	public void setPosition(Tuple position) {
		lookAtCameraPosition[x] = position.x();
		lookAtCameraPosition[y] = position.y();
		lookAtCameraPosition[z] = position.z();
	} // setPosition()
	
	// sets the look at point to the coordinates in the Tuple parameter
	public void setTarget(Tuple target) {
		lookAtTargetPosition[x] = target.x();
		lookAtTargetPosition[y] = target.y();
		lookAtTargetPosition[z] = target.z();
	} // setTarget()
	
} // class
