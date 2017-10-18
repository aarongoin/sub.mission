package core;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import entities.PatrolBoat;
import jig.Entity;
import jig.Vector;
import util.VectorUtil;


public class PatrolManager {
	
	Random rand;
	
	Vector zones[];
	Stack<Vector> needsAssigned;
	Vector spawnPoint;
	int onPatrol;
	
	// zones are defined as points on the map where Patrol Boats can be assigned to patrol
	// zones = {
	//				{x, y},
	//				...
	//			}
	public PatrolManager(Vector zones[], Vector spawn) {
		SubMission.addLayer("patrol");
		this.zones = zones;
		rand = new Random(System.currentTimeMillis());
		spawnPoint = spawn;
		onPatrol = 0;
		needsAssigned = new Stack<Vector>();
	}
	
	public void removeShip() {
		Vector p = SubMission.player.getPosition();
		float dist = 0;
		float e_dist;
		Entity farthest = null;
		// pick ship farthest from player
		for (Entity e : SubMission.getLayer("patrol")) {
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
		SubMission.removeEntity("patrol", farthest);
	}
	
	public boolean shouldPursueSubmarineAt(Vector assignment, Vector subLocation) {
		Vector zone = null;
		float zd = 1200;
		float d;
		// get zone closest to player
		for (Vector v : zones) {
			if (zone == null) {
				zone = v;
				zd = v.distance(subLocation);
			} else {
				d = v.distance(subLocation);
				if (d < zd) {
					zone = v;
					zd = d;
				}
			}
		}
		
		return (zone.distance(assignment) < 100);
	}
	
	public Vector randomPositionIn(int bounds[]) {
		float x = 0;
		float y = 0;
		
		x = rand.nextInt(Math.abs(bounds[2])) * (bounds[2] < 0 ? -1 : 1);
		
		y = rand.nextInt(Math.abs(bounds[3])) * (bounds[3] < 0 ? -1 : 1);
				
		return new Vector(x, y);
	}
	
	public void addShip() {
		addShip( zones[ rand.nextInt(zones.length) ].add( Vector.getRandomXY(-50, 50, -50, 50) ) );
	}
	
	public void addShip(Vector assignment) {
		
		Vector delta = assignment.subtract(spawnPoint);
		Vector current = spawnPoint.add(Vector.getRandomXY(-50, 50, -50, 50));
		
		PatrolBoat pb = new PatrolBoat(current, (float) delta.getRotation(), assignment, this);
		pb.setDestination(assignment);
		//System.out.println("Creating ship at: " + pb.getPosition());
		
		// and add to the game
		SubMission.addEntity("patrol", (Entity) pb);
	}
	
	public void reset() {
		SubMission.removeLayer("patrol");
		SubMission.addLayer("patrol");
		onPatrol = 0;
	}
	
	public void setPatrol(int qty) {
		
		while (onPatrol < qty) {
			addShip();
			onPatrol++;
		}
		
		while (onPatrol > qty) {
			removeShip();
			onPatrol--;
		}
	}
	
	public void onSink(PatrolBoat pb) {
		needsAssigned.push(pb.assignment);
	}

	public void update() {
		while (needsAssigned.size() > 0) {
			addShip(needsAssigned.pop());
		}
		/*for (Entity e : SubMission.getLayer("patrol")) {
			
			if (((Vessel) e).getDestination().distance(e.getPosition()) < 10) {
				SubMission.removeEntity("patrol", e);
				addShip();
			}
		}*/
		
	}
	
}
