package core;

import jig.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


/**
 * This state is active when the Game is over. In this state, the ball is
 * neither drawn nor updated; and a gameover banner is displayed. A timer
 * automatically transitions back to the StartUp State.
 * 
 * Transitions From PlayingState
 * 
 * Transitions To StartUpState
 */
class GameOverState extends BasicGameState {
	
	Sound endSound;
	private float timer;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		timer = 300f;

		endSound.play();
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game) {
		endSound.stop();
	}
		
	public void setUserScore(int bounces) {
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		SubMission G = (SubMission)game;
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		SubMission bg = (SubMission)game;
		float dt = delta / 16.666666666666667f;
		
		timer -= dt;
		if (timer <= 0)
			game.enterState(SubMission.MENUSTATE);

	}

	@Override
	public int getID() {
		return SubMission.GAMEOVERSTATE;
	}
	
}