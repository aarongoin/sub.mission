package bounce;

import java.util.Iterator;

import jig.ResourceManager;
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
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		container.setSoundOn(false);
	}


	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		
		bg.paddle.render(g);
		bg.sun.render(g);
		bg.belt1.render(g);
		bg.belt2.render(g);
		bg.belt3.render(g);

		g.drawString("Bounces: ?", 10, 30);
		g.drawImage(ResourceManager.getImage(BounceGame.STARTUP_BANNER_RSC), 225, 270);	
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

	}

	@Override
	public int getID() {
		return BounceGame.STARTUPSTATE;
	}
	
}