package core;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

import entities.MissionTarget;
import entities.Torpedo;
import entities.Vessel;
import jig.Entity;

public abstract class MissionManager {
	
	protected MissionTarget mission;
	protected int state;
	protected String endMessage;
	protected String missonState;
	
	public MissionManager() { 
		state = 0;
	}
	
	public String getEndMessage() {
		return endMessage;
	}
	
	public String getMissionState() {
		return missonState;
	}
	
	boolean WinOrLose() {
		
		boolean shouldEnd = true;
		
		if (SubMission.player.didRunAground(SubMission.map)) {
			missonState = "Mission Failed!";
			endMessage = "You ran aground!";
		} else if (SubMission.player.isSunk) {
			missonState = "Mission Failed!";
			endMessage = "The ship is sinking, captain!";
		} else if (missionFail()) {
			missonState = "Mission Failed!";
		} else if (missionWin()) {
			missonState = "Mission Completed!";
		} else shouldEnd = false;
		
		return shouldEnd;
	}
	
	public void render(Graphics g) {
		SubMission.trafficManager.render(g);
		SubMission.patrolManager.render(g);
		
		for (Entity e : SubMission.getLayer("torpedo")) {
			((Torpedo) e).render(g);
		}
		
		SubMission.player.render(g);
		mission.render(g);
		
		g.setFont(SubMission.text);
		renderObjective(g);
	}
	
	public boolean update(float dt, Input input, boolean mouse) {
		mission.update(dt);
		
		// check for conditions to advance mission state
		if (advance()) {
			state += 1;
			stage();
		}
		
		float ambientNoise = SubMission.getLayer("traffic").size() + SubMission.getLayer("patrol").size() * 20;
		
		SubMission.player.update(input, ambientNoise, dt, mouse);
		
		SubMission.trafficManager.update(dt, input, mouse);
		SubMission.patrolManager.update(dt, input, ambientNoise, mouse);
		
		for (Entity e : SubMission.getLayer("torpedo")) {
			((Torpedo) e).update(dt);
			if (((Vessel) e).didRunAground(SubMission.map) || !((Torpedo) e).haveFuel())
				SubMission.removeEntity("torpedo", e);
		}
		
		return WinOrLose();
	}

	abstract boolean advance();
	abstract void renderObjective(Graphics g);
	abstract boolean missionFail();
	abstract boolean missionWin();
	abstract void stage();
}
