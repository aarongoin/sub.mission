package entities;


import java.util.Stack;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import core.SubMission;
import jig.Entity;
import jig.Vector;
import util.VectorUtil;

public class Airplane {
	
	// Valid states:
	//		0: ready to go!
	//		1: takeoff
	//		2: deploying sonobuoys
	//		3: circling the map until sonobuoys disappear
	//		4: landing
	//		5: cooldown (fly off map and wait until called upon)
	int state;
	Vector airbase;
	Vector airstrip;
	float cooldown;
	Stack<Vector> toDeploy;
	Sonobuoy deployed[];
	int canDeploy;
	float speed;
	float radius;
	
	Vector position;
	Vector velocity;
		
	Image sprite;
	
	float countdown;

	public Airplane(float speed, float radius) {
		position = new Vector(950, 100);
		sprite = SubMission.getImage("airplane");
		sprite.rotate(180);
		
		state = 0;
		canDeploy = 4;
		toDeploy = new Stack<Vector>();
		deployed = new Sonobuoy[4];
		airbase = new Vector(950, 100);
		airstrip = new Vector(0, 1);
		
		this.speed = speed;
		this.radius = radius;
	}
	
	public boolean canDeploy() {
		return (state == 0);
	}
	
	public void turnToward(Vector target, float dt) {
		
		// figure out new Velocity turned towards target
		Vector directPath = target.subtract(position);
		float theta = VectorUtil.getAngleBetween(velocity, directPath);
		if (Math.abs(theta) > radius) {
			theta = radius * (theta < 0 ? -1 : 1);
			
		}
		theta *= dt;
		//System.out.println("theta: " + theta);
		velocity = velocity.rotate(theta);
		sprite.rotate(theta);
	}
	
	public void deployAt(Vector center, Vector direction) {
		float offset = 450;
		// generate deploy points
		for (int i = 0; i < canDeploy; i++) {
			toDeploy.push(center.add(direction.clampLength(offset, offset)));
			offset -= 300;
		}
		state = 1;
		velocity = airstrip.scale(speed);
	}
	
	public void deploySonobuoy() {
		int i = canDeploy - toDeploy.size();
		deployed[i] = new Sonobuoy(i, this, position, 150, 75);
		//deployed[i].debug = true;
		toDeploy.pop();
	}
	
	public void onDone(int id) {
		deployed[id] = null;
	}
	
	Vector circle() {
		Vector center = new Vector(SubMission.ScreenWidth / 2, SubMission.ScreenHeight / 2);
		Vector radius = position.subtract(center);
		int dist = (int) radius.length() - 400;
		//System.out.println("dist: " + dist + " turn toward: " + radius);
		if (Math.abs(dist) < 50) {
			radius = radius.getPerpendicular().scale(-1).rotate((dist > 0) ? 5 : -5);
		} else if (dist > 0) radius = radius.scale(-1);
		
		return position.add(radius.clampLength(10, 10));
	}
	
	public boolean update(float dt) {
		boolean detected = false;
		switch (state) {
		case 1: // takeoff
			//setRotation()
			if (position.distance(airbase) > 100) state = 2;
			else {
				position = position.add(velocity.scale(dt));
			}
			break;
		case 2: // deploy all sonobuoys
			if (toDeploy.isEmpty()) { // all deployed
				state = 3;
				countdown = 2;
			} else if (position.distance(toDeploy.peek()) < 5) { // can deploy
				deploySonobuoy();
			} else { // move towards next deployment spot
				turnToward(toDeploy.peek(), dt);
				position = position.add(velocity.scale(dt));
			}
			break;
		case 3: // circle the map
			turnToward(circle(), dt);
			position = position.add(velocity.scale(dt));
			countdown -= dt;
			if (countdown < 0) {
				countdown = 0;
				// update any remaining sonobuoys and count how many still live
				int d = 0;
				for (Sonobuoy s : deployed) {
					if (s == null) d++;
					else {
						if (s.update(dt)) {
							detected = true;
						}
					}
				}
				
				// transition to next state when all have died
				if (d == 4) {
					state = 4;
				}
			}
			break;
		case 4: // landing
			if (position.distance(airbase) < 5) {
				state = 5;
				cooldown = 120; // 2 minutes
			} else {
				// move back to airbase
				turnToward(airbase, dt);
				position = position.add(velocity.scale(dt));
			}
			break;
		case 5: // cooldown
			cooldown -= dt;
			if (cooldown < 0) {
				state = 0;
			}
			break;
		}
		return detected;
	}
	
	public void render(Graphics g) {
		float x = position.getX() - sprite.getWidth() / 2;
		float y = position.getY() - sprite.getHeight() / 2;
		if (state == 3  && countdown <= 0) {
			for (Sonobuoy s : deployed) {
				if (s != null) s.render(g);
			}
			Vector v = circle();
			g.drawLine(position.getX(), position.getY(), v.getX(), v.getY());
		}
		if (SubMission.player.getDepth() < 100 && state != 0) g.drawImage(sprite, x, y);
	}
}
