package entities;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;

import core.SubMission;
import jig.Entity;
import jig.Vector;

public class Torpedo extends Vessel {
	
	float fuel;
	float maxSpeed;
	List<Vector> line;
	Vessel target;
	Sound explosion;

	public Torpedo(String image, Vector p, float bearing, float speed, float f, Vector dest, Vessel t) {
		super(image, p, 20, bearing, 10, 20, 20);
		
		maxSpeed = speed;
		line = new ArrayList<Vector>();
		currentDepth = 10;
		fuel = f;
		target = t;
		baseSonar = 1;
		
		explosion = SubMission.getSound("torpedo_explosion");
		layer = "torpedo";
		
		setDestination(dest);
	}

	public boolean haveFuel() {
		return fuel > 0;
	}
	
	@Override
	public void update(float dt) {
		
		fuel -= dt;
		if (detect(target) > 2) {
			//System.out.println("Detected target at " + target.getAsTarget().distance(getPosition()));
			if ( target.getAsTarget().distance(getPosition()) < 10 )
				explode();
			else {
				setDestination(target.getAsTarget());
				
			}
		}
		
		line.add(getVelocity().scale(-dt));
		if (line.size() > 50)
			line.remove(0);
		
		if (getSpeed() < maxSpeed) {
			setSpeed(getSpeed() * 1.25f);
		}
		if (getSpeed() > maxSpeed)
			setSpeed(maxSpeed);
		
		super.update(dt);
	}
	
	void explode() {
		explosion.play();
		target.takeDamage();
		SubMission.removeEntity(layer, (Entity) this);
	}
	
	@Override
	public float getSonar() {
		return (175 * baseSonar - ambient - currentSpeed * 2);
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
}
