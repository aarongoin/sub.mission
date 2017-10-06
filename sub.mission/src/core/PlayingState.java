package core;



import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import entities.CommercialVessel;
import entities.MilitaryVessel;
import entities.MissionTarget;
import entities.Submarine;
import entities.Vessel;
import jig.Vector;
import jig.Entity;


class PlayingState extends BasicGameState {
	
	
	int state;
	float sonarCountdown;
	
	DepthMeter depth;
	SpeedMeter speed;
	
	Submarine player;
	MissionTarget mission;

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		SubMission G = (SubMission) game;
		
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		SubMission G = (SubMission) game;
		
		sonarCountdown = 0;
		
		// insert submarine
		player = new Submarine(100, 10);
		//player.debug(true);
		
		// generate UI
		depth = new DepthMeter((int) player.getDepth(), new Vector(SubMission.ScreenWidth - 24, 12));
		speed = new SpeedMeter((int) player.getSpeed(), new Vector(SubMission.ScreenWidth - 415, SubMission.ScreenHeight - 24));
		
		state = 0;
		stage(G);
		
		System.out.println("theta( x, 0): " + new Vector( 1, 0).getRotation()
					   + ", theta( 0, y): " + new Vector( 0, 1).getRotation()
					   + ", theta(-x, 0): " + new Vector(-1, 0).getRotation()
					   + ", theta(-x,-y): " + new Vector(-1,-1).getRotation()
					   + ", theta( 0,-y): " + new Vector( 0,-1).getRotation()
					   + ", theta( x,-y): " + new Vector( 1,-1).getRotation());
	}
	
	boolean advance() {
		switch (state) {
		case 1:
			return mission.getPercent() <= 0f;
		case 0:
		case 2:
		case 3:
			return mission.collides(player) != null;
		default:
			return false;
		}
	}
	
	void stage(SubMission G) {
		switch (state) {
		
		case 0: // mission start
			mission = new MissionTarget(new Vector(80f, 550f), 300f);
			// generate commercial traffic
			G.removeLayer("traffic");
			G.addLayer("traffic");
			CommercialVessel cv;
			cv = new CommercialVessel("ship1", new Vector(SubMission.ScreenWidth - 500, SubMission.ScreenHeight - 50), 30, 330);
			G.addEntity("traffic", (Entity) cv);
			cv = new CommercialVessel("ship2", new Vector(300, 350), 40, 80);
			G.addEntity("traffic", (Entity) cv);
			cv = new CommercialVessel("ship3", new Vector(100, 10), 50, 135);
			G.addEntity("traffic", (Entity) cv);
			cv = new CommercialVessel("ship4", new Vector(SubMission.ScreenWidth - 50, SubMission.ScreenHeight - 50), 60, 315);
			G.addEntity("traffic", (Entity) cv);
			cv = new CommercialVessel("ship3", new Vector(SubMission.ScreenWidth / 2, SubMission.ScreenHeight / 2), 50, 90);
			G.addEntity("traffic", (Entity) cv);
			// generate enemy patrol boats
			G.removeLayer("military");
			G.addLayer("military");
			break;
			
		case 1: // deploy special forces & surge of enemies
			mission = new MissionTarget(new Vector(100f, 150f), 0);
			break;
		case 2: // rendezvous with special forces
			mission = new MissionTarget(new Vector(100f, 150f), 60);
			break;
		case 3: // escape
			mission = new MissionTarget(new Vector(SubMission.ScreenWidth - 100f, 100f), 60);
			break;
		default:
			break;
		}
	}
	
	void renderObjective(Graphics g) {
		switch (state) {
		case 0:
			g.drawString("Get to the mission target to deploy your special forces.", 75f, 15f);
			break;
		case 1:
			g.drawString("Remain undetected until your rendezvous window opens.", 75f, 15f);
			break;
		case 2:
			g.drawString("Rendezvous with your team before the enemy captures them!", 75f, 15f);
			break;
		case 3:
			g.drawString("The package has been wounded! Escape to open waters and get him to medical help!", 75f, 15f);
			break;
		}
	}
	
	boolean missionFail() {
		switch (state) {
		case 0:
		case 2:
		case 3:
			return (mission.getPercent() < 0f);
		default:
			return false;
		}
	}
	
	boolean missionWin() {
		return (state == 3 && mission.collides(player) != null);
	}
	
	boolean WinOrLose(SubMission G) {
		
		boolean shouldEnd = false;
		
		if (player.didRunAground(G.map)) {
			G.missionFailed = 10;
			shouldEnd = true;
		} else if (missionFail()) {
			G.missionFailed = state + 1;
			shouldEnd = true;
		} else if (missionWin()) {
			G.missionFailed = 0;
			shouldEnd = true;
		}
		
		return shouldEnd;
	}
	
	void DetectWithSonar(Vessel e) {
		switch (player.detect((Vessel) e)) {
		case 0:
			e.drawAlpha = 0f;
			break;
		case 1:
			e.drawAlpha = 0.33f;
			break;
		case 2:
			e.drawAlpha = 0.6f;
			break;
		case 3:
			e.drawAlpha = 1f;
			break;
		}
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game) {
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		SubMission G = (SubMission) game;
		g.drawImage(G.map, 0, 0);
		g.drawImage(G.depth, 0, 0);
		
		
		depth.render(g);
		speed.render(g);
		
		for (Entity e : G.getLayer("traffic"))
			((CommercialVessel) e).render(g);
		
		player.render(g);
		mission.render(g);
		
		g.setFont(G.text);
		renderObjective(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		SubMission G = (SubMission) game;
		float dt = delta / 1000f;
		
		mission.update(dt);
		if (WinOrLose(G)) 
			G.enterState(SubMission.GAMEOVERSTATE);
		
		// check for conditions to advance mission state
		if (advance()) {
			state += 1;
			stage(G);
		}
		
		float ambientNoise = G.getLayer("traffic").size() * 50 + G.getLayer("military").size() * 20;
		//System.out.println(ambientNoise);
		
		// draw depth lines or land depending on submarine depth
		int d = (int) (player.getDepth() / 100);
		if (d > 0)
			G.depth = SubMission.getImage("d" + d);
		else
			G.depth = SubMission.getImage("land");
		
		// handle player input on depth/speed bars
		Input input = container.getInput();
		player.setDepth(depth.update(input, (int) player.getDepth()));
		player.setSpeed(speed.update(input, (int) player.getSpeed()));
		
		player.update(input, ambientNoise, dt);

		// submarine sonar affects how ships are drawn
		sonarCountdown -= dt;
		for (Entity e : G.getLayer("traffic")) {
			((CommercialVessel) e).update(dt);
			if (sonarCountdown <= 0)
				DetectWithSonar((Vessel) e);
		}
		for (Entity e : G.getLayer("military")) {
			((MilitaryVessel) e).update(dt);
			if (sonarCountdown <= 0)
				DetectWithSonar((Vessel) e);
		}
		if (sonarCountdown <= 0)
			sonarCountdown = 1;
	}

	@Override
	public int getID() {
		return SubMission.PLAYINGSTATE;
	}
	
}