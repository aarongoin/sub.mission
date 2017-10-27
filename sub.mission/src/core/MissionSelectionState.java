package core;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class MissionSelectionState extends BasicGameState {

	Button mission1;
	Button mission2;
	Button menu;
	Button begin;
	
	int mission;

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		mission1 = new Button(container, SubMission.text, "Mission 1", SubMission.ScreenWidth / 2 - 100, 100, 8);
		mission2 = new Button(container, SubMission.text, "Mission 2", SubMission.ScreenWidth / 2 + 100, 100, 8);
		
		menu = new Button(container, SubMission.text, "Menu", SubMission.ScreenWidth / 2 - 200, SubMission.ScreenHeight - 100, 8);
		begin = new Button(container, SubMission.text, "Begin", SubMission.ScreenWidth / 2 + 200, SubMission.ScreenHeight - 100, 8);
		
		mission = 1;
		SubMission.missionManager = new CommercialMission();
		mission2.setColor(Color.gray);
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		SubMission G = (SubMission) game;
		
		g.drawImage(SubMission.map, 0, 0);
		g.drawImage(G.depth, 0, 0);
		
		g.setColor(new Color(0, 0, 0, 0.75f));
		g.fillRect(0, 0, SubMission.ScreenWidth, SubMission.ScreenHeight);
		g.setColor(Color.white);
		
		mission1.render(g);
		mission2.render(g);
		
		g.setColor(Color.white);
		SubMission.missionManager.renderMission(g);
		
		menu.render(g);
		begin.render(g);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		Input input = container.getInput();
		boolean mouse = input.isMousePressed(Input.MOUSE_LEFT_BUTTON);
		
		if (menu.clicked(input, mouse))
			game.enterState(SubMission.MENUSTATE);
		else if (begin.clicked(input, mouse))
			game.enterState(SubMission.PLAYINGSTATE);
		else if (mission == 1 && mission2.clicked(input, mouse)) {
			mission = 2;
			mission1.setColor(Color.gray);
			mission2.setColor(Color.white);
			SubMission.missionManager = new SealTeamMission();
		} else if (mission == 2 && mission1.clicked(input, mouse)) {
			mission = 1;
			mission1.setColor(Color.white);
			mission2.setColor(Color.gray);
			SubMission.missionManager = new CommercialMission();
		}

	}

	@Override
	public int getID() {
		return SubMission.MISSIONSELECTIONSTATE;
	}

}
