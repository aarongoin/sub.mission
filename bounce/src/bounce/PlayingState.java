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
		BounceGame bg = (BounceGame) game;
		bounces = 0;
		prepareLevel(bg);
		container.setSoundOn(true);
	}
	
	private void prepareLevel(BounceGame bg) {
		bg.ball.setPosition(bg.paddle.getPosition().add(bg.paddle.getNormal().scale(300)));
		bg.ball.setVelocity(new Vector(0, 0));
		
		switch(bg.getLevel()) {
		case 1:
			bg.belt3.generateAsteroids("C", 10);
			bg.belt2.generateAsteroids("M", 0);
			bg.belt1.generateAsteroids("S", 0);
			break;
		case 2:
			bg.belt3.generateAsteroids("C", 20);
			bg.belt2.generateAsteroids("M", 0);
			bg.belt1.generateAsteroids("S", 0);
			break;
		case 3:
			bg.belt3.generateAsteroids("C", 0);
			bg.belt2.generateAsteroids("M", 10);
			bg.belt1.generateAsteroids("S", 20);
			break;
		case 4:
			bg.belt3.generateAsteroids("C", 30);
			bg.belt2.generateAsteroids("M", 10);
			bg.belt1.generateAsteroids("S", 0);
			break;
		case 5:
			bg.belt3.generateAsteroids("C", 0);
			bg.belt2.generateAsteroids("M", 20);
			bg.belt1.generateAsteroids("S", 30);
			break;
		case 6:
			bg.belt3.generateAsteroids("C", 45);
			bg.belt2.generateAsteroids("M", 15);
			bg.belt1.generateAsteroids("S", 0);
			break;
		case 7:
			bg.belt3.generateAsteroids("C", 45);
			bg.belt2.generateAsteroids("M", 10);
			bg.belt1.generateAsteroids("S", 15);
			break;
		case 8:
			bg.belt3.generateAsteroids("C", 45);
			bg.belt2.generateAsteroids("M", 10);
			bg.belt1.generateAsteroids("S", 25);
			break;
		case 9:
			bg.belt3.generateAsteroids("C", 50);
			bg.belt2.generateAsteroids("M", 15);
			bg.belt1.generateAsteroids("S", 25);
			break;
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		
		bg.sun.render(g);
		bg.belt1.render(g);
		bg.belt2.render(g);
		bg.belt3.render(g);
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
		
		bg.belt1.ballCollision(bg.ball);
		bg.belt2.ballCollision(bg.ball);
		bg.belt3.ballCollision(bg.ball);
		
		if ((bg.belt1.getCount() + bg.belt2.getCount() + bg.belt3.getCount()) == 0) {
			bg.setLevel(bg.getLevel() + 1);
			prepareLevel(bg);
		}
		
		bg.sun.update(dt);
		bg.belt1.update(dt);
		bg.belt2.update(dt);
		bg.belt3.update(dt);
	}

	@Override
	public int getID() {
		return BounceGame.PLAYINGSTATE;
	}
	
}