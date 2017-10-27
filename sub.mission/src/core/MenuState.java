package core;

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
class MenuState extends BasicGameState {
	
	
	int d;
	int dir;
	
	Button back;
	Button next;
	Button help;
	Button quit;
	Button start;
	
	int substate;
	
	float t;
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		SubMission G = (SubMission) game;
		// start background sound looping
		G.bg.loop();
		t = 0;
		d = 0;
		dir = 1;
		
		back = new Button(container, SubMission.text, "Back", SubMission.ScreenWidth / 2 - 200, SubMission.ScreenHeight - 100, 8);
		next = new Button(container, SubMission.text, "Next", SubMission.ScreenWidth / 2 + 200, SubMission.ScreenHeight - 100, 8);
		
		start = new Button(container, SubMission.text, "Start", SubMission.ScreenWidth / 2, SubMission.ScreenHeight / 2, 8);
		help = new Button(container, SubMission.text, "Instructions", SubMission.ScreenWidth / 2, SubMission.ScreenHeight / 2 + 60, 8);
		quit = new Button(container, SubMission.text, "Quit", SubMission.ScreenWidth / 2, SubMission.ScreenHeight / 2 + 120, 8);
		
		substate = 0;
		
		SubMission.missionManager = new CommercialMission(); // new SealTeamMission();
	}
	
	@Override
	public int getID() {
		return SubMission.MENUSTATE;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException { 
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		SubMission G = (SubMission) game;
		
		g.drawImage(SubMission.map, 0, 0);
		g.drawImage(G.depth, 0, 0);
		
		switch(substate) {
		case 0:
			g.setFont(SubMission.title);
			g.drawString("sub.mission", SubMission.ScreenWidth / 2 - 120, SubMission.ScreenHeight / 5 - 12);
			
			g.setFont(SubMission.text);
			g.drawString("by: Aaron Goin", SubMission.ScreenWidth / 2 - 50, SubMission.ScreenHeight / 5 + 40);
			
			start.render(g);
			help.render(g);
			quit.render(g);
			break;
		case 1:
			SubMission.missionManager.renderMission(g);
			
			back.render(g);
			next.render(g);
			break;
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		//float dt = delta / 1000f;
		
		Input input = container.getInput();
		boolean mouse = input.isMousePressed(Input.MOUSE_LEFT_BUTTON);
		if (substate == 0) {
			if (help.clicked(input, mouse))
				game.enterState(SubMission.INSTRUCTIONSTATE);
			else if (quit.clicked(input, mouse))
				container.exit();
			else if (start.clicked(input, mouse))
				substate = 1;
				
		} else if (substate == 1) {
			if (next.clicked(input, mouse))
				game.enterState(SubMission.PLAYINGSTATE);
			else if (back.clicked(input, mouse))
				substate = 0;
			
		}
	}
	
}