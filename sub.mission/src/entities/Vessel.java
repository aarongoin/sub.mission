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
	protected float maxSpeed;
	protected float pixelSpeed;
	
	protected float currentBearing;
	protected float targetBearing;
	
	protected float acceleration;
	protected float turnRadius;
	
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
		nose = getPosition().add(new Vector(1, 0).rotate(currentBearing).scale(sprite.getHeight() / 2));
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
		float d = Math.abs(currentBearing - targetBearing);
		if (d < 0.5 || d > 359.5) {
			//System.out.println("done turning.");
			currentBearing = targetBearing;
		} else {
			float turn = turnRadius*dt * 2 * currentSpeed / maxSpeed;
			if ((targetBearing > 0 && currentBearing > 0) || (targetBearing <= 0 && currentBearing <= 0)) {
				if (currentBearing > targetBearing) {
					//System.out.println("turning left...");
					currentBearing -= turn;
				} else {
					//System.out.println("turning right...");
					currentBearing += turn;
				}
			} else {
				d = targetBearing - currentBearing;
				if (d > 180 || d > -180) {
					//System.out.println("turning left...");
					currentBearing -= turn;
				} else {
					//System.out.println("turning right...");
					currentBearing += turn;
				}
			}
			
			if (currentBearing > 180)
				currentBearing -= 360;
			else if (currentBearing <= -180)
				currentBearing += 360;
			
		}
		
		setPosition(getPosition().add(new Vector(1, 0).rotate(currentBearing).scale(currentSpeed * 0.5144f * dt)));
		sprite.setRotation(currentBearing + 90);
		setNose();
	}
}
