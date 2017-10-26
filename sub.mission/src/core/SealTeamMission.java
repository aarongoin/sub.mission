package core;

import org.newdawn.slick.Graphics;

import entities.Airplane;
import entities.MissionTarget;
import entities.Submarine;
import jig.Vector;

public class SealTeamMission extends MissionManager {

	public SealTeamMission() {
		super();
		
		SubMission.player = new Submarine(150, 10, 90, new Vector(SubMission.ScreenWidth - 200f, 50));
		
		SubMission.trafficManager = new CommercialManager(SubMission.shippingLanes);
		
		SubMission.airSupport = new Airplane(60, 30);
		
		SubMission.patrolManager = new PatrolManager(SubMission.patrolZones, new Vector(SubMission.ScreenWidth / 2, SubMission.ScreenHeight / 2), new Vector(175, -50));
	
		stage();
	}

	@Override
	boolean advance() {
		switch (state) {
		case 1:
			return mission.getPercent() <= 0f;
		case 0:
		case 2:
		case 3:
			return mission.collides(SubMission.player) != null;
		default:
			return false;
		}
	}
	
	@Override
	void renderObjective(Graphics g) {
		switch (state) {
		case 0:
			g.drawString("Get to the mission target to deploy your special forces.", 75f, 15f);
			break;
		case 1:
			g.drawString("Move into deep water and remain undetected until your rendezvous window opens.", 75f, 15f);
			break;
		case 2:
			g.drawString("Rendezvous with your team before the enemy captures them!", 75f, 15f);
			break;
		case 3:
			g.drawString("The package has been wounded! Escape to open waters and get him to medical help!", 75f, 15f);
			break;
		}
	}

	@Override
	boolean missionFail() {
		boolean check = false;
		switch (state) {
		case 0:
			check = mission.getPercent() <= 0f;
			if (check) endMessage = "You need to deploy your team before the window closes!";
			break;
		case 2:
			check = mission.getPercent() <= 0f;
			if (check) endMessage = "You missed the rendezvous window! The enemy has captured your team.";
			break;
		case 3:
			check = mission.getPercent() <= 0f;
			if (check) endMessage = "You failed to escape enemy waters in time!";
			break;
		}
		return check;
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
			mission = new MissionTarget(new Vector(80f, 550f), 300f);
			// generate commercial traffic
			SubMission.trafficManager.reset();
			SubMission.trafficManager.setTraffic(10);
			// generate enemy patrol boats
			SubMission.patrolManager.reset();
			SubMission.patrolManager.setPatrol(5);
			SubMission.removeLayer("torpedo");
			SubMission.addLayer("torpedo");
			break;
			
		case 1: // deploy special forces & surge of enemies
			mission = new MissionTarget(new Vector(300f, 200f), 60);
			break;
		case 2: // rendezvous with special forces
			SubMission.airSupport.deployAt(new Vector(700, 425), Vector.getRandomXY(-1, 1, -1, 1));
			mission = new MissionTarget(new Vector(100f, 150f), 60);
			break;
		case 3: // escape
			mission = new MissionTarget(new Vector(SubMission.ScreenWidth - 100f, 100f), 120);
			break;
		default:
			break;
		}
	}
}
