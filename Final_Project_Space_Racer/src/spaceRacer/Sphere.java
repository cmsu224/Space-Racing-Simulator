/*****
 * Sphere.java
 * 
 * CS 335G Final Project
 * Casey Hutchinson and Chintan Suthar
 * 12/17/2015
 * 
 * Sphere class for drawing asteroids
 *****/

package spaceRacer;

import java.util.Random;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

// Sphere class - information for drawing and storing a Circle

public class Sphere {
	
	// private variables - final radius of the Sphere and RGB elements
	//   of the circle's final color
	private float radius;
	private Tuple[][] xyz;
	
	private Tuple[][] explosionVector;
	// the size of the step we use to draw the Sphere
	//   lower produces a smoother Sphere, higher uses less power 
	//   to draw
	final private int steps = 15;
	// the number of latitude steps we have
	final int latN = 180/steps + 1;
	// the number of longitude steps we have
	final int lonN = 360/steps;
	private Tuple location;

		// r, g, b;

	// Constructor - takes radius as parameter and sets default color 

	public Sphere(float new_radius, Tuple origin) {
		int i = 0, j = 0;

		radius = new_radius;
		location = origin;

		xyz = new Tuple[latN][lonN];
		for (double lat = -90; lat < 90 + steps; lat += steps, i++) {
			double y = Math.sin(lat*Math.PI/180) * radius + origin.y();
			j = 0;
			for (float lon = -180; lon < 180; lon += steps, j++) {
				double r = Math.cos(lat*Math.PI/180.0) * radius;
				double x = Math.cos(lon*Math.PI/180.0) * r + origin.x();
				double z = Math.sin(lon*Math.PI/180.0) * r + origin.z();
				
				xyz[i][j] = new Tuple(x,y,z);
			} // for
		} // for
		
	} // Sphere()
		
	// draw - takes GL2 handle and draws the Sphere - scales the Sphere 
	//   to radius 
	public void draw(GL2 gl, int texture) {

		// draw the Sphere
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		
        // Draw vertices from data in xyz array
		gl.glBindTexture(GL2.GL_TEXTURE_2D, texture);
		gl.glBegin(GL.GL_TRIANGLES);
		for(int i = 0; i < latN-1; i++) {
			for(int j = 0; j < lonN; j++) {
				Tuple p0 = xyz[i][j];
				Tuple p1 = xyz[(i+1)%latN][j];
				Tuple p2 = xyz[(i+1)%latN][(j+1)%lonN];
				Tuple p3 = xyz[i][(j+1)%lonN];
				
				gl.glTexCoord2f(0, 1);
				gl.glVertex3d(p0.x(), p0.y(), p0.z());
				gl.glTexCoord2f(1, 1);
				gl.glVertex3d(p1.x(), p1.y(), p1.z());
				gl.glTexCoord2f(1, 0);
				gl.glVertex3d(p2.x(), p2.y(), p2.z());

				gl.glTexCoord2f(0, 1);
				gl.glVertex3d(p0.x(), p0.y(), p0.z());
				gl.glTexCoord2f(1, 1);
				gl.glVertex3d(p2.x(), p2.y(), p2.z());
				gl.glTexCoord2f(1, 0);
				gl.glVertex3d(p3.x(), p3.y(), p3.z());

			} // for
		} // for
		
		// close up - End() gl calls, pop the matrix
		gl.glEnd();
	
		gl.glPopMatrix();
	} // draw()

	// moves this to a new location by translating its components
	public void move(Tuple direction) {
		for(int i = 0; i < latN; i++) {
			for(int j = 0; j < lonN; j++) {
				xyz[i][j].add(direction);
			} // for
		} // for
		location.add(direction);
	} // move()
	
	// set up an explosion vector if the object needs to blow up
	public void setExplosionVector() {
		Random randomNumber = new Random();
		final double explosionSpeed = 5;

		explosionVector = new Tuple[latN][lonN];
		
		for(int i = 0; i < latN; i++) {
			for(int j = 0; j < lonN; j++) {
				double x = xyz[i][j].x() - location.x();
				double y = xyz[i][j].y() - location.y();
				double z = xyz[i][j].z() - location.z();
				double factor = explosionSpeed * randomNumber.nextDouble();
				
				explosionVector[i][j] = new Tuple (x, y, z);
				explosionVector[i][j].normalize();
				explosionVector[i][j].scale(factor, factor, factor);
			} // for
		} // for
	} // setExplosionVector
	
	// animate an object exploding
	public void explode() {
		if(explosionVector == null) {
			setExplosionVector();
		}
		
		for(int i = 0; i < latN; i++) {
			for(int j = 0; j < lonN; j++) {
				xyz[i][j].add(explosionVector[i][j]);
			} // for
		} // for
	} // explode()
	
} // class
