package entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;

import core.SubMission;
import jig.Vector;

public class Submarine extends MilitaryVessel {

	static final String d100Color = "Color (0.0,0.0,0.6901961,1.0)";
	static final String d200Color = "Color (0.0,0.0,0.6117647,1.0)";
	static final String d300Color = "Color (0.0,0.0,0.5294118,1.0)";
	static final String d400Color = "Color (0.0,0.0,0.43137255,1.0)";
	static final String d500Color = "Color (0.0,0.0,0.3372549,1.0)";
	static final String d600Color = "Color (0.0,0.0,0.22352941,1.0)";
	static final String d700Color = "Color (0.0,0.0,0.11372549,1.0)";
	
	Image bearing;
	
	float currentDepth;
	float targetDepth;
	float diveSpeed;
	
	float hoverBearing;
	Vector bearingOffset;
	
	int torpedoes;
	int decoys;

	public Submarine(float depth, float dive) {
		super("sub0", new Vector(SubMission.ScreenWidth - 700f, 300f), 1.5f, 4, 180, 10, 10, 2);
		
		lookahead = 0f;
		
		hoverBearing = targetBearing;
		currentDepth = depth;
		targetDepth = depth;
		diveSpeed = dive;
		maxSpeed = 45;
		
		bearing = SubMission.getImage("bearing_target");
		bearingOffset = new Vector(0, -1).scale(30).setRotation(hoverBearing);
		setDestination(null);
		
		torpedoes = 5;
		decoys = 3;
	}
	
	public int getTorpedoes() {
		return torpedoes;
	}
	
	public int getDecoys() {
		return decoys;
	}
	
	public float getDepth() {
		return currentDepth;
	}
	
	public void setDepth(float d) {
		targetDepth = d;
	}
	
	@Override
	public float getSonar() {
		return (175 * baseSonar - ambient - currentSpeed * 8 - currentDepth);
	}
	
	@Override
	public int detect(Vessel other) {
		
		float theta = Math.abs((float) (getNose().subtract(getPosition()).getRotation() - other.getPosition().subtract(getPosition()).getRotation()));
		if (theta > 157.5 && theta < 202.5)
			return 0;
		
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
	public void render(Graphics g) {
		super.render(g);
		g.drawImage(bearing, getPosition().getX() + bearingOffset.getX() - 4, getPosition().getY() + bearingOffset.getY() - 4);
	}
	
	public void update(Input input, float ambient, float dt) {
		
		Vector mouse = new Vector( input.getMouseX(), input.getMouseY() ).subtract( getPosition() );
		
		if (mouse.lengthSquared() < 2000) {
			//System.out.println("mouse: " + mouse + " length: " + mouse.length() + " bearing: " + mouse.getRotation());
			hoverBearing = (float) mouse.getRotation();
			//System.out.println("mouse: " + hoverBearing);
			if (hoverBearing > 180)
				hoverBearing -= 360;
			else if (hoverBearing <= -180)
				hoverBearing += 360;
			//if (hoverBearing < 0)
			//	hoverBearing = 360 + hoverBearing;
			if (input.isMousePressed(Input.MOUSE_LEFT_BUTTON))
				targetBearing = hoverBearing;
		} else {
			hoverBearing = targetBearing;
		}
		//System.out.println("current: " + currentBearing);
		//System.out.println("hoverBearing: " + hoverBearing);
		bearing.setRotation(hoverBearing - 90);
		bearingOffset = bearingOffset.setRotation(hoverBearing);
		
		if (currentDepth < targetDepth) {
			currentDepth += diveSpeed*dt;
			if (currentDepth > targetDepth)
				currentDepth = targetDepth;
		} else if (currentDepth > targetDepth) {
			currentDepth -= diveSpeed*dt;
			if (currentDepth < targetDepth)
				currentDepth = targetDepth;
		}
		
		super.update(dt);
	}
	
	@Override
	public boolean didRunAground(Image map) {
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
}
