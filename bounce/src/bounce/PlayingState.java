package bounce;

import java.util.Iterator;

import jig.ResourceManager;
import jig.Vector;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
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
	int asteroids;
	
	Sound background;
		
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		background = ResourceManager.getSound(BounceGame.RECORD_SND);
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		BounceGame bg = (BounceGame) game;
		container.setSoundOn(true);
		bounces = 0;
		prepareLevel(bg);
		bg.didWin = false;
		background.loop();
		bg.score = 0;
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game) {
		background.stop();
	}
	
	private void resetBall(BounceGame bg) {
		bg.ball.setPosition( bg.paddle.getPosition().add( bg.paddle.getNormal().scale(100) ) );
		bg.ball.setVelocity(bg.paddle.getNormal().scale(-8f));
		bg.sun.addChild(bg.ball);
	}
	
	public void updateScore(BounceGame bg) {
		int levelScore = ( bg.getLevel() * 10 ) / ( 3 - bg.ball.getLives() + 1 );
		bg.score += levelScore;
	}
	
	private void prepareLevel(BounceGame bg) {
		bg.ball.setLives(3);
		resetBall(bg);
		bg.sun.removeDebris();
		
		switch(bg.getLevel()) {
		case 1:
			asteroids = 10;
			bg.belt3.generateAsteroids("C", 10);
			bg.belt1.generateAsteroids("M", 0);
			bg.belt2.generateAsteroids("S", 0);
			break;
		case 2:
			asteroids = 20;
			bg.belt3.generateAsteroids("C", 20);
			bg.belt1.generateAsteroids("M", 0);
			bg.belt2.generateAsteroids("S", 0);
			break;
		case 3:
			asteroids = 30;
			bg.belt3.generateAsteroids("C", 0);
			bg.belt1.generateAsteroids("M", 10);
			bg.belt2.generateAsteroids("S", 20);
			break;
		case 4:
			asteroids = 40;
			bg.belt3.generateAsteroids("C", 30);
			bg.belt1.generateAsteroids("M", 10);
			bg.belt2.generateAsteroids("S", 0);
			break;
		case 5:
			asteroids = 50;
			bg.belt3.generateAsteroids("C", 0);
			bg.belt1.generateAsteroids("M", 20);
			bg.belt2.generateAsteroids("S", 30);
			break;
		case 6:
			asteroids = 60;
			bg.belt3.generateAsteroids("C", 45);
			bg.belt1.generateAsteroids("M", 15);
			bg.belt2.generateAsteroids("S", 0);
			break;
		case 7:
			asteroids = 70;
			bg.belt3.generateAsteroids("C", 45);
			bg.belt2.generateAsteroids("M", 10);
			bg.belt1.generateAsteroids("S", 15);
			break;
		case 8:
			asteroids = 80;
			bg.belt3.generateAsteroids("C", 45);
			bg.belt1.generateAsteroids("M", 10);
			bg.belt2.generateAsteroids("S", 25);
			break;
		case 9:
			asteroids = 90;
			bg.belt3.generateAsteroids("C", 50);
			bg.belt1.generateAsteroids("M", 15);
			bg.belt2.generateAsteroids("S", 30);
			break;
		}
		ResourceManager.getSound(BounceGame.GONG_SND).play();
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		
		bg.sun.render(g);
		bg.belt1.render(g);
		bg.belt2.render(g);
		bg.belt3.render(g);
		bg.paddle.render(g);
		
		g.setFont(bg.text);
		g.drawString("Level: " + bg.level + "    Lives: " + bg.ball.getLives(), 63, 25);
		g.drawString("Score: " + bg.score, 1150, 25);
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		BounceGame bg = (BounceGame) game;
		float dt = delta / 16.666666666666667f;
		
		//System.out.print("Delta: " + delta + " dt: " + dt + "\n\n");

		Input input = container.getInput();
		
		if (input.isKeyDown(Input.KEY_0))
			bg.enterState(bg.STARTUPSTATE);

		bg.paddle.update(new Vector(input.getMouseX(), input.getMouseY()));
				
		if (bg.ball.collides(bg.paddle) != null) bg.paddle.reflectBall(bg.ball);
		
		bg.belt1.ballCollision(bg.ball);
		bg.belt2.ballCollision(bg.ball);
		bg.belt3.ballCollision(bg.ball);
		
		asteroids = bg.belt1.getCount() + bg.belt2.getCount() + bg.belt3.getCount();
		if (asteroids == 0) {
			updateScore(bg);
			if (bg.getLevel() == 9) {
				bg.didWin = true;
				bg.enterState(bg.GAMEOVERSTATE);
			} else {
				bg.setLevel(bg.getLevel() + 1);
				prepareLevel(bg);
			}
		}
		
		bg.sun.update(dt);
		bg.belt1.update(dt);
		bg.belt2.update(dt);
		bg.belt3.update(dt);
		
		bg.belt1.beltCollisions(bg.belt2);
		bg.belt1.beltCollisions(bg.belt3);
		bg.belt2.beltCollisions(bg.belt3);
		
		if (bg.ball.getLives() == 0) 
			bg.enterState(bg.GAMEOVERSTATE);
		else if (bg.sun.resetBall)
			resetBall(bg);
			
	}

	@Override
	public int getID() {
		return BounceGame.PLAYINGSTATE;
	}
	
}