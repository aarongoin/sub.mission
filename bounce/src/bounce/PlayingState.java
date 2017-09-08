package bounce;

import java.util.Iterator;

import jig.Vector;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


/**
 * This state is active when the Game is being played. In this state, sound is
 * turned on, the bounce counter begins at 0 and increases until 10 at which
 * point a transition to the Game Over state is initiated. The user can also
 * control the ball using the WAS & D keys.
 * 
 * Transitions From StartUpState
 * 
 * Transitions To GameOverState
 */
class PlayingState extends BasicGameState {
	int bounces;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		bounces = 0;
		container.setSoundOn(true);
	}
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		
		bg.sun.render(g);
		//bg.ball.render(g);
		//bg.ballTest.render(g);
		g.drawString("Bounces: " + bounces, 10, 30);
		for (Bang b : bg.explosions)
			b.render(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
		float dt = delta / 16.666666666666667f;
		
		//System.out.print("Delta: " + delta + " dt: " + dt + "\n\n");

		Input input = container.getInput();
		BounceGame bg = (BounceGame) game;
		
		bg.ball.collision(bg.ballTest);
		
		if (input.isKeyDown(Input.KEY_W)) {
			bg.ball.setVelocity(bg.ball.getVelocity().add(new Vector(0f, -.1f)));
		}
		if (input.isKeyDown(Input.KEY_S)) {
			bg.ball.setVelocity(bg.ball.getVelocity().add(new Vector(0f, +.1f)));
		}
		if (input.isKeyDown(Input.KEY_A)) {
			bg.ball.setVelocity(bg.ball.getVelocity().add(new Vector(-.1f, 0)));
		}
		if (input.isKeyDown(Input.KEY_D)) {
			bg.ball.setVelocity(bg.ball.getVelocity().add(new Vector(+.1f, 0f)));
		}
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
			bounces++;
		}
		//bg.ball.update(dt);
		
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
		//bg.ballTest.update(dt);
		
		bg.sun.update(dt);

		// check if there are any finished explosions, if so remove them
		for (Iterator<Bang> i = bg.explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}
		/*
		if (bounces >= 10) {
			((GameOverState)game.getState(BounceGame.GAMEOVERSTATE)).setUserScore(bounces);
			game.enterState(BounceGame.GAMEOVERSTATE);
		}
		*/
	}

	@Override
	public int getID() {
		return BounceGame.PLAYINGSTATE;
	}
	
}