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
	
	float patrolTimer;
	
	float safeDistance = 150f;
	
	public final Vector assignment;
	PatrolManager HQ;
	
	float shouldUpdate;

	public PatrolBoat(Vector p, float bearing, Vector assignment, PatrolManager hq) {
		super("patrol", p, 10, 2.5f, bearing, 1, 10, 10);
		movedFor = new HashMap<Vessel, Float>();
		
		targetSpeed = 35;
		
		towedSonar = null;
		towedDecoy = null;
		torpedoes = 4;
		decoys = 0;
		layer = "patrol";
		shouldUpdate = 0;
		
		lookahead = 2f;
		
		torpedoType = "enemy_torpedo";
		
		patrolTimer = 0;
		
		this.assignment = assignment;
		HQ = hq;
	}
	
	@Override
	public float getSonar() {
		//System.out.println("Base: " + (175 * baseSonar) + " Ambient: " + ambient + " Speed: " + (currentSpeed * 2));
		return (175 * baseSonar - ambient - currentSpeed * 2);
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
		
				
		patrolTimer -= dt;
		if (patrolTimer < 0)
			patrolTimer = 0;
		
		shouldUpdate += dt;
		if (shouldUpdate > 1) {
			shouldUpdate = 0;
			
			int detection = detect((Vessel) SubMission.player);
			//System.out.println("Detection: " + detection);
		
			if (detect(SubMission.player) > 2) {
				destination = SubMission.player.getAsTarget();
			} else if (patrolTimer == 0) {
				//setDestination( Vector.getRandomXY(SubMission.ScreenHeight - 200, SubMission.ScreenWidth - 200, 200, 200) );
				setDestination(assignment);
				patrolTimer = rand.nextFloat() * 60f;
			}
			/*float d = getPosition().distance(destination);
			if (d < safeDistance) {
				setWaypoint( getPosition().add( getPosition().subtract(destination).clampLength(safeDistance - d, safeDistance - d).rotate(rand.nextInt(90) - 90) ) );
			}*/
			
			if (detection == 3 && torpedoes > 0)
				SubMission.addEntity("torpedo", fireTorpedo(SubMission.player));
			
			fieldNav(collideWith);
			//navigate(collideWith);
		}
		
		super.update(dt, ambient);
		
		//navigate(collideWith);
	}
	
	@Override
	public void sink() {
		HQ.onSink(this);
		super.sink();
	}

}
