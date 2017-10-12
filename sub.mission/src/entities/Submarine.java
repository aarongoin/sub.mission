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

	float targetDepth;
	float diveSpeed;

	float hoverBearing;
	Vector bearingOffset;

	Sound fireTorpedo;
	
	public boolean isSunk;

	public Submarine(float depth, float dive) {
		super("sub0", new Vector(SubMission.ScreenWidth - 700f, 300f), 1.5f, 4, 0, 10, 10, 2);

		lookahead = 0f;

		hoverBearing = targetBearing;
		currentDepth = depth;
		targetDepth = depth;
		diveSpeed = dive;
		maxSpeed = 45;

		fireTorpedo = SubMission.getSound("fire_torpedo");
		bearing = SubMission.getImage("bearing_target");
		bearingOffset = new Vector(0, -1).scale(30).setRotation(hoverBearing);
		setDestination(null);

		setArsenal(8, 4, true);
		
		armor = 2;
		isSunk = false;

	}

	public float getDepth() {
		return currentDepth;
	}

	public void setDepth(float d) {
		targetDepth = d;
	}

	@Override
	public float getSonar() {
		return super.getSonar() - currentDepth;
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

	@Override
	public float getNoise() {
		if (currentDepth < 100)
			return super.getNoise() * (24 + (12 * (0 - currentDepth) / 100));
		else if (currentDepth < 200)
			return super.getNoise() * (12 + (6 * (100 - currentDepth) / 100));
		else if (currentDepth < 300)
			return super.getNoise() * (6 + (3 * (200 - currentDepth) / 100));
		else if (currentDepth < 400)
			return super.getNoise() * (3 + (2 * (300 - currentDepth) / 100));
		else if (currentDepth < 500)
			return super.getNoise() * (1 + (1 * (400 - currentDepth) / 100));
		else
			return 0f;
	}

	public float getNoise(float depth) {
		if (currentDepth < 100)
			return super.getNoise() * (24 + (12 * (0 - currentDepth) / 100));
		else if (currentDepth < 200)
			return super.getNoise() * (14 + (7 * (100 - currentDepth) / 100));
		else if (currentDepth < 300)
			return super.getNoise() * (20 + (10 * (200 - currentDepth) / 100));
		else if (currentDepth < 400)
			return super.getNoise() * (24 + (12 * (300 - currentDepth) / 100));
		else if (currentDepth < 500)
			return super.getNoise() * (38 + (19 * (400 - currentDepth) / 100));
		else if (currentDepth < 600)
			return super.getNoise() * (30 + (15 * (500 - currentDepth) / 100));
		else if (currentDepth < 700)
			return super.getNoise() * (22 + (11 * (600 - currentDepth) / 100));
		else
			return super.getNoise() * (20 + (10 * (700 - currentDepth) / 100));
	}

	@Override
	public void sink() {
		isSunk = true;
	}
	
	@Override
	public void render(Graphics g) {
		super.render(g);
		g.drawImage(bearing, getPosition().getX() + bearingOffset.getX() - 4,
				getPosition().getY() + bearingOffset.getY() - 4);
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

		super.update(dt, ambient);
	}
}
