package core;



import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import jig.Vector;


class PlayingState extends BasicGameState {
	
	DepthMeter depth;
	SpeedMeter speed;
	SubPlatform platform;
	SoundManager soundManager;

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		
		// generate UI
		depth = new DepthMeter((int) SubMission.player.getDepth(), new Vector(SubMission.ScreenWidth - 24, 12));
		speed = new SpeedMeter((int) SubMission.player.getSpeed(), new Vector(SubMission.ScreenWidth - 415, SubMission.ScreenHeight - 24));
		platform = new SubPlatform(SubMission.player);
		soundManager = new SoundManager();
		
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
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		SubMission G = (SubMission) game;
		g.drawImage(SubMission.map, 0, 0);
		g.drawImage(G.depth, 0, 0);
		
		depth.render(g);
		speed.render(g);
		platform.render(g);
		
		SubMission.missionManager.render(g);
		
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

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		SubMission G = (SubMission) game;
		float dt = delta / 1000f;
				
		// draw depth lines or land depending on submarine depth
		int d = (int) (SubMission.player.getDepth() / 100);
		if (d > 0) G.depth = SubMission.getImage("d" + d);
		else G.depth = SubMission.getImage("land");
		
		// handle player input on depth/speed bars
		Input input = container.getInput();
		boolean mouse = input.isMousePressed(Input.MOUSE_LEFT_BUTTON);
		SubMission.player.setDepth( depth.update(input, (int) SubMission.player.getDepth(), mouse) );
		SubMission.player.setSpeed( speed.update(input, (int) SubMission.player.getSpeed(), mouse) );
		SubMission.player.setTowState( platform.update(input, dt, mouse) );
		
		soundManager.update();
		
		if (SubMission.missionManager.update(dt, input, mouse)) 
			G.enterState(SubMission.GAMEOVERSTATE);
		
		G.update();
	}
}