package entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import core.SubMission;
import jig.Entity;
import jig.Vector;

public class Vessel extends Entity {
	
	static final String landColor = "Color (0.6,0.6,0.6,1.0)";
	
	protected Image sprite;
	
	protected Vector nose;
	
	public float drawAlpha;
	
	protected float currentSpeed;
	protected float targetSpeed;
	protected float pixelSpeed;
	
	protected float currentBearing;
	protected float targetBearing;
	
	float acceleration;
	float turnRadius;
	
	float baseNoise;
	
	public boolean debug;

	public Vessel(String image, Vector p, float noise, float bearing, float speed, float radius, float accel) {
		super(p);
		
		sprite = SubMission.getImage(image);
		addImageWithBoundingBox(sprite);
		
		currentBearing = bearing;
		targetBearing = bearing;
		currentSpeed = speed;
		targetSpeed = speed;
		
		acceleration = accel;
		turnRadius = radius;
		
		baseNoise = noise;
		setNose();
		
		drawAlpha = 1f;
	}
	
	public void debug(boolean value) {
		debug = value;
	}
	
	public void setImage(String i) {
		removeImage(sprite);
		sprite = SubMission.getImage(i);
		addImageWithBoundingBox(sprite);
	}
	
	public float getNoise() {
		return currentSpeed * baseNoise;
	}
	
	public float getSpeed() {
		return currentSpeed;
	}
	
	public void setSpeed(float s) {
		targetSpeed = s;
	}
	
	public float getBearing() {
		return currentBearing;
	}
	
	public void setBearing(float theta) {
		targetBearing = theta;
	}
	
	protected void setNose() {
		nose = getPosition().add(new Vector(0, -1).rotate(currentBearing).scale(sprite.getHeight() / 2));
	}
	
	public Vector getNose() {
		return nose;
	}
	
	public boolean didRunAground(Image map) {
		Color c = map.getColor((int) nose.getX(), (int) nose.getY());
		return c.toString() == landColor;
	}
	
	@Override
	public void render(Graphics g) {
		sprite.setAlpha(drawAlpha);
		if (debug) {
			float noise = getNoise();
			g.setColor(Color.red);
			g.drawOval(getPosition().getX() - noise, getPosition().getY() - noise, noise * 2, noise * 2);
			
			g.setColor(Color.white);
		}
		super.render(g);
	}
	
	public void update(float dt) {
	
		if (currentSpeed < targetSpeed) {
			currentSpeed += acceleration*dt;
			if (currentSpeed > targetSpeed)
				currentSpeed = targetSpeed;
		} else if (currentSpeed > targetSpeed) {
			currentSpeed -= acceleration*dt;
			if (currentSpeed < targetSpeed)
				currentSpeed = targetSpeed;
		}
		//System.out.println("targetBearing: " + targetBearing + " currentBearing: " + currentBearing);
		if (Math.abs(currentBearing - targetBearing) < 0.5)
			currentBearing = targetBearing;
		else {
			if (targetBearing > currentBearing || (currentBearing - targetBearing > 180))
				currentBearing += turnRadius*dt;
			else
				currentBearing -= turnRadius*dt;
		}
		
		setPosition(getPosition().add(new Vector(0, -1).rotate(currentBearing).scale(currentSpeed * 0.5144f * dt)));
		setRotation(currentBearing);
		setNose();
	}
}
