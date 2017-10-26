package entities;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;

import core.SubMission;
import jig.Entity;
import jig.Vector;
import util.VectorZ;

public class Torpedo extends Vessel {
	
	public int owner;
	Sound explosion;
	float fuel;
	List<Vector> line;
	float maxSpeed;
	Vessel target;
	VectorZ velocity3d;

	public Torpedo(int id, String image, Vector p, float depth, float bearing, float speed, float f, Vector dest, Vessel t) {
		super(image, p, 20, bearing, 10, 30, 20);
		
		maxSpeed = speed;
		line = new ArrayList<Vector>();
		currentDepth = depth;
		fuel = f;
		target = t;
		baseSonar = 1;
		diveSpeed = speed;
		explosion = SubMission.getSound("torpedo_explosion");
		layer = "torpedo";
		targetSpeed = speed;
		owner = id;
		velocity3d = new VectorZ(0, 0, 0);
		velocity3d.pointTo(target.getAsTargetZ(), currentSpeed);
		targetDepth = target.getDepth();
		
		setDestination(dest);
		
	}

	void explode() {
		explosion.play();
		target.takeDamage("torpedo");
		SubMission.removeEntity(layer, (Entity) this);
	}
	
	@Override
	public float getSonar() {
		return (175 * baseSonar - ambient - currentSpeed * 2);
	}
	
	public boolean haveFuel() {
		return fuel > 0;
	}
	
	@Override
	public void render(Graphics g) {
		// draw line
		g.setColor(new Color(0.8f, 0.8f, 0.8f, 0.3f));
		int s = line.size();
		Vector a = getPosition();
		Vector b;
		while (s-- > 0) {
			b = a.add( line.get(s) );
			g.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
			a = b;
		}
		
		if (debug) g.drawLine(destination.getX(), destination.getY(), getX(), getY());
				
		super.render(g);
	}
	
	@Override
	public void update(float dt) {

		fuel -= dt;
		
		// Use sonar to try and update target position
		if (detect(target) > 2) {
			//System.out.println("Detected target at " + target.getAsTarget().distance(getPosition()));
			if ( target.getAsTargetZ().distance(getAsTargetZ()) < 15 )
				explode();
			else {
				setDestination(target.getAsTarget());
				targetDepth = target.getDepth();
			}
		}
		
		// update trail
		line.add(getVelocity().scale(-dt));
		if (line.size() > 50)
			line.remove(0);
		
		adjustSpeed(dt);
		adjustBearing(dt);
		
		// calculate new 3d velocity
		velocity3d.pointTo(new VectorZ(new Vector(currentSpeed, 0).rotate(currentBearing), targetDepth - currentDepth), currentSpeed);
		
		//System.out.println("Torpeod v3d: <" + velocity3d.getX() + ", " + velocity3d.getY() + ", " + velocity3d.getZ() + ">");
		
		velocity = velocity3d.getVectorXY();
		setPosition( getFuture(dt) );
		currentDepth += velocity3d.getZ() * dt;
		//System.out.println("Torpedo target depth: " + targetDepth + " Current depth: " + currentDepth);
	}
}
