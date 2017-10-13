package entities;

import java.util.HashMap;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import core.SubMission;
import jig.Entity;
import jig.Vector;

public class Vessel extends Entity {
	
	static final String d100Color = "Color (0.0,0.0,0.6901961,1.0)";
	static final String d200Color = "Color (0.0,0.0,0.6117647,1.0)";
	static final String d300Color = "Color (0.0,0.0,0.5294118,1.0)";
	static final String d400Color = "Color (0.0,0.0,0.43137255,1.0)";
	static final String d500Color = "Color (0.0,0.0,0.3372549,1.0)";
	static final String d600Color = "Color (0.0,0.0,0.22352941,1.0)";
	static final String d700Color = "Color (0.0,0.0,0.11372549,1.0)";
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
	
	protected float currentDepth;
	
	protected float acceleration;
	protected float turnRadius;
	
	float baseNoise;
	float actionNoise;
	
	int radius;
	
	public boolean debug;
	
	protected Vector destination;
	protected Vector waypoint;
	
	public float lookahead;
	
	protected float ambient;
	protected float baseSonar;
	Random rand;
	
	protected int armor;
	protected String layer;
	
	
	protected HashMap<Vessel, Float> movedFor;

	public Vessel(String image, Vector p, float noise, float bearing, float speed, float radius, float accel) {
		super(p);
		
		baseSonar = 0;
		ambient = 0;
		actionNoise = 0;
		
		sprite = SubMission.getImage(image);
		addImageWithBoundingBox(sprite);
		
		this.radius = Math.max(sprite.getHeight(), sprite.getWidth()) / 4;
		
		rand = new Random(System.currentTimeMillis());
		
		currentDepth = 0;
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
		return currentSpeed * baseNoise + actionNoise;
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
		//System.out.println("x: " + (int) nose.getX() + " y: " + (int) nose.getY());
		Color c = map.getColor((int) nose.getX(), (int) nose.getY());
		switch(c.toString()) {
		case landColor:
			return true;
		case d100Color:
			return (currentDepth >= 100);
		case d200Color:
			return (currentDepth >= 200);
		case d300Color:
			return (currentDepth >= 300);
		case d400Color:
			return (currentDepth >= 400);
		case d500Color:
			return (currentDepth >= 500);
		case d600Color:
			return (currentDepth >= 600);
		case d700Color:
			return (currentDepth >= 700);
		default:
			return false;
		}
	}
	
	@Override
	public void render(Graphics g) {
		sprite.setAlpha(drawAlpha);
		sprite.setRotation(currentBearing + 90);
		if (debug) {
			float noise = getNoise();
			g.setColor(Color.red);
			g.drawOval(getPosition().getX() - noise, getPosition().getY() - noise, noise * 2, noise * 2);
			
			float sonar = getSonar();
			g.setColor(Color.green);
			g.drawOval(getPosition().getX() - sonar, getPosition().getY() - sonar, sonar * 2, sonar * 2);
			
			g.setColor(Color.white);
			g.drawOval(getX() - radius, getY() - radius, radius*2, radius*2);
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
			if (getPosition().distance(waypoint) < 5) {
				waypoint = null;
			}
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
	
	public Vector getAsTarget() {
		return getPosition();
	}
	
	public boolean wasClicked(float x, float y) {
		return new Vector(x, y).distance(getPosition()) < getRadius();
	}
	
	public float getSonar() {
		return (175 * baseSonar - ambient - currentSpeed * 8);
	}
	
	public void takeDamage(String source) {
		armor -= 1;
		if (armor <= 0) sink();	
	}
	
	public void sink() {
		SubMission.removeEntity(layer, (Entity) this);
	}
	
	public int detect(Vessel other) {
	
		float theta = Math.abs((float) (getNose().subtract(getPosition()).getRotation() - other.getPosition().subtract(getPosition()).getRotation()));
		if (theta > 157.5 && theta < 202.5)
			return 0;

		float distance = getPosition().distance(other.getPosition());
		float sonar = getSonar();
		float span = sonar + other.getNoise();

		if (distance < span) {
			int random = rand.nextInt((int) (distance + other.getNoise()));
			if (random <= sonar * 2 / 3)
				return 3;
			else if (random <= sonar)
				return 2;
			else
				return 1;
		}
		
		return 0;
	}
}
