package bounce;

import jig.Vector;

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
	public void init(GameContainer container, StateBasedGame game) throws SlickException { 
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		BounceGame bg = (BounceGame)game;
		
		container.setSoundOn(false);
		
		bg.belt2.generateAsteroids("S", 25);
		bg.belt1.generateAsteroids("M", 15);
		bg.belt3.generateAsteroids("C", 50);
	}


	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		
		//bg.paddle.render(g);
		bg.sun.render(g);
		bg.belt1.render(g);
		bg.belt2.render(g);
		bg.belt3.render(g);

		g.setFont(bg.title);
		g.drawString("BREAKOUT", 60, 15);
		
		g.setFont(bg.text);
		g.drawString("By: Aaron Goin", 63, 85);
		
		g.drawString("Aim the paddle with your mouse to direct the ball.", 63, 675);
		g.drawString("Hit asteroids with the ball to destroy them.", 63, 700);
		g.drawString("Destroy all the asteroids to complete each level.", 63, 725);
		g.drawString("Beat every level to win!", 63, 750);
		
		g.drawString("Use NUM KEYS 1-9 to select a level.", bg.ScreenWidth - 365, 25);
		g.drawString("Use NUM KEY 0 to return to this screen.", bg.ScreenWidth - 395, 750);
		
		g.drawString("Level " + bg.level, bg.ScreenWidth / 2 - 28, bg.ScreenHeight / 2 - 60);
		g.drawString("press space to play", bg.ScreenWidth / 2 - 75, bg.ScreenHeight / 2 + 30);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

		BounceGame bg = (BounceGame) game;
		float dt = delta / 16.666666666666667f;
		
		Input input = container.getInput();
		bg.paddle.update(new Vector(input.getMouseX(), input.getMouseY()));

		if (input.isKeyDown(Input.KEY_SPACE))
			bg.enterState(BounceGame.PLAYINGSTATE);
		else if (input.isKeyDown(Input.KEY_1))
			bg.setLevel(1);
		else if (input.isKeyDown(Input.KEY_2))
			bg.setLevel(2);
		else if (input.isKeyDown(Input.KEY_3))
			bg.setLevel(3);
		else if (input.isKeyDown(Input.KEY_4))
			bg.setLevel(4);
		else if (input.isKeyDown(Input.KEY_5))
			bg.setLevel(5);
		else if (input.isKeyDown(Input.KEY_6))
			bg.setLevel(6);
		else if (input.isKeyDown(Input.KEY_7))
			bg.setLevel(7);
		else if (input.isKeyDown(Input.KEY_8))
			bg.setLevel(8);
		else if (input.isKeyDown(Input.KEY_9))
			bg.setLevel(9);

		//bg.sun.update(dt);
		bg.belt1.update(dt);
		bg.belt2.update(dt);
		bg.belt3.update(dt);
		
		bg.belt1.beltCollisions(bg.belt2);
		bg.belt1.beltCollisions(bg.belt3);
		bg.belt2.beltCollisions(bg.belt3);
	}

	@Override
	public int getID() {
		return BounceGame.STARTUPSTATE;
	}
	
}