package core;

import entities.Vessel;
import jig.Entity;
import jig.Vector;

public class NavigationManager {

	float traffic;
	
	public NavigationManager() {
		traffic = 0f;
	}
		
	public void update(float dt) {
		boolean clear;
		float classE;
		Vessel E;
		Vessel O;
		Vector posE;
		Vector posO;
		
		traffic -= dt;
		if (traffic < 0) {
			traffic = 10f;
		
			for (Entity e : SubMission.getLayer("traffic")) {
				E = (Vessel) e;
				posE = E.getFuturePosition(E.lookahead);
				classE = E.getRadius() / E.getSpeed();
				clear = true;
				
				Vector land;
				// make sure won't run aground
				for (int[] l : SubMission.landMasses) {
					land = new Vector(l[0], l[1]);
					if (Physics.didCollide( posE, land, (float) E.getRadius(), (float) l[2] )) {
						// adjust course
						E.moveFor(posE, land, l[2]);
						clear = false;
					}
				}
				
				for (Entity o : SubMission.getLayer("traffic")) {
					if (e == o) continue;
					O = (Vessel) o;
					posO = O.getFuturePosition(E.lookahead);
					
					if (Physics.didCollide( posE, posO, (float) E.getRadius(), (float) O.getRadius() )) {
						// should E adjust course to avoid O
						if (classE <= O.getRadius() / O.getSpeed()) {
							E.moveFor(posE, posO, O.getRadius());
							clear = false;
						}
					}
				}
				// head towards target if clear ahead
				//if (clear) E.setWaypoint();
			}
		}
		
		for (Entity e : SubMission.getLayer("military")) {
			E = (Vessel) e;
			posE = E.getFuturePosition(E.lookahead);
			classE = E.getRadius() / E.getSpeed();
			clear = true;
			
			Vector land;
			// make sure won't run aground
			for (int[] l : SubMission.landMasses) {
				land = new Vector(l[0], l[1]);
				if (Physics.didCollide( posE, land, (float) E.getRadius(), (float) l[2] )) {
					// adjust course
					E.moveFor(posE, land, l[2]);
					clear = false;
				}
			}
			
			for (Entity o : SubMission.getLayer("traffic")) {
				O = (Vessel) o;
				posO = O.getFuturePosition(E.lookahead);
				
				if (Physics.didCollide( posE, posO, (float) E.getRadius(), (float) O.getRadius() )) {
					// should E adjust course to avoid O
					if (classE <= O.getRadius() / O.getSpeed()) {
						E.moveFor(posE, posO, O.getRadius());
						clear = false;
					}
				}
			}
			for (Entity o : SubMission.getLayer("military")) {
				if (e == o) continue;
				O = (Vessel) o;
				posO = O.getFuturePosition(E.lookahead);
				
				if (Physics.didCollide( posE, posO, (float) E.getRadius(), (float) O.getRadius() )) {
					// should E adjust course to avoid O
					if (classE <= O.getRadius() / O.getSpeed()) {
						E.moveFor(posE, posO, O.getRadius());
						clear = false;
					}
				}
			}
			// head towards target if clear ahead
			//if (clear) E.setWaypoint();
		}
	}

}
