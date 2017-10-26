package core;



import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import entities.Airplane;
import entities.CommercialVessel;
import entities.MissionTarget;
import entities.Submarine;
import entities.Torpedo;
import entities.Vessel;
import jig.Vector;
import jig.Entity;


class PlayingState extends BasicGameState {
	
	
	DepthMeter depth;
	MissionTarget mission;
	
	SubPlatform platform;
	Submarine player;
	
	float sonarCountdown;
	SpeedMeter speed;

	int state;
	
	Airplane airSupport;
	
	CommercialManager trafficManager;
	PatrolManager patrolManager;
	
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
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		SubMission G = (SubMission) game;
		
		sonarCountdown = 0;
		
		// insert submarine
		player = new Submarine(75, 10);
		//player.debug(true);
		
		// generate UI
		depth = new DepthMeter((int) player.getDepth(), new Vector(SubMission.ScreenWidth - 24, 12));
		speed = new SpeedMeter((int) player.getSpeed(), new Vector(SubMission.ScreenWidth - 415, SubMission.ScreenHeight - 24));
		platform = new SubPlatform(player);
		
		SubMission.player = player;
		
		trafficManager = new CommercialManager(SubMission.shippingLanes);
		
		airSupport = new Airplane(60, 30);
		
		patrolManager = new PatrolManager(SubMission.patrolZones, new Vector(400, 400), new Vector(175, -50));
		
		state = 0;
		stage(G);
		
		/*System.out.println("theta( x, 0): " + new Vector( 1, 0).getRotation()
					   + ", theta( 0, y): " + new Vector( 0, 1).getRotation()
					   + ", theta(-x, 0): " + new Vector(-1, 0).getRotation()
					   + ", theta(-x,-y): " + new Vector(-1,-1).getRotation()
					   + ", theta( 0,-y): " + new Vector( 0,-1).getRotation()
					   + ", theta( x,-y): " + new Vector( 1,-1).getRotation());
		*/
	}
	
	@Override
	public int getID() {
		return SubMission.PLAYINGSTATE;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		//SubMission G = (SubMission) game;
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game) {
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
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		SubMission G = (SubMission) game;
		g.drawImage(SubMission.map, 0, 0);
		g.drawImage(G.depth, 0, 0);
		
		trafficManager.render(g);
		patrolManager.render(g);
		
		for (Entity e : SubMission.getLayer("torpedo")) {
			((Torpedo) e).render(g);
		}
		
		player.render(g);
		mission.render(g);
		
		g.setFont(SubMission.text);
		renderObjective(g);
		depth.render(g);
		speed.render(g);
		platform.render(g);
		
		/*
		for (int[] l : SubMission.landMasses) {
			g.drawOval(l[0] - l[2], l[1] - l[2], l[2] * 2, l[2] * 2);
		}
		int w = SubMission.ScreenWidth / 45;
		int h = SubMission.ScreenHeight / 50;
		int o;
		for (int x=0; x < w; x++) {
			o = (x % 2 == 0) ? 25 : 0;
			for (int y=0; y < h; y++) {
				g.drawOval(x*45, y*50-o, 50, 50);
			}
		}*/
		
	}
	
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
	
	void stage(SubMission G) {
		switch (state) {
		
		case 0: // mission start
			mission = new MissionTarget(new Vector(80f, 550f), 300f);
			// generate commercial traffic
			trafficManager.reset();
			trafficManager.setTraffic(10);
			// generate enemy patrol boats
			patrolManager.reset();
			patrolManager.setPatrol(5);
			SubMission.removeLayer("torpedo");
			SubMission.addLayer("torpedo");
			break;
			
		case 1: // deploy special forces & surge of enemies
			mission = new MissionTarget(new Vector(300f, 200f), 60);
			break;
		case 2: // rendezvous with special forces
			airSupport.deployAt(new Vector(700, 425), Vector.getRandomXY(-1, 1, -1, 1));
			mission = new MissionTarget(new Vector(100f, 150f), 60);
			break;
		case 3: // escape
			mission = new MissionTarget(new Vector(SubMission.ScreenWidth - 100f, 100f), 120);
			break;
		default:
			break;
		}
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
		
		float ambientNoise = SubMission.getLayer("traffic").size() + SubMission.getLayer("patrol").size() * 20;
		//System.out.println(ambientNoise);
		
		// draw depth lines or land depending on submarine depth
		int d = (int) (player.getDepth() / 100);
		if (d > 0) G.depth = SubMission.getImage("d" + d);
		else G.depth = SubMission.getImage("land");
		
		// handle player input on depth/speed bars
		Input input = container.getInput();
		boolean mouse = input.isMousePressed(Input.MOUSE_LEFT_BUTTON);
		player.setDepth( depth.update(input, (int) player.getDepth(), mouse) );
		player.setSpeed( speed.update(input, (int) player.getSpeed(), mouse) );
		player.setTowState( platform.update(input, dt, mouse) );
		
		player.update(input, ambientNoise, dt, mouse);
			
		trafficManager.update(dt, input, mouse);
		patrolManager.update(dt, input, ambientNoise, mouse);
		
		for (Entity e : SubMission.getLayer("torpedo")) {
			((Torpedo) e).update(dt);
			if (((Vessel) e).didRunAground(SubMission.map) || !((Torpedo) e).haveFuel())
				SubMission.removeEntity("torpedo", e);
		}
		
		G.update();
	}

	boolean WinOrLose(SubMission G) {
		
		boolean shouldEnd = false;
		
		if (player.didRunAground(SubMission.map)) {
			G.missionFailed = 10;
			shouldEnd = true;
		} else if (player.isSunk) {
			G.missionFailed = 6;
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
	
}