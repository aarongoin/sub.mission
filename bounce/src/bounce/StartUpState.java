package bounce;

import java.util.Iterator;

import jig.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
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
class StartUpState extends BasicGameState {

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		container.setSoundOn(false);
	}


	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		
		bg.ball.render(g);
		bg.ballTest.render(g);
		g.drawString("Bounces: ?", 10, 30);
		for (Bang b : bg.explosions)
			b.render(g);
		g.drawImage(ResourceManager.getImage(BounceGame.STARTUP_BANNER_RSC),
				225, 270);		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

		float dt = delta / 16.666666666666667f;
		
		Input input = container.getInput();
		BounceGame bg = (BounceGame)game;

		if (input.isKeyDown(Input.KEY_SPACE))
			bg.enterState(BounceGame.PLAYINGSTATE);	
		
		// bounce the ball...
		boolean bounced = false;
		if (bg.ball.getCoarseGrainedMaxX() > bg.ScreenWidth
				|| bg.ball.getCoarseGrainedMinX() < 0) {
			bg.ball.bounce(90);
			bounced = true;
		} else if (bg.ball.getCoarseGrainedMaxY() > bg.ScreenHeight
				|| bg.ball.getCoarseGrainedMinY() < 0) {
			bg.ball.bounce(0);
			bounced = true;
		}
		if (bounced) {
			bg.explosions.add(new Bang(bg.ball.getX(), bg.ball.getY()));
		}
		bg.ball.update(dt);
		
		// bounce the test ball...
		bounced = false;
		if (bg.ballTest.getCoarseGrainedMaxX() > bg.ScreenWidth
				|| bg.ballTest.getCoarseGrainedMinX() < 0) {
			bg.ballTest.bounce(90);
			bounced = true;
		} else if (bg.ballTest.getCoarseGrainedMaxY() > bg.ScreenHeight
				|| bg.ballTest.getCoarseGrainedMinY() < 0) {
			bg.ballTest.bounce(0);
			bounced = true;
		}
		if (bounced) {
			bg.explosions.add(new Bang(bg.ballTest.getX(), bg.ballTest.getY()));
		}
		bg.ballTest.update(dt);

		// check if there are any finished explosions, if so remove them
		for (Iterator<Bang> i = bg.explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}

	}

	@Override
	public int getID() {
		return BounceGame.STARTUPSTATE;
	}
	
}