package core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import entities.MilitaryVessel;
import entities.Vessel;
import jig.Entity;
import jig.Vector;

public class Towable {

	Entity sprite;
	Image waves;
	int fullLength;
	Vector position;
	List<Vector> line;
	int state;
	MilitaryVessel parent;
	float winchSpeed;
	float alpha;
	int id;
	boolean waveOn;
	int maxSpeed;
	
	public Towable(MilitaryVessel v, int l, float speed, String sprite, String active, int id, int spd) {
		
		this.id = id;
		line = new ArrayList<Vector>();
		parent = v;
		fullLength = l;
		state = 0;
		winchSpeed = speed;
		
		this.sprite = new Entity(0, 0);
		this.sprite.addImage( SubMission.getImage(sprite) );
		waves = SubMission.getImage(active);
		
		alpha = 1f;
		position = v.getTail();
		
		waves.setAlpha(0.1f);
		waveOn = true;
		
		maxSpeed = spd;
	}
	
	public int getState() {
		return state;
	}
	
	public void reset(MilitaryVessel v) {
		state = 0;
		parent = v;
		alpha = 1f;
		line = new ArrayList<Vector>();
	}
		
	public void deploy(boolean value) {
		state = value ? 2 : 3;
	}
	
	public void update(float dt) {
		
		Vector p = new Vector(0, 0);
		if (parent != null) {
			p = parent.getVelocity().scale(-dt);
		
			if (parent.getSpeed() == 0 && ( state == 1 || state == 2 ))
				state = 3;
		}
		Vector m = p.clampLength(winchSpeed, winchSpeed);

		switch (state) {
		case 0: // fully stowed
			if (line.size() > 0)
				line = new ArrayList<Vector>();
			break;
		case 1: // fully extended
			// check for line-snapping condition
			if (parent.getSpeed() > maxSpeed) {
				state = 4;
			} else {
				line.add(m);
				line.remove(0);
				position = parent.getTail();
			}
			break;
		case 2: // extending
			line.add(m);
			if (line.size() > fullLength) {
				line.remove(0);
				state = 1;
			}
			position = parent.getTail();
			break;
		case 3: // retracting
			if (line.size() > 0) {
				line.add(m);
				line.remove(0);
				line.remove(0);
			} else state = 0;
			position = parent.getTail();
			break;
		case 4: // snapped
			alpha *= 0.99;
			if (alpha < 0.1 && parent != null) {
				parent.cableSnapped(id);
			}
			break;
		}
		
		if (waveOn) {
			waves.setAlpha(waves.getAlpha() * 0.95f);
			if (waves.getAlpha() <= 0.1) waveOn = false;
		} else {
			waves.setAlpha(waves.getAlpha() * 1.1f);
			if (waves.getAlpha() >= 0.7) waveOn = true;
		}
	}
	
	public void detach() {
		parent = null;
	}
		
	public void render(Graphics g) {
		
		int s = line.size();
		if (state == 0 || s == 0) return;
		
		Vector a = position;
		Vector b;
		float avg = 0;
		// draw line
		g.setColor(new Color(0.3f, 0.3f, 0.3f, alpha));
		while (s-- > 0) {
			b = a.add( line.get(s) );
			g.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
			a = b;
			if (s < 10)
				avg += line.get(s).getRotation() / 10;
		}
		
		// draw sprite
		float bearing = avg;
		sprite.setPosition(a);
		sprite.setRotation(bearing);
		sprite.render(g);
		
		if (state != 1) return;
		
		g.drawImage(waves, a.getX() - waves.getWidth() / 2, a.getY() - waves.getHeight() / 2);
		
	}
}
