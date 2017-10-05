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
import entities.Submarine;
import entities.Vessel;
import jig.Vector;
import jig.Entity;


class PlayingState extends BasicGameState {
	
	float sonarCountdown;
	
	DepthMeter depth;
	SpeedMeter speed;
	
	Submarine player;

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
		
		// set mission marker
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
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		SubMission G = (SubMission) game;
		float dt = delta / 1000f;
		float ambientNoise = G.getLayer("traffic").size() * 50;
		//System.out.println(ambientNoise);
		
		int d = (int) (player.getDepth() / 100);
		if (d > 0)
			G.depth = SubMission.getImage("d" + d);
		else
			G.depth = SubMission.getImage("land");
		
		Input input = container.getInput();
		player.setDepth(depth.update(input, (int) player.getDepth()));
		player.setSpeed(speed.update(input, (int) player.getSpeed()));
		
		player.update(input, ambientNoise, dt);
		
		if (player.didRunAground(G.map)) {
			G.missionFailed = 1;
			G.enterState(SubMission.GAMEOVERSTATE);
		}

		sonarCountdown -= dt;
		if (sonarCountdown <= 0) {
			sonarCountdown = 1;
			
		}

		for (Entity e : G.getLayer("traffic")) {
			((CommercialVessel) e).update(dt);
			
			if (sonarCountdown == 1) {
				int detect = player.detect((Vessel) e);
				switch (detect) {
				case 0:
					((Vessel) e).drawAlpha = 0f;
					break;
				case 1:
					((Vessel) e).drawAlpha = 0.33f;
					break;
				case 2:
					((Vessel) e).drawAlpha = 0.6f;
					break;
				case 3:
					((Vessel) e).drawAlpha = 1f;
					break;
				}
			}
		}
			
	}

	@Override
	public int getID() {
		return SubMission.PLAYINGSTATE;
	}
	
}