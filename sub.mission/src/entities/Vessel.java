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
import util.VectorUtil;

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
	
	String collideWith[];
	
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
	
	public VesselNavigator navi;
	
	public Vessel(String image, Vector p, float noise, float bearing, float speed, float radius, float accel) {
		super(p);
		//debug = true;
		
		id = Vessel.getID();
		
		baseSonar = 0;
		ambient = 0;
		actionNoise = 0;
		
		velocity = new Vector(0, 0);
		
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
		
		drawAlpha = 1f;
		
		destination = null;
		waypoint = null;
		
		setPosition(p);
		
		navi = new VesselNavigator(sprite.getWidth(), sprite.getHeight() * 3);
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
		Vector future = getFuture(lookahead);
		Vector land;
		for (int[] l : SubMission.landMasses) {
			land = new Vector(l[0], l[1]);
			if (Physics.didCollide(future, land, (float) radius, (float) l[2] )) {
				// adjust course
				//moveFor(land, l[2]);
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
	
	public boolean isDetected() {
		switch (SubMission.player.detect(this)) {
		case 0:
			drawAlpha = 0f;
			break;
		case 1:
			drawAlpha = 0.33f;
			break;
		case 2:
			drawAlpha = 0.6f;
			break;
		case 3:
			drawAlpha = 1f;
			return true;
		}
		return false;
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
	
	public float willCollideIn(Vessel other, float within) {
		float Dx = other.getPosition().getX() - getPosition().getX();
		float Dy = other.getPosition().getY() - getPosition().getY();
		
		System.out.println("DX: " + Dx);
		System.out.println("DY: " + Dy);
		
		float denom = other.getVelocity().getX()*velocity.getY() - velocity.getX()*other.getVelocity().getY();
		System.out.println("denom: " + denom);
		float Ta = ( other.getVelocity().getX()*Dy - Dx*other.getVelocity().getY() ) / denom;
		float Tb = ( velocity.getX()*Dy - Dx*velocity.getY() ) / denom;
		System.out.println("timeA: " + Ta);
		System.out.println("timeB: " + Tb);
		
		Vector Ca = getFuture(Ta);
		Vector Cb = other.getFuture(Tb);
		System.out.println("CA: " + Ca);
		System.out.println("CB: " + Cb);
		
		System.out.println("willCollide: " + (Ta > 0  && Ta <= within && Ca.distance(Cb) < Math.max(radius, other.getRadius())));
		return (Ta > 0  && Ta <= within && Ca.distance(Cb) < Math.max(radius, other.getRadius())) ? Ta : 0f;
	}
	
	public void fieldNav(String layers[]) {
		Vector target = (destination != null) ? destination.subtract( getPosition() ).clampLength(currentSpeed, currentSpeed) : velocity;
		Vessel other;
		Vector line;
		float distance;
		float theta;
		float perimeter = radius * currentSpeed;
		
		target = target.scale(2);
		
		for (int[] land : SubMission.landMasses) {
			line = getPosition().subtract(new Vector(land[0], land[1]));
			target = target.add( line.scale(1/2) );
		}
		
		for (String layer : layers) {
			for (Entity e : SubMission.getLayer(layer)) {
				other = (Vessel) e;
				if (other.id == id) continue;
				distance = other.getPosition().distance(getPosition());
				if (distance < perimeter) {
					target = target.add(getPosition().subtract(other.getPosition()).scale(10/distance));
				}	
			}
		}
		setWaypoint(getPosition().add(target.scale(10)));
	}
	
	public VesselNavigator getNav() {
		return navi;
	}
	
	public void betterSteering(String layers[]) {
		Vessel other;
		setWaypoint(destination);
		setSpeed(maxSpeed);
		// get closest potential collision
			for (String layer : layers) {
				for (Entity e : SubMission.getLayer(layer)) {
					other = (Vessel) e;
					if (other.id == id) continue;
					
					if ( navi.isBlockingLane(other.getNav()) && navi.shouldGiveWay(other.getNav()) ) {
						float theta = navi.turnToAvoid(other.getNav());
						if (theta == 0) setSpeed(0);
						else {
							setWaypoint( getPosition().add( new Vector(20, 0).setRotation(currentBearing + theta) ) );
							setSpeed(maxSpeed);
						}
					}
					
				}
			}
	}
	
	public void navigate(String layers[]) {
		Vector target = (destination != null) ? destination.subtract( getPosition() ) : velocity;
		Vessel other;
		Vessel toAvoid = null;
		float nearestTime = 100;
		float nearestTheta = 0;
		float time;
		float theta;
		
		// get closest potential collision
		for (String layer : layers) {
			for (Entity e : SubMission.getLayer(layer)) {
				other = (Vessel) e;
				if (other.id == id) continue;
				time = willCollideIn(other, lookahead);
				if (time > 0) {
					theta = VectorUtil.getAngleBetween(target, other.getVelocity());
					System.out.println("time: " + time);
					if ( !(theta > 0 && theta > 135) && (toAvoid == null || time < nearestTime) ) {
						toAvoid = other;
						nearestTime = time;
						nearestTheta = theta;
					}
				}
			}
		}
		if (toAvoid != null) {
			if (Math.abs(nearestTheta) > 135) { // handle potential head-on collision
				setWaypoint( getPosition().add( velocity.getPerpendicular().clampLength(toAvoid.getRadius() + radius, toAvoid.getRadius() + radius) ) );
			} else { // handle crossing collision
				setWaypoint( toAvoid.getFuture(toAvoid.getRadius()) );
			}
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
		float r = currentSpeed * radius;
		sprite.setAlpha(drawAlpha);
		sprite.setRotation(currentBearing + 90);
		if (debug) {
			/*float noise = getNoise();
			g.setColor(Color.red);
			g.drawOval(getPosition().getX() - noise, getPosition().getY() - noise, noise * 2, noise * 2);
			
			float sonar = getSonar();
			g.setColor(Color.green);
			g.drawOval(getPosition().getX() - sonar, getPosition().getY() - sonar, sonar * 2, sonar * 2);
			
			g.setColor(new Color(0.5f, 0.5f, 0.5f));
			if (waypoint != null) g.drawLine(waypoint.getX(), waypoint.getY(), getX(), getY());
			g.setColor(new Color(1f, 1f, 1f));
			if (destination != null) g.drawLine(destination.getX(), destination.getY(), getX(), getY());*/
			navi.getLane().render(g);
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
		setPosition( getFuture(dt) );
	}
	
	public boolean wasClicked(float x, float y) {
		return new Vector(x, y).distance(getPosition()) < 15;
	}
}
