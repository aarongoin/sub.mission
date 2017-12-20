package core;

import java.util.Random;

import org.newdawn.slick.Sound;

public class SoundManager {
	
	Random rand;
	Sound current;
	int sound;
	
	public SoundManager() {
		rand = new Random(System.currentTimeMillis());
		current = null;
	}
	
	public void update() {
		if ((current == null || !current.playing()) && rand.nextInt(3600) == 1) {
			// pick a random sound and play it
			int r = sound;
			while (r == sound) r = rand.nextInt(4);
			sound = r;
			current = SubMission.getSound("random_" + rand.nextInt(4));
			current.play(1f, 0.75f);
		}
	}
	public void stop() {
		current.stop();
	}
}