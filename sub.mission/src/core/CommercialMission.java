package core;

import org.newdawn.slick.Graphics;

import entities.Airplane;
import entities.MissionTarget;
import entities.Submarine;
import jig.Vector;

public class CommercialMission extends MissionManager {

	public CommercialMission() {
		SubMission.player = new Submarine(300, 10, -135, new Vector(SubMission.ScreenWidth - 100f, SubMission.ScreenHeight - 100f));
		
		SubMission.trafficManager = new CommercialManager(SubMission.shippingLanes);
		
		SubMission.airSupport = new Airplane(60, 30);
		
		SubMission.patrolManager = new PatrolManager(SubMission.patrolZones, new Vector(SubMission.ScreenWidth / 2, SubMission.ScreenHeight / 2), new Vector(175, -50));
	
		stage();
	}

	@Override
	boolean advance() {
		switch (state) {
		case 0: // has player sunk a single ship?
			return (SubMission.trafficManager.getSunk() > 0);
		case 1: // has player sunk at least three ships?
			return (SubMission.trafficManager.getSunk() > 2);
		case 2: // has player sunk six ships?
			return (SubMission.trafficManager.getSunk() > 5);
		default:
			return false;
		}
	}

	@Override
	void renderObjective(Graphics g) {
		if (state == 3) {
			g.drawString("Escape to open waters.", 75f, 15f);
		} else {
			int remaining = 6 - SubMission.trafficManager.getSunk();
			g.drawString("Sink " + remaining + " commercial vessels.", 75f, 15f);
		}
	}

	@Override
	boolean missionFail() {
		return false;
	}

	@Override
	boolean missionWin() {
		boolean check = (state == 3 && mission.collides(SubMission.player) != null);
		if (check) endMessage = "Great Job, Captain.";
		return check;
	}

	@Override
	void stage() {
		switch (state) {
		case 0: // mission start
			// generate commercial traffic
			SubMission.trafficManager.reset();
			SubMission.trafficManager.setTraffic(10);
			// generate enemy patrol boats
			SubMission.patrolManager.reset();
			SubMission.patrolManager.setPatrol(4);
			SubMission.removeLayer("torpedo");
			SubMission.addLayer("torpedo");
			break;
			
		case 1: // deploy more patrol
			SubMission.patrolManager.setPatrol(6);
			break;
		case 2: // call in air support
			SubMission.patrolManager.setPatrol(9);
			SubMission.airSupport.deployAt(new Vector(700, 425), Vector.getRandomXY(-1, 1, -1, 1));
			break;
		case 3: // call in more patrol before player escapes
			mission = new MissionTarget(new Vector(SubMission.ScreenWidth - 100f, SubMission.ScreenHeight - 100f), 0);
		default:
			break;
		}
	}

	@Override
	void renderMission(Graphics g) {
		g.setFont(SubMission.text);
		g.drawString("Captain,", 300, SubMission.ScreenHeight / 5);
		
		g.drawString("We are at war, and yet our enemy flourishes due to trade. Strike fear into their hearts and", 300, SubMission.ScreenHeight / 5 + 60);
		g.drawString("sink 6 commercial vessels to dissuade future trade and weaken our foe. Then escape into open", 300, SubMission.ScreenHeight / 5 + 90);
		g.drawString("waters to complete this mission.", 300, SubMission.ScreenHeight / 5 + 120);
				
		g.drawString("Good luck.", 300, SubMission.ScreenHeight / 5 + 180);
	}

}
