package core;

import jig.ResourceManager;
import jig.Vector;

import java.awt.Font;
import java.awt.List;
import java.util.Collection;
import java.util.Stack;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * This state is active prior to the Game starting. In this state, sound is
 * turned off, and the bounce counter shows '?'. The user can only interact with
 * the game by pressing the SPACE key which transitions to the Playing State.
 * Otherwise, all game objects are rendered and updated normally.
 * 
 * Transitions From (Initialization), GameOverState
 * 
 * Transitions To PlayingState
 */
class LoadingState extends BasicGameState {
	
	int state;
	String message; 
	
	Stack<String> assets;
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		state = 1;
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		
		assets = new Stack<String>();
		message = "Loading Images.";
	}


	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		SubMission bg = (SubMission)game;
		
		g.setFont(bg.subtitle);
		g.drawString(message, 40, 40);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		SubMission G = (SubMission) game;
		switch(state) {
		case 1:
			assets.addAll(SubMission.IMG.values());
			while (assets.size() > 0)
				ResourceManager.loadImage(assets.pop());
			state += 1;
			message = "Loading Sounds.";
			break;
			
		case 2:
			assets.addAll(SubMission.SND.values());
			while (assets.size() > 0)
				ResourceManager.loadSound(assets.pop());
			state += 1;
			break;
			
		case 3:
			G.bg = SubMission.getSound("bg");
			G.map = SubMission.getImage("map");
			G.depth = SubMission.getImage("land");
			G.enterState(SubMission.MENUSTATE);
			break;
		}
	}

	@Override
	public int getID() {
		return SubMission.LOADINGSTATE;
	}
	
}