package entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Sound;

import core.SubMission;
import jig.Vector;

public class Submarine extends MilitaryVessel {

	Image bearing;

	Vector bearingOffset;
	float crushDepth = 700;
	
	float crushSpeed = 40;
	float dangerDepth = 600;
	
	float dangerSpeed = 35;
	float diveSpeed;

	Sound explosionA;
	Sound explosionB;

	Sound fireTorpedo;
	float hoverBearing;
	public boolean isSunk;
	
	Vessel target;
	
	float targetDepth;
	
	Image targetLock;

	public Submarine(float depth, float dive) {
		super("sub0", new Vector(SubMission.ScreenWidth - 100f, 100f), 1.5f, 6, 180, 10, 20, 2);
		//debug = true;
		lookahead = 0f;

		hoverBearing = targetBearing;
		currentDepth = 100;
		targetDepth = 100;
		diveSpeed = dive;
		maxSpeed = 45;

		torpedoType = "sub_torpedo";
		
		bearing = SubMission.getImage("bearing_target");
		bearingOffset = new Vector(0, -1).scale(30).setRotation(hoverBearing);
		setDestination(null);

		setArsenal(8, 4, true);
		
		armor = 2;
		isSunk = false;
		
		target = null;
		targetLock = SubMission.getImage("target_lock");
		
		fireTorpedo = SubMission.getSound("fire_torpedo");
		explosionA = SubMission.getSound("explosion_a");
		explosionB = SubMission.getSound("explosion_b");
	}

	@Override
	public int detect(Vessel other) {

		if (towedSonar == null || towedSonar.getState() != 1) {
			float theta = Math.abs((float) (getNose().subtract(getPosition()).getRotation()
					- other.getPosition().subtract(getPosition()).getRotation()));
			if (theta > 157.5 && theta < 202.5)
				return 0;
		}
		float distance = getPosition().distance(other.getPosition());
		float sonar = getSonar();
		if (distance <= sonar / 2)
			return 3;
		else if (distance <= sonar)
			return 2;
		else if (distance <= sonar + other.getNoise())
			return 1;

		return 0;
	}

	@Override
	public Torpedo fireTorpedo(Vessel v) {
		Torpedo t = super.fireTorpedo(v);
		if (t != null)
			fireTorpedo.play();
		return t;
	}
	
	public float getArmor() {
		return armor;
	}

	public float getDepth() {
		return currentDepth;
	}

	public void getLock(Vessel v) {
		target = v;
		targetLock.rotate(3);
	}

	@Override
	public float getNoise() {
		float noise = 0;
		if (currentDepth < 100)
			noise = super.getNoise() * (24 + (12 * (0 - currentDepth) / 100));
		else if (currentDepth < 200)
			noise = super.getNoise() * (12 + (6 * (100 - currentDepth) / 100));
		else if (currentDepth < 300)
			noise = super.getNoise() * (6 + (3 * (200 - currentDepth) / 100));
		else if (currentDepth < 400)
			noise = super.getNoise() * (3 + (2 * (300 - currentDepth) / 100));
		else if (currentDepth < 500)
			noise = super.getNoise() * (1 + (1 * (400 - currentDepth) / 100));
		
		noise *= (currentSpeed > 20 ? 1f : 0.5f);
		//System.out.println("Sub noise: " + noise);
		return noise;
	}
	
	public float getNoise(float depth) {
		float noise = 0;
		if (currentDepth < 100)
			noise = super.getNoise() * (24 + (12 * (0 - currentDepth) / 100));
		else if (currentDepth < 200)
			noise = super.getNoise() * (14 + (7 * (100 - currentDepth) / 100));
		else if (currentDepth < 300)
			noise = super.getNoise() * (20 + (10 * (200 - currentDepth) / 100));
		else if (currentDepth < 400)
			noise = super.getNoise() * (24 + (12 * (300 - currentDepth) / 100));
		else if (currentDepth < 500)
			noise = super.getNoise() * (38 + (19 * (400 - currentDepth) / 100));
		else if (currentDepth < 600)
			noise = super.getNoise() * (30 + (15 * (500 - currentDepth) / 100));
		else if (currentDepth < 700)
			noise = super.getNoise() * (22 + (11 * (600 - currentDepth) / 100));
		else
			noise = super.getNoise() * (20 + (10 * (700 - currentDepth) / 100));
		
		noise *= (currentSpeed > 20 ? 1.5f : 1);
		// System.out.println("Sub noise: " + noise);
		return noise;
	}

	
	@Override
	public float getSonar() {
		return super.getSonar() - currentDepth;
	}
	
	public void lockOn() {
		if (targetLock.getRotation() > -0.01 && targetLock.getRotation() < 0.01) {
			SubMission.addEntity("torpedo", fireTorpedo(target));
			target = null;
			targetLock.setRotation(0);
		} else {
			targetLock.rotate(3);
		}
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		g.drawImage(bearing, getPosition().getX() + bearingOffset.getX() - 4,
				getPosition().getY() + bearingOffset.getY() - 4);
		
		if (target != null)
			g.drawImage(targetLock, target.getX() - targetLock.getWidth() / 2, target.getY() - targetLock.getWidth() / 2);
	}

	public void setDepth(float d) {
		targetDepth = d;
	}
	
	@Override
	public void sink() {
		isSunk = true;
	}
	
	@Override
	public void takeDamage(String source) {
		super.takeDamage(source);
		if (source == "torpedo") {
			explosionA.play();
			explosionB.play();
		}
	}

	public void update(Input input, float ambient, float dt) {

		Vector mouse = new Vector(input.getMouseX(), input.getMouseY()).subtract(getPosition());

		if (mouse.lengthSquared() < 2000) {
			// System.out.println("mouse: " + mouse + " length: " + mouse.length() + "
			// bearing: " + mouse.getRotation());
			hoverBearing = (float) mouse.getRotation();
			// System.out.println("mouse: " + hoverBearing);
			if (hoverBearing > 180)
				hoverBearing -= 360;
			else if (hoverBearing <= -180)
				hoverBearing += 360;
			// if (hoverBearing < 0)
			// hoverBearing = 360 + hoverBearing;
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
				targetBearing = hoverBearing;
		} else {
			hoverBearing = targetBearing;
		}
		// System.out.println("current: " + currentBearing);
		// System.out.println("hoverBearing: " + hoverBearing);
		bearing.setRotation(hoverBearing - 90);
		bearingOffset = bearingOffset.setRotation(hoverBearing);

		if (currentDepth < targetDepth) {
			currentDepth += diveSpeed * dt;
			if (currentDepth > targetDepth)
				currentDepth = targetDepth;
		} else if (currentDepth > targetDepth) {
			currentDepth -= diveSpeed * dt;
			if (currentDepth < targetDepth)
				currentDepth = targetDepth;
		}
		
		if  (	(currentDepth >  crushDepth && rand.nextInt(1500) == 0) 
			 || (currentDepth > dangerDepth && rand.nextInt(2000) == 0)
			) {
			takeDamage("depth");
		}
		
		if  (	(currentSpeed > crushSpeed && rand.nextInt(1500) == 0) 
				 || (currentSpeed > dangerSpeed && rand.nextInt(2000) == 0)
				) {
				takeDamage("speed");
			}
		
		if (target != null) lockOn();

		super.update(dt, ambient);
	}
}
