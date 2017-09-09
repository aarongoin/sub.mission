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
		bg.belt.render(g);
		bg.paddle.render(g);
		
		g.drawString("Bounces: " + bounces, 10, 30);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		BounceGame bg = (BounceGame) game;
		float dt = delta / 16.666666666666667f;
		
		//System.out.print("Delta: " + delta + " dt: " + dt + "\n\n");

		Input input = container.getInput();
		bg.paddle.update(new Vector(input.getMouseX(), input.getMouseY()));
				
		if (bg.ball.collides(bg.paddle) != null) bg.paddle.reflectBall(bg.ball);
		
		bg.belt.ballCollision(bg.ball);
		bg.sun.update(dt);
		bg.belt.update(dt);
	}

	@Override
	public int getID() {
		return BounceGame.PLAYINGSTATE;
	}
	
}