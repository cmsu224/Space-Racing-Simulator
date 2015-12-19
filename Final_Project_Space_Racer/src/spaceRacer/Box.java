/*****
 * Box.java
 * 
 * CS 335G Final Project
 * Casey Hutchinson and Chintan Suthar
 * 12/17/2015
 * 
 * Box class to pass on to the reverseBox class
 *****/

package spaceRacer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
// import com.jogamp.opengl.glu.GLU;

public class Box {
	protected final int x = 0;		// allows array[x] or [y] instead of [0]
	protected final int y = 1;		// same
	protected final int z = 2;		// same
	protected double origin[];		// x, y, z to store the center of the Box
	protected double height;		// how wide and tall the Box is
	protected Tuple[] vertices = new Tuple[8];	// the vertices of the Box
	protected double[] lookAtCameraPosition = 	// camera data we can modify
		{0, 0, 0};
	protected double[] lookAtTargetPosition =	// camera data we can modify
		{0, 0, 1};
	protected double[] lookAtUpVector =			// camera data we can modify
		{0, 1, 0};
	
	protected int[] indices = {			// index list for drawing the quads
			0, 1, 2, 3,		// rear face
			4, 0, 3, 7,		// left face
			5, 4, 7, 6,		// front face
			1, 5, 6, 2,		// right face
			6, 7, 3, 2,		// bottom face 
			4, 5, 1, 0		// top face
	};
	
	// constructor - gets an origin point (x, y, z) and a height - derives 
	//   the vertices from that
	public Box(double orig_x, double orig_y, double orig_z, double new_height) {

		origin = new double[3];
		origin[x] = orig_x;
		origin[y] = orig_y;
		origin[z] = orig_z;
		
		height = new_height;
		for(int i = 0; i < 8; i++) {
			vertices[i] = new Tuple();
		}
		calculateVertices();
	}

	// calculates vertices based on the origin and the height
	private void calculateVertices() {
		final double half_height = height / 2d;

		vertices[0].set(
				origin[x] - half_height,
				origin[y] - half_height,
				origin[z] + half_height);
		vertices[1].set(
				origin[x] + half_height,
				origin[y] - half_height,
				origin[z] + half_height);
		vertices[2].set(
				origin[x] + half_height,
				origin[y] + half_height,
				origin[z] + half_height);
		vertices[3].set(
				origin[x] - half_height,
				origin[y] + half_height,
				origin[z] + half_height);
		vertices[4].set(
				origin[x] - half_height,
				origin[y] - half_height,
				origin[z] - half_height);
		vertices[5].set(
				origin[x] + half_height,
				origin[y] - half_height,
				origin[z] - half_height);
		vertices[6].set(
				origin[x] + half_height,
				origin[y] + half_height,
				origin[z] - half_height);
		vertices[7].set(
				origin[x] - half_height,
				origin[y] + half_height,
				origin[z] - half_height);

	}
	
	// draw function - called from display in Simulator
	// sets up the perspective and plots the vertices
	protected void draw(GL2 gl, int texture) {

        // set up the texture and begin the shape
        gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		gl.glBegin(GL2.GL_QUADS);
		
		// using the indices, plot the vertices and apply textures
		for(int i = 0; i < indices.length-3; i += 4) {
	        gl.glTexCoord2f(0, 1);	        
	        gl.glVertex3d(
	        		vertices[indices[i]].x(),
	        		vertices[indices[i]].y(),
	        		vertices[indices[i]].z());

	        gl.glTexCoord2f(1, 1);
	        gl.glVertex3d(
	        		vertices[indices[i+1]].x(),
	        		vertices[indices[i+1]].y(),
	        		vertices[indices[i+1]].z());
	        
	        gl.glTexCoord2f(1, 0);
	        gl.glVertex3d(
	        		vertices[indices[i+2]].x(),
	        		vertices[indices[i+2]].y(),
	        		vertices[indices[i+2]].z());
	        
	        gl.glTexCoord2f(0, 0);
	        
	        gl.glVertex3d(
	        		vertices[indices[i+3]].x(),
	        		vertices[indices[i+3]].y(),
	        		vertices[indices[i+3]].z());
		} // for

		// close up
		gl.glEnd();
		
	} // draw()
	
	// move - Simulator call to move the box
	public void move(double move_x, double move_y, double move_z) {
		setCamera(lookAtCameraPosition[x] + move_x, lookAtCameraPosition[y] + move_y, lookAtCameraPosition[z] + move_z);
	} // move()
	
	// set the object camera position
	public void setCamera(double camx, double camy, double camz) {
		lookAtCameraPosition[x] = camx;
		lookAtCameraPosition[y] = camy;
		lookAtCameraPosition[z] = camz;
	} // setCamera()
	
	// set the viewing angle 
	public void setTarget(double targetx, double targety, double targetz) {
		lookAtTargetPosition[x] = targetx;
		lookAtTargetPosition[y] = targety;
		lookAtTargetPosition[z] = targetz;
	} // setTarget()
	
} // class
