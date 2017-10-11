package entities;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import core.SubMission;
import jig.Entity;
import jig.Vector;

public class Torpedo extends Vessel {
	
	float maxSpeed;
	List<Vector> line;

	public Torpedo(String image, Vector p, float bearing, float speed, Vector dest) {
		super(image, p, 20, bearing, 10, 20, 4);
		
		maxSpeed = speed;
		line = new ArrayList<Vector>();
		currentDepth = 10;
		
		setDestination(dest);
	}
	
	@Override
	public void update(float dt) {
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
				
		super.render(g);
	}
}
