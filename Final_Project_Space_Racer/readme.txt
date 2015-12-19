Space Race
CS 335 Final Group Project, Fall 2015
by Casey Hutchinson and Chintan Suthar

 turnin: 12/17/2015
 demo: 12/17/2015

This project uses OpenGL to create a Space Racer game.

We coded it using mostly standard libraries we used in class:

* OpenGL (with GLU)
* Random for random number generation
* AWT (for events, fonts, images, windows)
* IO, NIO, ImageIO (for images - file reading)
* LinkedList (for playback)
* Calendar (for timer)

	===== Usage =====

To use the program, launch the main method in SpaceRacer.java.

-- First person mode --
Dragging the mouse will aim the ship in a different direction. Movement and
firing are controlled by the keyboard (below). Once the ship is moving in a
direction, it will continue in that direction until it is set in another 
direction or something hits it. 

In this mode, the game is "active", so the timer is going, the asteroids are
moving, it is possible to pass through gates to score or win, and it is
possible to shoot at asteroids, which will explode if hit.

-- Third person mode --
Dragging the mouse will aim the ship in a different direction. Movement is
controlled by the keyboard (below). Each time the movement key is hit, the
ship will jump forward and stop to allow for more detailed viewing control.

In this mode, the game is "passive", so the timer is stopped, the asteroids
are still, it is impossible to pass through gates to score or win, and it is
not possible to shoot at asteroids.


Game play proceeds until either the player ship is hit by an asteroid or the
ship passes through all of the gates. The final gate is invisible until all
the other gates are passed. Once the game is over, a message shows up on the
screen saying if the player won or lost, and the game shows the player's last
game. At that point, it can be reset with a key.


The important keys:

	w		sets ship movement in the camera direction
	s		sets ship movement away from the camera direction
	a		stops movement
	d		stops movement
	Esc		toggles first person mode/third person mode
	Space	fires a laser forward at an asteroid (first person only)
	q		tilts the ship to the left - also resets the simulation when 
				the game is over
	e		tilts the ship to the right
	
	
	===== Code Structure =====
	
Space Racer (main) declares:
	JOGLEventListener (listener) declares:
		Simulator (main simulation handler) declares:
			Tuple (for coordinate passing)
			ReverseBox (for skybox)
			Racetrack (for track handling) declares:
				Timer (to time races)
				Gate (for gates) declares:
					Sphere (for drawing)
				Asteroid (for asteroid) declares:
					Sphere (for drawing)
					