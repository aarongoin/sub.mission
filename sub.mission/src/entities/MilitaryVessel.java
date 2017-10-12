package entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;

import core.SubMission;

import java.util.Random;

import jig.Vector;

public class MilitaryVessel extends Vessel {

	float torpedoSpeed = 50;
	protected float baseSonar;
	float ambient;
	Random rand;
	
	protected int torpedoes;
	protected int decoys;
	
	protected Towable towedSonar;
	protected Towable towedDecoy;

	public MilitaryVessel(String image, Vector p, float noise, float sonar, float bearing, float speed, float radius, float accel) {
		super(image, p, noise, bearing, speed, radius, accel);
		
		baseSonar = sonar;
		ambient = 0;
		
		rand = new Random(System.currentTimeMillis());
		
		torpedoes = 0;
		decoys = 0;
		
		towedSonar = null;
		towedDecoy = null;
		
	}
	
	public void setArsenal(int t, int d, boolean s) {
		torpedoes = t;
		decoys = d;
		towedSonar = new Towable(this, 400, 0.2f, "towed_sonar", "sonar_waves", 1, 20);
		towedDecoy = new Towable(this, 400, 0.2f, "towed_decoy", "decoy_waves", 2, 20);
	}
	
	public boolean haveTowedSonar() {
		return (towedSonar != null);
	}
	
	public boolean decoyDeployed() {
		return (towedDecoy.getState() > 0 && towedDecoy.getState() != 4);
	}
	
	public int getTorpedoes() {
		return torpedoes;
	}
	
	public int getDecoys() {
		return decoys;
	}
	
	public Torpedo fireTorpedo(Vessel v) {
		if (torpedoes > 0) {
			float timeToTarget = getPosition().distance(v.getPosition()) / (torpedoSpeed  * 0.5144f);
			Vector target = v.getPosition();//.add(v.getFuturePosition(timeToTarget));
			System.out.println("Target: " + target + " time: " + timeToTarget);
			torpedoes -= 1;
			return new Torpedo("sub_torpedo", getPosition(), (float) v.getPosition().subtract(getPosition()).getRotation(), torpedoSpeed, torpedoSpeed, target);
		} else return null;
	}
	
	public float getSonar() {
		float b = (towedSonar != null && towedSonar.getState() == 1) ? 3 : 0;
		return (175 * (baseSonar + b) - ambient - currentSpeed * 8);
	}
	
	public void update(float dt, float ambient) {
		
		if (towedSonar != null && towedSonar.getState() > 0) towedSonar.update(dt);
		if (towedDecoy != null && towedDecoy.getState() > 0) towedDecoy.update(dt);
		
		this.ambient = ambient;
		super.update(dt);
	}
	
	public int detect(Vessel other) {
		
		if (towedSonar.getState() != 1) {
			float theta = Math.abs((float) (getNose().subtract(getPosition()).getRotation() - other.getPosition().subtract(getPosition()).getRotation()));
			if (theta > 157.5 && theta < 202.5)
				return 0;
		}
		float distance = getPosition().distance(other.getPosition());
		float sonar = getSonar();
		float span = sonar + other.getNoise();

		if (distance < span) {
			int random = rand.nextInt((int) (distance + other.getNoise()));
			if (random <= sonar * 2 / 3)
				return 3;
			else if (random <= sonar)
				return 2;
			else
				return 1;
		}
		
		return 0;
	}
	
	public void setTowState(int s) {
		if (s == 1) {
			towedSonar.deploy(false);
			towedDecoy.deploy(false);
		} else if (s == 2) {
			towedSonar.deploy(true);
			towedDecoy.deploy(false);
		} else if (s == 3) {
			towedSonar.deploy(false);
			towedDecoy.deploy(true);
		}
	}

	
	public void cableSnapped(int id) {
		//System.out.println("id: " + id);
		if (id == 1) {
			//System.out.println("Towed Sonar Array snapped!");
			towedSonar.detach();
			towedSonar = null;
		} else if (id == 2) {
			if (decoys == 0) {
				//System.out.println("No more decoys!");
				towedDecoy.detach();
				towedDecoy = null;
			} else {
				towedDecoy.reset(this);
				decoys--;
			}
		}
	}
	
	@Override
	public void render(Graphics g) {
		if (debug) {
			float sonar = getSonar();
			g.setColor(Color.green);
			g.drawOval(getPosition().getX() - sonar, getPosition().getY() - sonar, sonar * 2, sonar * 2);
			
			g.setColor(Color.white);
		}
		
		if (towedSonar != null && towedSonar.getState() > 0) towedSonar.render(g);
		if (towedDecoy != null && towedDecoy.getState() > 0) towedDecoy.render(g);
		
		super.render(g);
	}
}
