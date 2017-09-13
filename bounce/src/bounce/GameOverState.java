package bounce;

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
		
		if ( ((BounceGame)game).didWin )
			endSound =  ResourceManager.getSound(BounceGame.CLAP_SND);
		else
			endSound = ResourceManager.getSound(BounceGame.KAZOO_SND);
		
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
		BounceGame bg = (BounceGame)game;
		
		bg.belt1.render(g);
		bg.belt2.render(g);
		bg.belt3.render(g);

		g.setFont(bg.title);
		if (bg.didWin)
			g.drawString("YOU WIN!", bg.ScreenWidth / 2 - 145, bg.ScreenHeight / 2 - 40);
		else
			g.drawString("YOU LOSE!", bg.ScreenWidth / 2 - 155, bg.ScreenHeight / 2 - 40);
		
		g.setFont(bg.text);
		g.drawString("Score: " + bg.score, 1150, 25);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		BounceGame bg = (BounceGame)game;
		float dt = delta / 16.666666666666667f;
		
		timer -= dt;
		if (timer <= 0)
			game.enterState(BounceGame.STARTUPSTATE);

		bg.belt1.update(dt);
		bg.belt2.update(dt);
		bg.belt3.update(dt);
		
		bg.belt1.beltCollisions(bg.belt2);
		bg.belt1.beltCollisions(bg.belt3);
		bg.belt2.beltCollisions(bg.belt3);
	}

	@Override
	public int getID() {
		return BounceGame.GAMEOVERSTATE;
	}
	
}