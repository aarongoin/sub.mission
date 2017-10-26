package core;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
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
	public void enter(GameContainer container, StateBasedGame game) {
		timer = 1000f;
	}
	
	@Override
	public int getID() {
		return SubMission.GAMEOVERSTATE;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}
		
	@Override
	public void leave(GameContainer container, StateBasedGame game) {
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		SubMission G = (SubMission) game;
		
		g.drawImage(SubMission.map, 0, 0);
		g.drawImage(G.depth, 0, 0);
		
		
		// Draw mission end-state
		int width = SubMission.subtitle.getWidth(SubMission.missionManager.getMissionState());
	    int height = SubMission.subtitle.getHeight(SubMission.missionManager.getMissionState());
	    
	    g.setFont(SubMission.subtitle);
		g.drawString(SubMission.missionManager.getMissionState(), SubMission.ScreenWidth / 2 - width / 2, SubMission.ScreenHeight / 2 - 30 - height / 2);
	
		// Draw mission end-state message
		width = SubMission.text.getWidth(SubMission.missionManager.getEndMessage());
	    height = SubMission.text.getHeight(SubMission.missionManager.getEndMessage());
	    
	    g.setFont(SubMission.text);
		g.drawString(SubMission.missionManager.getEndMessage(), SubMission.ScreenWidth / 2 - width / 2, SubMission.ScreenHeight / 2 + 30 - height / 2);
	
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		float dt = delta / 16.666666666666667f;
		
		timer -= dt;
		if (timer <= 0)
			game.enterState(SubMission.MENUSTATE);

	}
	
}