/*****
 * Simulator.java
 * 
 * CS 335G Final Project
 * Casey Hutchinson and Chintan Suthar
 * 12/17/2015
 * 
 * Interface between the JoglEventListener and the object classes
 *****/

package spaceRacer;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.*;
import com.jogamp.opengl.util.awt.TextRenderer;

public class Simulator {
	
	final private int x = 0;	// allows array[x] or [y] instead of [0]
	final private int y = 1;	// same
	final private int NUMBER_OF_TEXTURES = 14;		// number of textures we're using

	private GL2 gl;				// OpenGL handle
	private GLU glu = new GLU();			// for GLU calls
	private double lookAtRadius = 200;	// radius for the camera lookAt point
	private float screenAspect = 1f;			// screen aspect ratio
	private ReverseBox skybox;		// ReverseBox for sky, background drawing
	private double[] rotate;		// rotation angle for sum view
	private double tilt = 0;		// tile angle for camera
	private boolean replayMode = false;
	final private double speed = 0.5;

	private int texID[]  = new int[NUMBER_OF_TEXTURES]; 	// array to store texture IDs

	final private int NUMBER_OF_CAMERAS = 2;
	private CameraTracker[] camera;
	private int currentCamera = 0;
	
	boolean freeView = false;
		
	private Racetrack track;
	Tuple momentum;
	
	LinkedList<Node> cameraPath;

	// constructor - sets class variables, binds images, sets up OpenGL
	public Simulator(GLAutoDrawable glDrawable) {
		gl = glDrawable.getGL().getGL2();	// set up gl handle
		
		// set up ReverseBox for skybox
		skybox = new ReverseBox(0, 0, 0, 5000);
		// skybox.setCamera(0, 0, 0);
		skybox.setTarget(0, 0, lookAtRadius);		
		
		momentum = new Tuple();
		
		camera = new CameraTracker[NUMBER_OF_CAMERAS];
		camera[0] = new CameraTracker(0, 0, 0,
				0, 0, 1,
				0, 1, 0);
		
		cameraPath = new LinkedList<Node>();
		cameraPath.add(new Node(camera[0]));
		
		rotate = new double[2];
		rotate[x] = rotate[y] = 0;
				
		// Load HUD texture
        // load an image; 
        try {
			BufferedImage aImage = ImageIO.read(new File("ShipInterior.png"));
			ByteBuffer buf = convertImageData(aImage);
			
			gl.glGenTextures(NUMBER_OF_TEXTURES, texID, 0);
			gl.glBindTexture(GL.GL_TEXTURE_2D, texID[0]);
			// GL.GL_SRGB_ALPHA
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, aImage.getWidth(), 
                    aImage.getHeight(), 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buf);

			gl.glEnable(GL.GL_TEXTURE_2D);
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			
			gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
			gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
        
        // load skybox.jpg image
        try {
			BufferedImage aImage = ImageIO.read(new File("skybox.jpg"));
			ByteBuffer buf = convertImageData(aImage);
			
			gl.glBindTexture(GL.GL_TEXTURE_2D, texID[1]);
			
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, aImage.getWidth(), 
                    aImage.getHeight(), 0, GL2.GL_BGR, GL.GL_UNSIGNED_BYTE, buf);
						
		} catch (IOException e) {
			e.printStackTrace();
		} 
        
        // load tree-01.jpg image
        try {
			BufferedImage aImage = ImageIO.read(new File("tree-01.png"));
			ByteBuffer buf = convertImageData(aImage);
			
			gl.glBindTexture(GL.GL_TEXTURE_2D, texID[2]);
			
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, aImage.getWidth(), 
                    aImage.getHeight(), 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE, buf);
						
		} catch (IOException e) {
			e.printStackTrace();
		} 

        // YellowTexture.jpg image load
        try {
			BufferedImage aImage = ImageIO.read(new File("YellowTexture.jpg"));
			ByteBuffer buf = convertImageData(aImage);
			
			gl.glBindTexture(GL.GL_TEXTURE_2D, texID[3]);
			
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, aImage.getWidth(), 
                    aImage.getHeight(), 0, GL2.GL_BGR, GL.GL_UNSIGNED_BYTE, buf);
						
		} catch (IOException e) {
			e.printStackTrace();
		} 

        // load GreyTexture.jpg image
        try {
			BufferedImage aImage = ImageIO.read(new File("GreyTexture.jpg"));
			ByteBuffer buf = convertImageData(aImage);
			
			gl.glBindTexture(GL.GL_TEXTURE_2D, texID[4]);
			
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, aImage.getWidth(), 
                    aImage.getHeight(), 0, GL2.GL_BGR, GL.GL_UNSIGNED_BYTE, buf);
						
		} catch (IOException e) {
			e.printStackTrace();
		} 
        
        
        try {
			BufferedImage aImage = ImageIO.read(new File("Space_Ship_Plate_04.jpg"));
			ByteBuffer buf = convertImageData(aImage);
			
			gl.glBindTexture(GL.GL_TEXTURE_2D, texID[5]);
			
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, aImage.getWidth(), 
                    aImage.getHeight(), 0, GL2.GL_BGR, GL.GL_UNSIGNED_BYTE, buf);
						
		} catch (IOException e) {
			e.printStackTrace();
		} 

        // load GreyTexture.jpg image
        try {
			BufferedImage aImage = ImageIO.read(new File("ShipTop.jpg"));
			ByteBuffer buf = convertImageData(aImage);
			
			gl.glBindTexture(GL.GL_TEXTURE_2D, texID[6]);
			
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, aImage.getWidth(), 
                    aImage.getHeight(), 0, GL2.GL_BGR, GL.GL_UNSIGNED_BYTE, buf);
						
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        try {
			BufferedImage aImage = ImageIO.read(new File("texture_mars.jpg"));
			ByteBuffer buf = convertImageData(aImage);
			
			gl.glBindTexture(GL.GL_TEXTURE_2D, texID[7]);
			
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, aImage.getWidth(), 
                    aImage.getHeight(), 0, GL2.GL_BGR, GL.GL_UNSIGNED_BYTE, buf);
						
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        try {
			BufferedImage aImage = ImageIO.read(new File("texture_mercury.jpg"));
			ByteBuffer buf = convertImageData(aImage);
			
			gl.glBindTexture(GL.GL_TEXTURE_2D, texID[8]);
			
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, aImage.getWidth(), 
                    aImage.getHeight(), 0, GL2.GL_BGR, GL.GL_UNSIGNED_BYTE, buf);
						
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        try {
			BufferedImage aImage = ImageIO.read(new File("texture_moon.jpg"));
			ByteBuffer buf = convertImageData(aImage);
			
			gl.glBindTexture(GL.GL_TEXTURE_2D, texID[9]);
			
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, aImage.getWidth(), 
                    aImage.getHeight(), 0, GL2.GL_BGR, GL.GL_UNSIGNED_BYTE, buf);
						
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        try {
			BufferedImage aImage = ImageIO.read(new File("texture_neptune.jpg"));
			ByteBuffer buf = convertImageData(aImage);
			
			gl.glBindTexture(GL.GL_TEXTURE_2D, texID[10]);
			
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, aImage.getWidth(), 
                    aImage.getHeight(), 0, GL2.GL_BGR, GL.GL_UNSIGNED_BYTE, buf);
						
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        try {
			BufferedImage aImage = ImageIO.read(new File("texture_sun.jpg"));
			ByteBuffer buf = convertImageData(aImage);
			
			gl.glBindTexture(GL.GL_TEXTURE_2D, texID[11]);
			
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, aImage.getWidth(), 
                    aImage.getHeight(), 0, GL2.GL_BGR, GL.GL_UNSIGNED_BYTE, buf);
						
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        try {
			BufferedImage aImage = ImageIO.read(new File("texture_venus_atmosphere.jpg"));
			ByteBuffer buf = convertImageData(aImage);
			
			gl.glBindTexture(GL.GL_TEXTURE_2D, texID[12]);
			
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, aImage.getWidth(), 
                    aImage.getHeight(), 0, GL2.GL_BGR, GL.GL_UNSIGNED_BYTE, buf);
						
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        try {
			BufferedImage aImage = ImageIO.read(new File("texture_venus_surface.jpg"));
			ByteBuffer buf = convertImageData(aImage);
			
			gl.glBindTexture(GL.GL_TEXTURE_2D, texID[13]);
			
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
	        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, aImage.getWidth(), 
                    aImage.getHeight(), 0, GL2.GL_BGR, GL.GL_UNSIGNED_BYTE, buf);
						
		} catch (IOException e) {
			e.printStackTrace();
		}



        // track.setTexture(texID[3]);
        		
        // set the black background
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        // set up the depth buffer
        gl.glClearDepth(1.0f);
        // enable depth testing
        gl.glEnable(GL.GL_DEPTH_TEST);
        // sets the type of depth testing
        
		track = new Racetrack(texID);				
	}
	
	// display - called by JoglEventListener in display() calls
	public void display() {
		// clear the previous scene
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		// clear out the model view
		
		
		if(replayMode) {
			if(cameraPath.size() <= 0) {
				return;
			}
			Node tempNode = cameraPath.removeFirst();
			camera[currentCamera].setPosition(tempNode.position);
			camera[currentCamera].setTarget(tempNode.lookAt);
			
			skybox.setTarget(
					tempNode.lookAt.x(),
					tempNode.lookAt.y(),
					tempNode.lookAt.z());
			
//			System.out.println("Node is: " + tempNode);
//			System.out.println("List has: " + cameraPath.size() + " more elements.");
		}

		camera[currentCamera].transform(momentum.x(), momentum.y(), momentum.z());
		if(!freeView && !replayMode) {
			cameraPath.add(new Node(camera[currentCamera]));			
		}
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        // draw the room objects and transform them to the current position
        roomDraw();
        
        
        // draw the skybox
        skybox.draw(gl, glu, texID[1]);		
        // box.draw(gl, glu, texID[2], texID[3]);

		// clear out the projection matrix
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        // draw the heads-up display that doesn't move
        drawHUD();

        // set up the perspective
        glu.gluPerspective(45.0f, screenAspect, 0.5, 20000);
		
	}
	
	// drawHUD - helper function to draw the heads up display
	//   make the draw() code a little more clear
	private void drawHUD() {
		if(freeView) {
			return;
		}
		
		double [] corners = {-1, 1, -0.55, -1};  // double [] corners = {-0.95, -0.70, 0.95, 0.80}; 
		final double z_coord = -1.0;
		// Tuple nextUp = new Tuple();
		// track.nextGate(nextUp);
        
		gl.glPushMatrix();
		gl.glBindTexture(GL.GL_TEXTURE_2D, texID[0]);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		
		gl.glBegin(GL2.GL_QUADS);
        gl.glNormal3f(0,  0, 1);
        gl.glColor4d(0, 0, 1, 1);
        
        gl.glTexCoord2f(0, 1);
		gl.glVertex3d(corners[0], corners[3], z_coord);

        gl.glTexCoord2f(1, 1);
		gl.glVertex3d(corners[1], corners[3], z_coord);
        
        gl.glTexCoord2f(1, 0);
		gl.glVertex3d(corners[1], corners[2], z_coord);
        
        gl.glTexCoord2f(0, 0);
		gl.glVertex3d(corners[0], corners[2], z_coord);
               
        gl.glEnd();
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        
        drawCrosshairs();
        gl.glPopMatrix();

        TextRenderer renderer;
        String text;
        if(track.gameOver()) {
        	// text = "Game Over!";
        	if(track.getLastTime() > 0) {
        		text = "You won in " + track.getLastTime() + " seconds.";
        	} else {
        		text = "You lose!";
        	}
	        renderer = new TextRenderer(new Font("Serif", Font.PLAIN, 24), true, true);        	
        } else {
	        // String test
	        text = track.gatesLeft() + " gates left.";
	        if(track.gatesLeft() == 1) {
	        	text = "Gates cleared! Finish the race!";
	        }
	        renderer = new TextRenderer(new Font("Serif", Font.PLAIN, 18), true, true);
        }
        renderer.beginRendering(500, 500);

        // gl.glTranslatef(0.5f, 0.5f, 0.0f);
        gl.glPushMatrix();
        //gl.glLoadIdentity();
        // gl.glTranslatef(0.5f, 0.5f, 0.0f);
        renderer.setColor(0.0f, 1.0f, 0f, 1);
        renderer.draw(text, 90, 90);
        renderer.flush();
        gl.glPopMatrix();

        renderer.endRendering();
        		
	}
	
	private ByteBuffer convertImageData(BufferedImage bufferedImage) {
        // build a byte buffer from the temporary image
        // that be used by OpenGL to produce a texture.
        
        DataBuffer buf = bufferedImage.getRaster().getDataBuffer(); 
        if(bufferedImage.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
        	int w = bufferedImage.getWidth();
        	int h = bufferedImage.getHeight();
        	
        	byte[] srcBuffer = ((DataBufferByte) buf).getData();
        	
        	for(int i = 0; i < w*h; i++) {
        		byte temp = srcBuffer[4*i];
        		srcBuffer[4*i+0] = srcBuffer[4*i+1];
        		srcBuffer[4*i+1] = srcBuffer[4*i+2];
        		srcBuffer[4*i+2] = srcBuffer[4*i+3];
        		srcBuffer[4*i+3] = temp;
        	}
        }
        
        byte[] data = ((DataBufferByte) buf).getData();

        return (ByteBuffer.wrap(data)); 
	}

	// get screen aspect from JoglEventListener when it's changed
	public void setAspect(int width, int height) {
//		screenHeight = height;
//		screenWidth = width;
		screenAspect = (float) width / (float) height;
	}

	
	// rotate view based on mouse movement from JoglEventListener
	public void rotate(double rot_x, double rot_y) {
		if(replayMode) {
			return;
		}
		Tuple rotateTuple = new Tuple();
		
		rotate[x] += 0.05 * rot_x;
		rotate[y] -= 0.05 * rot_y;
				
		rotateTuple.mouseRotate(rotate[x], rotate[y]);
		rotateTuple.scale(lookAtRadius, lookAtRadius, lookAtRadius);

		skybox.setTarget(
				rotateTuple.x(),
				rotateTuple.y(),
				rotateTuple.z());
		
		camera[currentCamera].setTarget(
				rotateTuple.x(),
				rotateTuple.y(),
				rotateTuple.z());

	}
	
	public void debug() {
		System.out.println("<Thruster>");
		System.out.printf("  <Momentum>%s</Momentum> \n", momentum);
		System.out.printf("  <Rotation>(%f, %f)</Rotation> \n", rotate[x],
				rotate[y]);
		System.out.println("</Thruster>");	
		System.out.println("Linked list contents:" + cameraPath);
		System.out.println("List has " + cameraPath.size() + " elements.");
	}
	
	// move the box based on the wasd key from JoglEventListener
	public void move(double move_x, double move_y) {
		if(replayMode) {
			return;
		}

//		Tuple test = new Tuple();
// 		Tuple moveTuple = new Tuple();
		if(move_x != 0) {
			// momentum.mouseRotate(rotate[x] + Math.PI/2f, rotate[y] + Math.PI/2f);
			// momentum.scale(move_x, move_x, move_x);
			momentum.set(0, 0, 0);
//			track.nextGate(test);
//			System.out.printf("Location is: %s \n", test);
//			camera[currentCamera].getVector(test);
//			test.normalize();
//			System.out.printf("Test vector: %s \n", test);
			
		} else {
			// momentum.mouseRotate(rotate[x], rotate[y]);
			// momentum.scale(move_y, move_y, move_y);
			camera[currentCamera].getVector(momentum);
			momentum.normalize();

			if(freeView) {
				momentum.scale(move_y, move_y, move_y);
				camera[currentCamera].transform(momentum.x(), momentum.y(), momentum.z());
				momentum.set(0, 0, 0);
			}
			
			momentum.scale(speed, speed, speed);

		}
//		moveTuple.mouseRotate(rotate[x], rotate[y]);
//		moveTuple.scale(move_x, 0, move_y);
		
		// box.move(moveTuple.x(), moveTuple.y(), moveTuple.z());
		// movement.add(moveTuple.x(), moveTuple.y(), moveTuple.z());
		
		// camera[currentCamera].transform(moveTuple.x(), moveTuple.y(), moveTuple.z());
		
	}
	
	public void toggleFreeView() {
		if(replayMode) {
			return;
		}

		if(freeView) {
			freeView = false;
			currentCamera = 0;
		} else {
			freeView = true;
			currentCamera = 1;
			camera[1] = new CameraTracker(camera[0]);
			momentum.set(0, 0, 0);
		}
		track.enableFreeView(freeView);			
	}
	
	public void tiltLeft() {
		if(replayMode) {
			reset();
			return;
		}

		tilt -= 0.01;
		camera[currentCamera].setUpVector(new Tuple(
				Math.cos((Math.PI / 2) + tilt),
				Math.sin((Math.PI / 2) + tilt), 0));
	}
	
	public void tiltRight() {
		if(replayMode) {
			return;
		}

		tilt += 0.01;
		camera[currentCamera].setUpVector(new Tuple(
				Math.cos((Math.PI / 2) + tilt),
				Math.sin((Math.PI / 2) + tilt), 0));
	}
	
	// roomDraw - helper function to draw and move the room contents
	//   make the draw() code a little more clear
	private void roomDraw() {

		if(track.gameOver()) {
			replayMode = true;
		}
		
		gl.glPushMatrix();
		camera[currentCamera].lookAt(glu);
		track.checkCollisions(camera[currentCamera]);
		track.draw(gl);		
        drawShip();
		gl.glPopMatrix();

	}
	
	public void reset() { // back to the beginning
		
		lookAtRadius = 200;
		tilt = 0;
		replayMode = false;
		currentCamera = 0;
		freeView = false;
		skybox = new ReverseBox(0, 0, 0, 5000);
		skybox.setTarget(0, 0, lookAtRadius);		
		momentum = new Tuple();
		camera = new CameraTracker[NUMBER_OF_CAMERAS];
		camera[0] = new CameraTracker(0, 0, 0,
				0, 0, 1,
				0, 1, 0);

		cameraPath = new LinkedList<Node>();
		cameraPath.add(new Node(camera[0]));

		rotate = new double[2];
		rotate[x] = rotate[y] = 0;

        // set the black background
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        // set up the depth buffer
        gl.glClearDepth(1.0f);
        // enable depth testing
        gl.glEnable(GL.GL_DEPTH_TEST);
        // sets the type of depth testing
        
		track = new Racetrack(texID);		
	}
	
	public void drawShip() {
		if(!freeView) {
			return;
		}
		gl.glPushMatrix();
		
		gl.glTranslated(camera[0].position_x(), camera[0].position_y(), camera[0].position_z());
		
    	//layer 1
    	//bottom triangle plain
    	 gl.glBindTexture(GL2.GL_TEXTURE_2D, texID[5]);
    	 gl.glBegin(GL2.GL_TRIANGLES);	// Drawing Using Triangles
    	 gl.glNormal3f(0.0f, 0.0f, -1.0f);
         gl.glTexCoord2f(0, 0); gl.glVertex3f(0, 0, 2);
         gl.glTexCoord2f(1, 0); gl.glVertex3f(1, 0, 0);
         gl.glTexCoord2f(0, 1); gl.glVertex3f(-1, 0, 0);
         gl.glEnd();
         
        //top triangle plain 
         
         gl.glBegin(GL2.GL_TRIANGLES);
         gl.glTexCoord2f(0, 0); gl.glVertex3f(0, 0.2f, 2);
         gl.glTexCoord2f(1, 0); gl.glVertex3f(1, 0.2f, 0);
         gl.glTexCoord2f(0, 1); gl.glVertex3f(-1, 0.2f, 0);
         gl.glEnd();
         
         //side right rectangle plain
         gl.glBegin(GL2.GL_QUADS);
         gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0, 0, 2);
         gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0, 0.2f, 2);
         gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1, 0.2f, 0);
         gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1, 0, 0);
         gl.glEnd();
         
         //side left rectangle plain
         gl.glBegin(GL2.GL_QUADS);
         gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0, 0, 2);
         gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0, 0.2f, 2);
         gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(1, 0.2f, 0);
         gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(1, 0, 0);
         gl.glEnd();
         
         //back of bottom triangle
         gl.glBegin(GL2.GL_QUADS);
         gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(1, 0.2f, 0);
         gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(1, 0, 0);
         gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-1, 0, 0);
         gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1, 0.2f, 0);
         gl.glEnd();
         
         //layer 2
         //bottom plain of rectangle box
         gl.glBindTexture(GL2.GL_TEXTURE_2D, texID[6]);
         gl.glBegin(GL2.GL_QUADS);
         gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.3f, 0.2f, 1.3f);
         gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0.3f, 0.2f, 1.3f);
         gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.3f, 0.2f, 0f);
         gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-0.3f, 0.2f, 0f);
         gl.glEnd();
         
       //top plain of rectangle box
         
         gl.glBegin(GL2.GL_QUADS);
         gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.3f, 0.4f, 1.3f);
         gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0.3f, 0.4f, 1.3f);
         gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.3f, 0.4f, 0f);
         gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-0.3f, 0.4f, 0f);
         gl.glEnd();
         
       //left side of rectangle
         gl.glBegin(GL2.GL_QUADS);
         gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.3f, 0.2f, 1.3f);
         gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-0.3f, 0.4f, 1.3f);
         gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-0.3f, 0.4f, 0f);
         gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-0.3f, 0.2f, 0f);
         gl.glEnd();
         
       //right side of rectangle
         gl.glBegin(GL2.GL_QUADS);
         gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0.3f, 0.2f, 1.3f);
         gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0.3f, 0.4f, 1.3f);
         gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.3f, 0.4f, 0f);
         gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0.3f, 0.2f, 0f);
         gl.glEnd();
         
         //back of the rectangle
         gl.glBegin(GL2.GL_QUADS);
         gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.3f, 0.4f, 0f);
         gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-0.3f, 0.2f, 0f);
         gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.3f, 0.2f, 0f);
         gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0.3f, 0.4f, 0f);
         gl.glEnd();
         
       //front of the rectangle
         gl.glBegin(GL2.GL_QUADS);
         gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.3f, 0.4f, 1.3f);
         gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-0.3f, 0.2f, 1.3f);
         gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0.3f, 0.2f, 1.3f);
         gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0.3f, 0.4f, 1.3f);
         gl.glEnd();
         
 		gl.glPopMatrix();

    }
	
	public void shoot() {
		Tuple trajectory = new Tuple();
		Tuple laser = new Tuple();
		Tuple start = new Tuple();
//		CameraTracker bulletCam = new CameraTracker(camera[currentCamera]);

//		System.out.println("Pew pew");
		camera[currentCamera].position(laser);
		camera[currentCamera].position(start);
		camera[currentCamera].getVector(trajectory);
		trajectory.normalize();
		
		while(!track.checkShot(laser) && laser.distance(start) < lookAtRadius) {
			laser.add(trajectory);
		}

	}
	
	private void drawCrosshairs() {
		double length = 0.05;

		// for each axis, set up the material color to color them in, 
		//   draw a line along the axis from 0 to length
		
		// x axis: 
        float materialColor[] = {1, 0, 0, 1.0f};
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, materialColor, 0);
		gl.glBegin(GL.GL_LINES);
		gl.glColor4f(1, 0, 0, 1);
		gl.glVertex3d(-length, 0, 0);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(length, 0, 0);
		
		// y axis:
		materialColor[0] = 0;
        materialColor[1] = 1;
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, materialColor, 0);
		gl.glColor4f(1, 0, 0, 1);
		gl.glVertex3d(0, -length, 0);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(0, length, 0);
		
		// z axis: 
		materialColor[1] = 0;
        materialColor[2] = 1;
		gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, materialColor, 0);
		// gl.glColor4f(0, 0, 1, 1);
		gl.glVertex3d(0, 0, 0);
		gl.glVertex3d(0, 0, length);
		
		// end drawing
		gl.glEnd();
		
	}

	
	private class Node {
		public Tuple lookAt;
		public Tuple position;
		
		Node(CameraTracker camera) {
			lookAt = new Tuple();
			position = new Tuple();
			camera.position(position);
			camera.lookAt(lookAt);
		}
		
		public String toString() {
			return "Position: (" + position.x() + ", " + position.y() + ", "
					+ position.z() + ") -- Look at: (" + lookAt.x() + ", " +
					lookAt.y() + ", " + lookAt.z() + ") \n"; 
		}
	} // Node class

}
