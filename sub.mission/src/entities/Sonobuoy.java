package entities;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import core.SubMission;
import jig.Vector;

public class Sonobuoy {
	
	Vector position;
	float timer;
	Airplane owner;
	int id;
	float depth;
	Image current;
	Image above = SubMission.getImage("sonobuoy_above");
	Image even = SubMission.getImage("sonobuoy_even");
	Image below = SubMission.getImage("sonobuoy_below");
	boolean blink;
	float alpha;
	float sonar;
	Random rand;
	public boolean debug;

	public Sonobuoy(int id, Airplane owner, Vector position, float depth, float sonar) {
		this.position = position;
		timer = 30;
		this.owner = owner;
		this.id = id;
		this.depth = depth;
		setImage();
		alpha = 0.1f;
		blink = true;
		this.sonar = sonar;
		rand = new Random(System.currentTimeMillis());
	}
	
	public void setImage() {
		// update image based on player depth
		if (Math.abs(SubMission.player.getDepth() - depth) < 10 ) {
			current = even;
		} else if (SubMission.player.getDepth() < depth) {
			current = below;
		} else {
			current = above;
		}
	}
	
	public boolean update(float dt) {
		timer -= dt;
		if (timer < 0) {
			owner.onDone(id);
		}
		setImage();
		
		if (blink) {
			alpha *= 0.95f;
			if (alpha <= 0.1) blink = false;
		} else {
			alpha *= 1.15f;
			if (alpha >= 0.99) blink = true;
		}
		
		current.setAlpha(alpha);
		
		// try to detect player
		return detect();
	}
	
	public boolean detect() {

		float noise = SubMission.player.getNoise();
		float distance = position.distance(SubMission.player.getPosition());
		float span = sonar + noise;

		if (distance < span) {
			int random = rand.nextInt((int) (distance + noise));
			return (random <= sonar * 2 / 3);
				
		}
		return false;
	}
	
	public void render(Graphics g) {
		float x = position.getX() - current.getWidth() / 2;
		float y = position.getY() - current.getHeight() / 2;
		g.drawImage(current, x, y);
		
		if (debug) {
			g.setColor(Color.green);
			g.drawOval(position.getX() - sonar, position.getY() - sonar, sonar * 2, sonar * 2);
		}
	}
}
