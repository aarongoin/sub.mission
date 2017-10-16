package entities;

import java.util.HashMap;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import core.Physics;
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
	
	private static int nextID = 0;
	
	static int getID() {
		return nextID++;
	}
	protected float acceleration;
	float actionNoise;
	
	protected float ambient;
	
	protected int armor;
	float baseNoise;
	protected float baseSonar;
	protected float currentBearing;
	
	protected float currentDepth;
	protected float currentSpeed;
	
	public boolean debug;
	
	protected Vector destination;
	public float drawAlpha;
	
	public final int id;
	protected String layer;
	
	public float lookahead;
	
	protected float maxSpeed;
	
	protected HashMap<Vessel, Float> movedFor;
	protected Vector nose;
	
	protected float pixelSpeed;
	
	int radius;
	Random rand;
	protected Image sprite;
	
	protected Vector tail;
	protected float targetBearing;
	
	protected float targetSpeed;
	
	protected float turnRadius;
	
	
	protected Vector velocity;

	protected Vector waypoint;
	
	public Vessel(String image, Vector p, float noise, float bearing, float speed, float radius, float accel) {
		super(p);
		debug = true;
		
		id = Vessel.getID();
		
		baseSonar = 0;
		ambient = 0;
		actionNoise = 0;
		
		velocity = new Vector(0, 0);
		
		sprite = SubMission.getImage(image);
		addImageWithBoundingBox(sprite);
		
		this.radius = Math.max(sprite.getHeight(), sprite.getWidth()) / 2;
		
		rand = new Random(System.currentTimeMillis());
		
		currentDepth = 0;
		currentBearing = bearing;
		targetBearing = bearing;
		currentSpeed = speed;
		targetSpeed = speed;
		
		acceleration = accel;
		turnRadius = radius;
		
		baseNoise = noise;
		
		drawAlpha = 1f;
		
		destination = null;
		waypoint = null;
		
		setPosition(p);
	}
	
	public void adjustBearing(float dt) {
		
		// set target bearing based on waypoint or destination
		if (waypoint != null) {
			//System.out.println(waypoint);
			targetBearing = (float) waypoint.subtract(getPosition()).getRotation();
			if (getPosition().distance(waypoint) < 2) {
				waypoint = null;
			}
		} else if (destination != null) {
			targetBearing = (float) destination.subtract(getPosition()).getRotation();
			//System.out.println(currentBearing);
		}
		
		// calculate turn & which direction to turn
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
		}
		
		clampBearing();
	}
	
	public void adjustSpeed(float dt) {
		if (currentSpeed < targetSpeed) {
			currentSpeed += acceleration*dt;
			if (currentSpeed > targetSpeed)
				currentSpeed = targetSpeed;
		} else if (currentSpeed > targetSpeed) {
			currentSpeed -= acceleration*dt;
			if (currentSpeed < targetSpeed)
				currentSpeed = targetSpeed;
		}
	}
	
	public void avoidLand() {
		Vector future = getFuturePosition(lookahead);
		Vector land;
		for (int[] l : SubMission.landMasses) {
			land = new Vector(l[0], l[1]);
			if (Physics.didCollide(future, land, (float) radius, (float) l[2] )) {
				// adjust course
				moveFor(land, l[2]);
			}
		}
	}
	
	public void clampBearing() {
		if (currentBearing > 180)
			currentBearing -= 360;
		else if (currentBearing <= -180)
			currentBearing += 360;
	}
	
	public void debug(boolean value) {
		debug = value;
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
	
	public boolean didRunAground(Image map) {
		if (outOfBounds()) return false;
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
	
	public Vector getAsTarget() {
		return getPosition();
	}
	
	public float getBearing() {
		return currentBearing;
	}
	
	public Vector getDestination() {
		return destination;
	}
	
	public Vector getFuture(float dt) {
		return getPosition().add( getVelocity().scale(dt) );
	}
	
	public Vector getFuturePosition(float dt) {
		return getPosition().add( getVelocity().scale(dt) );
	}
		
	public float getNoise() {
		return currentSpeed * baseNoise + actionNoise;
	}
	
	public Vector getNose() {
		return nose;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public float getSonar() {
		return (175 * baseSonar - ambient - currentSpeed * 8);
	}
	
	public float getSpeed() {
		return currentSpeed;
	}
	
	public Vector getTail() {
		return tail;
	}
	
	public Vector getTurn(float dt) {
		return new Vector(
			(float) Math.cos(turnRadius*Math.PI*dt / 180),
			(float) Math.sin(turnRadius*Math.PI*dt / 180)
		);
	}
	
	public Vector getVelocity() {
		return velocity;
	}
	
	public void moveFor(Vector land, float radius) {
		Vector tangent = new Vector(1, 0).rotate(currentBearing).getPerpendicular().clampLength(radius, radius);
		Vector a = land.add(tangent);
		Vector b = land.add(tangent.scale(-1));
		
		if  ((destination != null) && (destination.distanceSquared(a) < destination.distanceSquared(b)) ) {
			setWaypoint(a);
		} else setWaypoint(b);
	}
	
	public void moveFor(Vessel other, Vector otherFuture, Vector myFuture) {
		Vector tangent = new Vector(1, 0).rotate(currentBearing).getPerpendicular();
		float d = otherFuture.distance(myFuture);
		Vector adjustment = other.getVelocity().scale(-1).project(tangent).clampLength(d, d);
		Vector waypoint = otherFuture.add(adjustment);
		setWaypoint(waypoint);

	}
	
	public void navigate(String layers[]) {
		Vessel other;
		Vessel toAvoid = null;
		Vector avoid = new Vector(0, 0);
		Vector me = avoid;
		Vector myFuture;
		Vector otherFuture;
		Vector position = getPosition();
		
		// get closest potential collision
		for (String layer : layers) {
			for (Entity e : SubMission.getLayer(layer)) {
				other = (Vessel) e;
				for (int i = 3; i > 0; i--) {
					myFuture = getFuturePosition(lookahead / i);
					otherFuture = other.getFuturePosition(lookahead / i);
					if ( Physics.didCollide(myFuture, otherFuture, radius, other.getRadius()) ) {
						if (toAvoid == null || position.distance(otherFuture) < position.distance(avoid) ) {
							toAvoid = other;
							avoid = otherFuture;
							me = myFuture;
							break;
						}
					}
				}
			}
		}
		if (toAvoid != null) {
			moveFor(toAvoid, avoid, me);
		} else {
			setWaypoint(destination);
		}
	}
	
	public boolean outOfBounds() {
		Vector p = getNose();
		return (		p.getX() < 0 || p.getX() > SubMission.ScreenWidth
				||	p.getY() < 0 || p.getY() > SubMission.ScreenHeight
		);
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
			Vector fp = getFuturePosition(lookahead);
			g.drawOval(fp.getX() - radius, fp.getY() - radius, radius*2, radius*2);
		}
		super.render(g);
	}
	
	public void setBearing(float theta) {
		targetBearing = theta;
	}
	
	public void setDestination(Vector d) {
		destination = d;
		waypoint = null;
	}
	
	public void setImage(String i) {
		removeImage(sprite);
		sprite = SubMission.getImage(i);
		addImageWithBoundingBox(sprite);
	}
	
	@Override
	public void setPosition(Vector p) {
		super.setPosition(p);
		nose = p.add(new Vector(1, 0).rotate(currentBearing).scale(sprite.getHeight() / 2));
		tail = p.add(new Vector(1, 0).rotate(currentBearing).scale(sprite.getHeight() / (- 2)));
	}
	
	public void setSpeed(float s) {
		targetSpeed = s;
	}
	
	public void setWaypoint() {
		waypoint = null;
	}
	
	public void setWaypoint(Vector w) {
		//System.out.println("setting waypoint: " + w.subtract(getPosition()));
		//System.out.println("currentBearing: " + currentBearing + " targetBearing: " + w.subtract(getPosition()).getRotation());
		waypoint = w;
	}
	
	public void sink() {
		SubMission.removeEntity(layer, (Entity) this);
	}
	
	public void takeDamage(String source) {
		armor -= 1;
		if (armor <= 0) sink();	
	}
	
	public void update(float dt) {
		
		adjustSpeed(dt);
		adjustBearing(dt);
		
		velocity = new Vector(1, 0).rotate(currentBearing).scale(currentSpeed * 0.5144f);
		setPosition( getFuturePosition(dt) );
	}
	
	public boolean wasClicked(float x, float y) {
		return new Vector(x, y).distance(getPosition()) < getRadius();
	}
}
