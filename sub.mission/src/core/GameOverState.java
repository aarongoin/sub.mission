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
	
	private float timer;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		SubMission G = (SubMission) game;
		timer = 1000f;

		G.depth = SubMission.getImage("land");
	}
	
	@Override
	public void leave(GameContainer container, StateBasedGame game) {
	}
		
	public void setUserScore(int bounces) {
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		SubMission G = (SubMission) game;
		
		g.drawImage(G.map, 0, 0);
		g.drawImage(G.depth, 0, 0);
		
		g.setFont(G.subtitle);
		if (G.missionFailed > 0)
			g.drawString("Mission Failed.", SubMission.ScreenWidth / 2 - 90, SubMission.ScreenHeight / 2 - 30);
		else
			g.drawString("Mission Complete!", SubMission.ScreenWidth / 2 - 110, SubMission.ScreenHeight / 2 - 30);
		
		g.setFont(G.text);
		switch (G.missionFailed) {
		case 0:
			g.drawString("Great Job, Captain.", SubMission.ScreenWidth / 2 - 82, SubMission.ScreenHeight / 2 + 30);
			break;
		case 1:
			g.drawString("You need to deploy your team before the window closes!", SubMission.ScreenWidth / 2 - 230, SubMission.ScreenHeight / 2 + 30);
			break;
		case 3:
			g.drawString("Your team has been captured!", SubMission.ScreenWidth / 2 - 125, SubMission.ScreenHeight / 2 + 30);
			break;
		case 4:
			g.drawString("You failed to escape enemy waters in time!", SubMission.ScreenWidth / 2 - 175, SubMission.ScreenHeight / 2 + 30);
			break;
		case 10:
			g.drawString("You ran aground!", SubMission.ScreenWidth / 2 - 72, SubMission.ScreenHeight / 2 + 30);
			break;
		}
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