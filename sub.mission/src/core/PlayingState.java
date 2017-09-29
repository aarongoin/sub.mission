package core;



import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import jig.Vector;


class PlayingState extends BasicGameState {
	
	DepthMeter depth;
	SpeedMeter speed;
	
	
	float currentDepth;
	int targetDepth;
	float currentSpeed;
	int targetSpeed;

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		SubMission G = (SubMission) game;
		
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		// generate UI
		targetDepth = 100;
		targetSpeed = 10;
		currentDepth = 50;
		currentSpeed = 10;
		depth = new DepthMeter(100, new Vector(SubMission.ScreenWidth - 24, 12));
		speed = new SpeedMeter(10, new Vector(SubMission.ScreenWidth - 415, SubMission.ScreenHeight - 24));
		
		// insert submarine
		
		// generate commercial traffic
		
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
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		SubMission G = (SubMission) game;
		float dt = delta / 1000f;
		
		//System.out.println((currentDepth < targetDepth));
		if (currentSpeed < targetSpeed) {
			currentSpeed += 1*dt;
			if (currentSpeed > targetSpeed)
				currentSpeed = targetSpeed;
		} else if (currentSpeed > targetSpeed) {
			currentSpeed -= 1*dt;
			if (currentSpeed < targetSpeed)
				currentSpeed = targetSpeed;
		}
		
		if (currentDepth < targetDepth) {
			currentDepth += 10*dt;
			if (currentDepth > targetDepth)
				currentDepth = targetDepth;
		} else if (currentDepth > targetDepth) {
			currentDepth -= 10*dt;
			if (currentDepth < targetDepth)
				currentDepth = targetDepth;
		}
		
		int d = (int) (currentDepth / 100);
		if (d > 0)
			G.depth = SubMission.getImage("d" + d);
		else
			G.depth = SubMission.getImage("land");
		
		Input input = container.getInput();
		targetDepth = depth.update(input, (int) currentDepth);
		targetSpeed = speed.update(input, (int) currentSpeed);
		//for (Entity e : G.getLayer("patrol"))
		//	e.update(dt);
	}

	@Override
	public int getID() {
		return SubMission.PLAYINGSTATE;
	}
	
}