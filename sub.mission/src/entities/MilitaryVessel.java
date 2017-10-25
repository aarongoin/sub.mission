package entities;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;

import core.SubMission;

import java.util.Random;

import jig.Entity;
import jig.Vector;
import util.VectorZ;

public class MilitaryVessel extends Vessel {

	protected int decoys;

	protected int torpedoes;
	float torpedoSpeed = 45;

	protected String torpedoType;
	protected Towable towedDecoy;
	
	protected Towable towedSonar;

	public MilitaryVessel(String image, Vector p, float noise, float sonar, float bearing, float speed, float radius,
			float accel) {
		super(image, p, noise, bearing, speed, radius, accel);
		// debug = true;
		baseSonar = sonar;
		ambient = 0;

		torpedoes = 0;
		decoys = 0;

		towedSonar = null;
		towedDecoy = null;

	}

	public void cableSnapped(int id) {
		// System.out.println("id: " + id);
		if (id == 1) {
			// System.out.println("Towed Sonar Array snapped!");
			towedSonar.detach();
			towedSonar = null;
		} else if (id == 2) {
			if (decoys == 0) {
				// System.out.println("No more decoys!");
				towedDecoy.detach();
				towedDecoy = null;
			} else {
				towedDecoy.reset(this);
				decoys--;
			}
		}
	}

	public boolean decoyDeployed() {
		return (towedDecoy.getState() > 0 && towedDecoy.getState() != 4);
	}

	public int detect(Vessel other) {

		if (towedSonar == null || towedSonar.getState() != 1) {
			float theta = Math.abs((float) (getNose().subtract(getPosition()).getRotation()
					- other.getPosition().subtract(getPosition()).getRotation()));
			if (theta > 157.5 && theta < 202.5)
				return 0;
		}
		float distance = getPosition().distance(other.getPosition());
		float sonar = getSonar();
		float span = (sonar + other.getNoise()) - distance;
		// System.out.println("Distance: " + distance + " Sonar: " + sonar);
		if (span > 0) {
			int random = rand.nextInt((int) sonar);
			if (random < other.getNoise())
				return 3;
			else if (random < span * 2)
				return 2;
			else
				return 1;
		}

		return 0;
	}

	public Torpedo fireTorpedo(Vessel v) {
		if (torpedoes > 0) {
			actionNoise += 100;
			float timeToTarget = getPosition().distance(v.getPosition()) / (torpedoSpeed * 0.5144f);
			Vector target = v.getPosition();// .add(v.getFuturePosition(timeToTarget));
			torpedoes -= 1;
			return new Torpedo(this.id, torpedoType, getPosition(), currentDepth,
					(float) v.getPosition().subtract(getPosition()).getRotation(), torpedoSpeed, torpedoSpeed, target,
					v);
		} else
			return null;
	}

	@Override
	public Vector getAsTarget() {
		if (towedDecoy != null && towedDecoy.getState() == 1) {
			return towedDecoy.sprite.getPosition();
		} else {
			return getPosition();
		}
	}
	
	@Override
	public VectorZ getAsTargetZ() {
		if (towedDecoy != null && towedDecoy.getState() == 1) {
			return new VectorZ(towedDecoy.sprite.getPosition(), currentDepth);
		} else {
			return new VectorZ( getPosition(), currentDepth);
		}
	}

	public int getDecoys() {
		return decoys;
	}

	@Override
	public float getSonar() {
		float b = (towedSonar != null && towedSonar.getState() == 1) ? 3 : 0;
		return (175 * (baseSonar + b) - ambient - currentSpeed * 8);
	}

	public int getTorpedoes() {
		return torpedoes;
	}

	public boolean haveTowedSonar() {
		return (towedSonar != null);
	}

	@Override
	public void render(Graphics g) {
		if (towedSonar != null && towedSonar.getState() > 0)
			towedSonar.render(g);
		if (towedDecoy != null && towedDecoy.getState() > 0)
			towedDecoy.render(g);

		super.render(g);
	}

	public void setArsenal(int t, int d, boolean s) {
		torpedoes = t;
		decoys = d;
		towedSonar = new Towable(this, 400, 0.2f, "towed_sonar", "sonar_waves", 1, 20);
		towedDecoy = new Towable(this, 400, 0.2f, "towed_decoy", "decoy_waves", 2, 20);
	}

	public void setTowState(int s) {
		if (s == 1) {
			if (towedSonar != null) towedSonar.deploy(false);
			if (towedDecoy != null) towedDecoy.deploy(false);
		} else if (s == 2) {
			if (towedSonar != null) towedSonar.deploy(true);
			if (towedDecoy != null) towedDecoy.deploy(false);
		} else if (s == 3) {
			if (towedSonar != null) towedSonar.deploy(false);
			if (towedDecoy != null) towedDecoy.deploy(true);
		}
	}

	@Override
	public void takeDamage(String source) {
		if (source == "torpedo" && towedDecoy != null && towedDecoy.getState() == 1) {
			// destroy decoy instead of taking damage
			towedDecoy.reset(this);
			decoys--;
		} else
			super.takeDamage(source);
	}

	public void update(float dt, float ambient) {
		if (actionNoise > 0.01) {
			actionNoise *= 0.9f;
		} else {
			actionNoise = 0;
		}

		if (towedSonar != null && towedSonar.getState() > 0)
			towedSonar.update(dt);
		if (towedDecoy != null && towedDecoy.getState() > 0)
			towedDecoy.update(dt);

		this.ambient = ambient;
		super.update(dt);
	}
}
