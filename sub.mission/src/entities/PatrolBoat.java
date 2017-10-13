package entities;

import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import core.SubMission;
import jig.Vector;

public class PatrolBoat extends MilitaryVessel {
	
	float safeDistance = 150f;
	
	float shouldUpdate;

	public PatrolBoat(Vector p, float bearing) {
		super("patrol", p, 3, 2.5f, bearing, 45, 20, 10);
		movedFor = new HashMap<Vessel, Float>();
		
		towedSonar = null;
		towedDecoy = null;
		torpedoes = 4;
		decoys = 0;
		layer = "patrol";
		shouldUpdate = 0;
	}
	
	@Override
	public void update(float dt, float ambient) {
		
		super.update(dt, ambient);
		
		shouldUpdate += dt;
		if (shouldUpdate < 1) return;
		shouldUpdate = 0;
		
		int detection = detect((Vessel) SubMission.player);
		//System.out.println("Detection: " + detection);
	
		if (detect(SubMission.player) > 2) {
			destination = SubMission.player.getAsTarget();
		} else if (destination == null) {
			destination = Vector.getRandomXY(SubMission.ScreenHeight - 200, SubMission.ScreenWidth - 200, 200, 200);
		}
		float d = getPosition().distance(destination);
		if (d > safeDistance) {
			waypoint = destination;
		} else if (d < safeDistance) {
			waypoint = getPosition().add( getPosition().subtract(destination).clampLength(safeDistance - d, safeDistance - d) );
			
		}
		
		if (detection == 3 && torpedoes > 0)
			SubMission.addEntity("torpedo", fireTorpedo(SubMission.player));
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

}
