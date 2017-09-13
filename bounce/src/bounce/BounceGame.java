package bounce;

import java.awt.Font;

import jig.Entity;
import jig.Vector;
import jig.ResourceManager;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A Simple Game of Bounce.
 * 
 * The game has three states: StartUp, Playing, and GameOver, the game
 * progresses through these states based on the user's input and the events that
 * occur. Each state is modestly different in terms of what is displayed and
 * what input is accepted.
 * 
 * In the playing state, our game displays a moving rectangular "ball" that
 * bounces off the sides of the game container. The ball can be controlled by
 * input from the user.
 * 
 * When the ball bounces, it appears broken for a short time afterwards and an
 * explosion animation is played at the impact site to add a bit of eye-candy
 * additionally, we play a short explosion sound effect when the game is
 * actively being played.
 * 
 * Our game also tracks the number of bounces and syncs the game update loop
 * with the monitor's refresh rate.
 * 
 * Graphics resources courtesy of qubodup:
 * http://opengameart.org/content/bomb-explosion-animation
 * 
 * Sound resources courtesy of DJ Chronos:
 * http://www.freesound.org/people/DJ%20Chronos/sounds/123236/
 * 
 * 
 * @author wallaces
 * 
 */
public class BounceGame extends StateBasedGame {
	
	public static final int STARTUPSTATE = 0;
	public static final int PLAYINGSTATE = 1;
	public static final int GAMEOVERSTATE = 2;
	
	public static final String FU_SND = "bounce/resource/sound/fu.wav";
	public static final String CORK_SND = "bounce/resource/sound/cork.wav";
	public static final String GONG_SND = "bounce/resource/sound/gong.wav";
	public static final String RECORD_SND = "bounce/resource/sound/record.wav";
	public static final String RICOCHET_SND = "bounce/resource/sound/ricochet.wav";
	public static final String KAZOO_SND = "bounce/resource/sound/kazoo.wav";
	public static final String CLAP_SND = "bounce/resource/sound/clap.wav";
	
	public static final String SUN_RSC = "bounce/resource/sun.png";
	public static final String BALL_RSC = "bounce/resource/ball.png";
	public static final String PADDLE_RSC = "bounce/resource/paddle.png";
	public static final String ASTEROID_S_RSC = "bounce/resource/asteroid_s.png";
	public static final String ASTEROID_M_RSC = "bounce/resource/asteroid_m.png";
	public static final String ASTEROID_C_RSC = "bounce/resource/asteroid_c.png";
	public static final String DEBRIS_S_RSC = "bounce/resource/debris_s.png";
	public static final String DEBRIS_M_RSC = "bounce/resource/debris_m.png";
	public static final String DEBRIS_C_RSC = "bounce/resource/debris_c.png";

	public final int ScreenWidth;
	public final int ScreenHeight;
		
	Paddle paddle;
	Ball ball;
	Sun sun;
	
	Belt belt1;
	Belt belt2;
	Belt belt3;
	
	int level;
	int lives;
	
	public TrueTypeFont title;
	public TrueTypeFont subtitle;
	public TrueTypeFont text;
	
	public boolean didWin;
	public int score;

	/**
	 * Create the BounceGame frame, saving the width and height for later use.
	 * 
	 * @param title
	 *            the window's title
	 * @param width
	 *            the window's width
	 * @param height
	 *            the window's height
	 */
	public BounceGame(String title, int width, int height) {
		super(title);
		ScreenHeight = height;
		ScreenWidth = width;

		Entity.setCoarseGrainedCollisionBoundary(Entity.CIRCLE);
	}
	
	public void setLevel(int l) {
		level = l;
	}
	
	public int getLevel() {
		return level;
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		
		container.setShowFPS(false);
		
		addState(new StartUpState());
		addState(new GameOverState());
		addState(new PlayingState());
		
		// the sound resource takes a particularly long time to load,
		// we preload it here to (1) reduce latency when we first play it
		// and (2) because loading it will load the audio libraries and
		// unless that is done now, we can't *disable* sound as we
		// attempt to do in the startUp() method.
		ResourceManager.loadSound(FU_SND);
		ResourceManager.loadSound(CORK_SND);	
		ResourceManager.loadSound(GONG_SND);
		ResourceManager.loadSound(RECORD_SND);
		ResourceManager.loadSound(RICOCHET_SND);
		ResourceManager.loadSound(KAZOO_SND);
		ResourceManager.loadSound(CLAP_SND);

		// preload all the resources to avoid warnings & minimize latency...
		ResourceManager.loadImage(BALL_RSC);
		ResourceManager.loadImage(SUN_RSC);
		ResourceManager.loadImage(PADDLE_RSC);
		ResourceManager.loadImage(ASTEROID_S_RSC);
		ResourceManager.loadImage(ASTEROID_M_RSC);
		ResourceManager.loadImage(ASTEROID_C_RSC);
		ResourceManager.loadImage(DEBRIS_S_RSC);
		ResourceManager.loadImage(DEBRIS_M_RSC);
		ResourceManager.loadImage(DEBRIS_C_RSC);
		
		title = new TrueTypeFont(new Font("Verdana", Font.PLAIN, 60), true);
		subtitle = new TrueTypeFont(new Font("Verdana", Font.PLAIN, 24), true);
		text = new TrueTypeFont(new Font("Verdana", Font.PLAIN, 16), true);
		
		
		ball = new Ball(ScreenWidth / 2, ScreenHeight / 5, 0f, 0f, 4f);
		paddle = new Paddle(new Vector(ScreenWidth / 2, ScreenHeight / 2), 40f, 1.2f);
		sun = new Sun(new Vector(ScreenWidth / 2, ScreenHeight / 2), -0.5f);
				
		belt1 = new Belt(new Vector(ScreenWidth / 2, ScreenHeight / 2), -0.05f, 200, sun);
		
		belt2 = new Belt(new Vector(ScreenWidth / 2, ScreenHeight / 2), -0.05f, 250, sun);
		
		belt3 = new Belt(new Vector(ScreenWidth / 2, ScreenHeight / 2), -0.05f, 300, sun);

		level = 1;
		lives = 3;
	}
	
	public static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new BounceGame("Breakout", 1300, 800));
			app.setDisplayMode(1300, 800, false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}

	
}
