package core;

import java.util.Random;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import entities.CommercialVessel;
import jig.Entity;
import jig.Vector;

public class CommercialManager {

	Random rand;
	
	int trafficLevel;
	int lanes[][][];

	// lanes are defined as a pair of off-screen boxes were ships spawn and de-spawn
	// lanes = {
	//				{
	//					{x0, y0, x1, y1},
	//					{x0, y0, x1, y1}
	//				},
	//				...
	//			}
	public CommercialManager(int lanes[][][]) {
		SubMission.addLayer("traffic");
		this.lanes = lanes;
		rand = new Random(System.currentTimeMillis());
		trafficLevel = 0;
	}
	
	public void removeShip() {
		Vector p = SubMission.player.getPosition();
		float dist = 0;
		float e_dist;
		Entity farthest = null;
		// pick ship farthest from player
		for (Entity e : SubMission.getLayer("traffic")) {
			if (farthest == null) {
				farthest = e;
				dist = p.distance(e.getPosition());
			} else {
				e_dist = p.distance(e.getPosition());
				if (e_dist > dist) {
					farthest = e;
					dist = e_dist;
				}
			}
		}
		SubMission.removeEntity("traffic", farthest);
	}
	
	public Vector randomPositionIn(int bounds[]) {
		float x = 0;
		float y = 0;
		
		x = rand.nextInt(Math.abs(bounds[2])) * (bounds[2] < 0 ? -1 : 1);
		
		y = rand.nextInt(Math.abs(bounds[3])) * (bounds[3] < 0 ? -1 : 1);
				
		return new Vector(x, y);
	}
	
	public void addShip() {
		
		// pick a shipping lane box
		int lane[][] = lanes[ rand.nextInt(lanes.length) ];
		
		// generate vessel within one of the two boxes
		int b = rand.nextInt(2);
		int bounds[] = lane[b];
		
		Vector start = randomPositionIn(bounds);
		// set the vessel's destination to be somewhere in the other box
		bounds = lane[ (b == 1) ? 0 : 1 ];
		Vector end = randomPositionIn(bounds);
		Vector delta = end.subtract(start);
		Vector current = start.add( delta.scale(rand.nextFloat()) );
		
		CommercialVessel cv = new CommercialVessel("ship" + (rand.nextInt(3) + 1), current, 40, (float) delta.getRotation());
		cv.setDestination(end);
		
		//System.out.println("Creating ship at: " + cv.getPosition());
		
		// and add to the game
		SubMission.addEntity("traffic", (Entity) cv);
	}
	
	public void reset() {
		SubMission.removeLayer("traffic");
		SubMission.addLayer("traffic");
		trafficLevel = 0;
	}
	
	public void setTraffic(int qty) {
		
		while (trafficLevel < qty) {
			addShip();
			trafficLevel++;
		}
		
		while (trafficLevel > qty) {
			removeShip();
			trafficLevel--;
		}
	}
	
	public boolean colliding(CommercialVessel cv) {
		CommercialVessel other;
		for (Entity e : SubMission.getLayer("traffic")) {
			other = (CommercialVessel) e;
			if (other.id == cv.id) continue;
			else if (other.getPosition().distance(cv.getPosition()) < 50) return true;
		}
		return false;
	}

	public void update(float dt, Input input, boolean mouse) {
		int toAdd = 0;
		CommercialVessel v;
		for (Entity e : SubMission.getLayer("traffic")) {
			v = (CommercialVessel) e;
			
			if (v.didRunAground(SubMission.map)
			|| v.getDestination().distance(v.getPosition()) < 10
			|| colliding(v)) {
				SubMission.removeEntity("traffic", e);
				toAdd++;
			} else {
				v.update(dt);
				v.isDetected();
				if (mouse && v.wasClicked(input.getMouseX(), input.getMouseY()) && v.isDetected()) {
					input.clearMousePressedRecord();
					SubMission.player.getLock(v);
				}
			}
		}
		while (toAdd-- > 0) addShip();
	}

	public void render(Graphics g) {
		for (Entity e : SubMission.getLayer("traffic"))
			((CommercialVessel) e).render(g);
		
	}
	
	
}
