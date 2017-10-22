package core;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import entities.Airplane;
import entities.PatrolBoat;
import entities.Vessel;
import jig.Entity;
import jig.Vector;
import util.VectorUtil;


public class PatrolManager {
	
	Random rand;
	
	Vector zones[];
	Stack<Integer> needsAssigned;
	ArrayList<PatrolBoat> cowards;
	Vector spawnPoint;
	Vector fleePoint;
	int onPatrol;
	Stack<Vector> playerPosition;
	Vector player;
	
	
	// zones are defined as points on the map where Patrol Boats can be assigned to patrol
	// zones = {
	//				{x, y},
	//				...
	//			}
	public PatrolManager(Vector zones[], Vector spawn, Vector flee) {
		SubMission.addLayer("patrol");
		this.zones = zones;
		rand = new Random(System.currentTimeMillis());
		spawnPoint = spawn;
		onPatrol = 0;
		needsAssigned = new Stack<Integer>();
		cowards = new ArrayList<PatrolBoat>();
		fleePoint = flee;
		playerPosition = new Stack<Vector>();
	}
	
	public Vector getFleePoint() {
		return fleePoint;
	}
	
	public Vector zoneAssignment(int zone) {
		return zones[zone].add( Vector.getRandomXY(-50, 50, -50, 50) );
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
	
	public int getClosestZoneTo(Vector location) {
		int zone = -1;
		float zd = 1200;
		float d;
		for (int i = 0; i < zones.length; i++) {
			if (zone < 0) {
				zone = i;
				zd = zones[i].distance(location);
			} else {
				d = zones[i].distance(location);
				if (d < zd) {
					zone = i;
					zd = d;
				}
			}
		}
		return zone;
	}
	
	public void detectedSubmarineAt(Vector subLocation) {
		playerPosition.push(subLocation);
	}
	
	public boolean shouldPursueSubmarineAt(int zone, Vector subLocation) {
		
		playerPosition.push(subLocation);
		
		int closest = getClosestZoneTo(subLocation);
		
		return closest == zone;
	}
	
	public Vector randomPositionIn(int bounds[]) {
		float x = 0;
		float y = 0;
		
		x = rand.nextInt(Math.abs(bounds[2])) * (bounds[2] < 0 ? -1 : 1);
		
		y = rand.nextInt(Math.abs(bounds[3])) * (bounds[3] < 0 ? -1 : 1);
				
		return new Vector(x, y);
	}
	
	public void addShip(Vector start) {
		addShip(rand.nextInt(zones.length), start);
	}
	
	public void addShip(int zone, Vector start) {
		
		Vector assignment = zoneAssignment(zone);
		Vector delta = assignment.subtract(start);
		Vector current = start.add(Vector.getRandomXY(-50, 50, -50, 50));
		
		PatrolBoat pb = new PatrolBoat(current, (float) delta.getRotation(), this);
		pb.assignTo(zone, assignment);
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

		
		while (onPatrol < zones.length) {
			addShip(onPatrol, spawnPoint);
			onPatrol++;
		}
		
		while (onPatrol < qty) {
			addShip(spawnPoint);
			onPatrol++;
		}
		
		while (onPatrol > qty) {
			removeShip();
			onPatrol--;
		}
	}
	
	public void onSink(PatrolBoat pb) {
		needsAssigned.push(pb.zone);
	}
	
	public void onReturn(PatrolBoat pb) {
		cowards.add(pb);
		needsAssigned.push(pb.zone);
		pb.assignment = fleePoint;
	}
	
	public void update(float dt, Input input, float ambientNoise) {
		int closestZone = getClosestZoneTo(SubMission.player.getPosition());
		int size = playerPosition.size();
		if (size > 0) {
			player = playerPosition.pop().scale(1/size);
			while (playerPosition.size() > 0) {
				player = player.add( playerPosition.pop().scale(1/size) );
			}
		}

		while (needsAssigned.size() > 0) {
			addShip(needsAssigned.pop(), fleePoint);
		}
		
		for (int i = cowards.size() - 1; i >= 0; i--) {
			if (cowards.get(i).getPosition().distance(fleePoint) < 50) {
				SubMission.removeEntity("patrol", (Entity) cowards.remove(i));
			}
		}
		PatrolBoat v;
		for (Entity e : SubMission.getLayer("patrol")) {
			v = (PatrolBoat) e;
			if (v.zone == closestZone && v.getPosition().distance(v.assignment) < 150) v.pursuePlayer(player);
			v.update(dt, ambientNoise);
			if (v.didRunAground(SubMission.map)) {
				onSink(v);
				v.sink();
			} else if (v.isDetected()
					&& input.isMousePressed(Input.MOUSE_LEFT_BUTTON)
					&& v.wasClicked(input.getMouseX(), input.getMouseY())) {
				
				SubMission.player.getLock(v);
			}
		}
	}
	
	public void render(Graphics g) {
		
		for (Entity e : SubMission.getLayer("patrol"))
			((PatrolBoat) e).render(g);
	}
}
