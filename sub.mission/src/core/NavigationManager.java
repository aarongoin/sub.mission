package core;

import entities.Vessel;
import jig.Entity;
import jig.Vector;

public class NavigationManager {

	float traffic;
	
	public NavigationManager() {
		traffic = 0f;
	}
	
	public boolean willCollide(Vessel a, Vessel b, float time) {
		return willCollide(a.getVelocity(), b.getVelocity(), a.getPosition(), b.getPosition(), time);
	}
	
	public boolean willCollide(Vector Va, Vector Vb, Vector Pa, Vector Pb, float time) {
		int xN = (int) (Pb.getX() - Pa.getX());
		int xD = (int) (Va.getX() - Vb.getX());
		
		int yN = (int) (Pb.getY() - Pa.getY());
		int yD = (int) (Va.getY() - Vb.getY());
		
		float tx = ( xN + 0.01f ) / ( xD + 0.01f);
		float ty = ( yN + 0.01f ) / ( yD + 0.01f);
		
		//System.out.println("tx: " + xN + "/" + xD + " ty: " + yN + "/" + yD);
		
		if (xN == 0 && xD == 0 || yN == 0 && yD == 0)
			
			
			return ( (tx == 1 && ty < time) || (ty == 1 && tx < time) );
		else
			return ( (Math.abs(tx - ty) < 10) && tx < time );
	}
	
	
	public void update(float dt) {
		float classE;
		Vessel E;
		Vessel O;
		Vector posE;
		Vector posO;
		Vector waypoint;
		
		traffic -= dt;
		if (traffic < 0) {
			traffic = 0.2f;
		
			for (Entity e : SubMission.getLayer("traffic")) {
				E = (Vessel) e;
				posE = E.getFuturePosition(E.lookahead);
				classE = E.getRadius() / E.getSpeed();
				waypoint = posE.subtract(E.getPosition());
				
				Vector land;
				// make sure won't run aground
				for (int[] l : SubMission.landMasses) {
					land = new Vector(l[0], l[1]);
					if (Physics.didCollide( posE, land, (float) E.getRadius(), (float) l[2] )) {
						// adjust course
						E.moveFor(land, l[2]);
					}
				}
				
				for (Entity o : SubMission.getLayer("traffic")) {
					if (e == o) continue;
					O = (Vessel) o;
					posO = O.getFuturePosition(E.lookahead);
					
					if (willCollide(E, O, 60f) && classE <= O.getRadius() / O.getSpeed()) {
						//E.moveFor(O);
						waypoint = waypoint.add(O.getVelocity().scale(-1).scale(O.getRadius() / E.getPosition().distanceSquared(O.getPosition())));
					}
				}
				E.setWaypoint(waypoint);
			}
		}
		
		for (Entity e : SubMission.getLayer("patrol")) {
			E = (Vessel) e;
			posE = E.getFuturePosition(E.lookahead);
			classE = E.getRadius() / E.getSpeed();
			
			Vector land;
			// make sure won't run aground
			for (int[] l : SubMission.landMasses) {
				land = new Vector(l[0], l[1]);
				if (Physics.didCollide( posE, land, (float) E.getRadius(), (float) l[2] )) {
					// adjust course
					E.moveFor(land, l[2]);
				}
			}
			
			for (Entity o : SubMission.getLayer("traffic")) {
				O = (Vessel) o;
				posO = O.getFuturePosition(E.lookahead);
				
				if (Physics.didCollide( posE, posO, (float) E.getRadius(), (float) O.getRadius() )) {
					// should E adjust course to avoid O
					if (classE <= O.getRadius() / O.getSpeed()) {
						E.moveFor(O);
					}
				}
			}
			for (Entity o : SubMission.getLayer("patrol")) {
				if (e == o) continue;
				O = (Vessel) o;
				posO = O.getFuturePosition(E.lookahead);
				
				if (Physics.didCollide( posE, posO, (float) E.getRadius(), (float) O.getRadius() )) {
					// should E adjust course to avoid O
					if (classE <= O.getRadius() / O.getSpeed()) {
						E.moveFor(O);
					}
				}
			}
		}
	}

}
