package entities;

import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import core.PatrolManager;
import core.SubMission;
import jig.Entity;
import jig.Vector;

public class PatrolBoat extends MilitaryVessel {
	
	String collideWith[] = {"traffic", "patrol"};
	
	boolean flee;
	
	float safeDistance = 150f;
	
	public int zone;
	public Vector assignment;
	Vector playerAt;
	PatrolManager HQ;
	
	float shouldUpdate;

	public PatrolBoat(Vector p, float bearing, PatrolManager hq) {
		super("patrol", p, 10, 2.5f, bearing, 10, 10, 4);
		movedFor = new HashMap<Vessel, Float>();
		//debug = true;
		targetSpeed = 35;
		
		towedSonar = null;
		towedDecoy = null;
		torpedoes = 2;
		decoys = 0;
		layer = "patrol";
		shouldUpdate = 0;
		maxSpeed = 35;
		lookahead = 2f;
		
		torpedoType = "enemy_torpedo";
		
		flee = false;
		HQ = hq;
		
		navi = new VesselNavigator(sprite.getWidth(), sprite.getHeight() * 4, sprite.getHeight() * 1.5f);
	}
	
	@Override
	public float getSonar() {
		//System.out.println("Base: " + (175 * baseSonar) + " Ambient: " + ambient + " Speed: " + (currentSpeed * 2));
		return (175 * baseSonar - ambient - currentSpeed * 2);
	}
	
	public void pursuePlayer(Vector player) {
		playerAt = player;
	}
	
	@Override
	public void render(Graphics g) {
		// draw line
		g.setColor(new Color(0.8f, 0.8f, 0.8f, 0.3f));
		if (debug && destination != null) g.drawLine(destination.getX(), destination.getY(), getX(), getY());
				
		super.render(g);
	}
	
	@Override
	public void update(float dt, float ambient) {

		shouldUpdate += dt;
		if (shouldUpdate > 0.5) {
			shouldUpdate = 0;
			
			navi.update(currentBearing, getPosition());
			betterSteering(collideWith);
			//fieldNav(collideWith);
		}
		
		int detection = detect((Vessel) SubMission.player);
		//System.out.println("Detection: " + detection);
	
		if (detection > 2) {
			int fudge = 100 / detection;
			Vector subPosition = SubMission.player.getAsTarget().add(Vector.getRandomXY(-fudge, fudge, -fudge, fudge));
			HQ.detectedSubmarineAt(subPosition);
		}
		
		if (playerAt != null && !flee) {
			setDestination(playerAt);
			// adding some randomness to whether or not the boat will fire
			if (detection > 2 && torpedoes > 0 && rand.nextInt(40) == 0)
				SubMission.addEntity("torpedo", fireTorpedo(SubMission.player));
		} else {
			setDestination(assignment);
		}
		
		super.update(dt, ambient);
		
	}
	
	public void assignTo(int zone, Vector assignment) {
		this.assignment = assignment;
		this.zone = zone;
		setDestination(assignment);
	}
	
	@Override
	public Torpedo fireTorpedo(Vessel target) {
		if (torpedoes == 1) {
			// flee back to HQ
			setDestination( HQ.getFleePoint() );
			flee = true;
			HQ.onReturn(this); // request replacement
		}
		return super.fireTorpedo(target);
	}
}
