package entities;

import java.util.HashMap;

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
	
	int radius;
	
	public boolean debug;
	
	protected Vector destination;
	protected Vector waypoint;
	
	public float lookahead;
	
	protected HashMap<Vessel, Float> movedFor;

	public Vessel(String image, Vector p, float noise, float bearing, float speed, float radius, float accel) {
		super(p);
		
		sprite = SubMission.getImage(image);
		addImageWithBoundingBox(sprite);
		
		this.radius = Math.max(sprite.getHeight(), sprite.getWidth());
		
		currentBearing = bearing;
		targetBearing = bearing;
		currentSpeed = speed;
		targetSpeed = speed;
		
		acceleration = accel;
		turnRadius = radius;
		
		baseNoise = noise;
		setNose();
		
		drawAlpha = 1f;
		
		destination = null;
		waypoint = null;
	}
	
	public void debug(boolean value) {
		debug = value;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public Vector getDestination() {
		return destination;
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
	
	public Vector getTail() {
		return getPosition().add(new Vector(1, 0).rotate(currentBearing).scale(sprite.getHeight() / (- 2)));
	}
	
	public Vector getVelocity() {
		return new Vector(1, 0).rotate(currentBearing).scale(currentSpeed * 0.5144f);
	}
	
	public boolean didRunAground(Image map) {
		Color c = map.getColor((int) nose.getX(), (int) nose.getY());
		return c.toString() == landColor;
	}
	
	@Override
	public void render(Graphics g) {
		sprite.setAlpha(drawAlpha);
		sprite.setRotation(currentBearing + 90);
		if (debug) {
			float noise = getNoise();
			g.setColor(Color.red);
			g.drawOval(getPosition().getX() - noise, getPosition().getY() - noise, noise * 2, noise * 2);
			
			g.setColor(Color.white);
			g.drawOval(getFuturePosition(lookahead).getX() - radius, getFuturePosition(lookahead).getY() - radius, radius*2, radius*2);
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
		
		if (waypoint != null) {
			//System.out.println(waypoint);
			targetBearing = (float) waypoint.subtract(getPosition()).getRotation();
		} else if (destination != null) {
			targetBearing = (float) destination.subtract(getPosition()).getRotation();
			//System.out.println(currentBearing);
		}
		
		//System.out.println("targetBearing: " + targetBearing + " currentBearing: " + currentBearing);
		float d = Math.abs(currentBearing - targetBearing);
		if (d < 0.5 || d > 359.5) {
			//System.out.println("done turning.");
			currentBearing = targetBearing;
		} else {
			float turn = turnRadius*dt;
			float cb;
			float tb;
			if (currentBearing <= 0 && targetBearing > 0) {
				tb = targetBearing;
				cb = currentBearing + 360;
			} else if (currentBearing > 0 && targetBearing <= 0) {
				cb = currentBearing + Math.abs(targetBearing);
				tb = 0;
			} else {
				cb = currentBearing;
				tb = targetBearing;
			}
			
			//System.out.println("tb: " + tb + " cb: " + cb);
			
			if (cb > tb && cb - tb < 180) {
				//System.out.println("turning left...");
				currentBearing -= turn;
			} else {
				//System.out.println("turning right...");
				currentBearing += turn;
			}
			
			if (currentBearing > 180)
				currentBearing -= 360;
			else if (currentBearing <= -180)
				currentBearing += 360;
			
		}
		
		//System.out.println("currentBearing: " + currentBearing);
		setPosition( getFuturePosition(dt) );
		//System.out.println(getPosition());
		//System.out.println();
		setNose();
	}
	
	public void setDestination(Vector d) {
		destination = d;
		waypoint = d;
	}
	
	public void setWaypoint() {
		waypoint = null;
	}
	
	public void setWaypoint(Vector w) {
		//System.out.println("setting waypoint: " + w.subtract(getPosition()));
		//System.out.println("currentBearing: " + currentBearing + " targetBearing: " + w.subtract(getPosition()).getRotation());
		waypoint = w;
	}
	
	public void moveFor(Vector other, float r) {
		//Vector w = other.add(new Vector(1, 0).rotate(currentBearing).getPerpendicular().scale(r));
		//setWaypoint(w);
		
	}
	
	public void moveFor(Vessel other) {
		Float bearing = movedFor.get(other);
		if (bearing != null && bearing == other.getBearing()) return;
		moveFor(other.getPosition(), other.getRadius() * 2);
		movedFor.put(other, other.getBearing());
	}
	
	public Vector getFuturePosition(float dt) {
		Vector t = getPosition().add( getVelocity().scale(dt) );
		//System.out.println(t + " " + dt + " " + currentBearing);
		return t;
	}
}
