package core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import entities.Vessel;
import jig.Entity;
import jig.Vector;

public class Towable {

	Entity sprite;
	int fullLength;
	List<Vector> line;
	int state;
	Vessel parent;
	float winchSpeed;
	
	public Towable(Vessel v, int l, float speed, String sprite) {
		
		line = new ArrayList<Vector>();
		parent = v;
		fullLength = l;
		state = 0;
		winchSpeed = speed;
		
		this.sprite = new Entity(0, 0);
		this.sprite.addImage( SubMission.getImage(sprite) );
		
	}
		
	public void deploy(boolean value) {
		state = value ? 2 : 3;
	}
	
	public void update(float dt) {
		
		Vector p = parent.getVelocity().scale(-dt);
		Vector m = p.clampLength(winchSpeed, winchSpeed);
		
		switch (state) {
		case 0: // fully stowed
			break;
		case 1: // fully extended
			// check for line-snapping condition
			if (m.lengthSquared() < p.lengthSquared()) {
				// TODO: snap the line
			} else {
				line.add(m);
				line.remove(0);
			}
			break;
		case 2: // extending
			line.add(m);
			if (line.size() > fullLength) {
				line.remove(0);
				state = 1;
			}
			break;
		case 3: // retracting
			line.add(m);
			if (line.size() > 0) {
				line.remove(0);
				line.remove(0);
			} else state = 0;
			break;
		}
		
	}
		
	public void render(Graphics g) {
		
		if (state == 0) return;
		
		int s = line.size();
		Vector a = parent.getTail().add( line.get(s) );
		Vector b;
		// draw line
		g.setColor(Color.gray);
		while (s-- > 0) {
			b = a.add( line.get(s) );
			g.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
			a = b;
		}
		
		// draw sprite
		float bearing = (float) line.get(s).getRotation();
		sprite.setPosition(a);
		sprite.rotate(bearing);
		sprite.render(g);
		
		if (state != 1) return;
		
		// TODO: draw active animation
		
	}
}
