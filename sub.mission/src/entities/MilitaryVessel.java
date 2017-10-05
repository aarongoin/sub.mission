package entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import java.util.Random;

import jig.Vector;

public class MilitaryVessel extends Vessel {
	
	protected float baseSonar;
	float ambient;
	Random rand;

	public MilitaryVessel(String image, Vector p, float noise, float sonar, float bearing, float speed, float radius, float accel) {
		super(image, p, noise, bearing, speed, radius, accel);
		
		baseSonar = sonar;
		ambient = 0;
		
		rand = new Random(System.currentTimeMillis());
	}
	
	public float getSonar() {
		return (175 * baseSonar - ambient - currentSpeed * 8);
	}
	
	public void update(float dt, float ambient) {
		this.ambient = ambient;
		super.update(dt);
	}
	
	public int detect(Vessel other) {
		
		float theta = Math.abs((float) (getNose().subtract(getPosition()).getRotation() - other.getPosition().subtract(getPosition()).getRotation()));
		if (theta > 157.5 && theta < 202.5)
			return 0;
		
		float distance = getPosition().distance(other.getPosition());
		float sonar = getSonar();
		float span = sonar + other.getNoise();

		if (distance < span) {
			int random = rand.nextInt((int) (distance + other.getNoise()));
			if (random <= sonar * 2 / 3)
				return 3;
			else if (random <= sonar)
				return 2;
			else
				return 1;
		}
		
		return 0;
	}

	
	@Override
	public void render(Graphics g) {
		if (debug) {
			float sonar = getSonar();
			g.setColor(Color.green);
			g.drawOval(getPosition().getX() - sonar, getPosition().getY() - sonar, sonar * 2, sonar * 2);
			
			g.setColor(Color.white);
		}
		super.render(g);
	}
}
