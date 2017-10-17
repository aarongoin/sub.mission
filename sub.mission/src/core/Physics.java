package core;

import jig.Vector;
import util.VectorUtil;

public class Physics {
	
	
	
	public static void advanceToPresent(float dt, FreeBody a, FreeBody b) {
		a.setPosition( a.getPosition().add( a.getVelocity().scale(dt) ) );
		b.setPosition( b.getPosition().add( b.getVelocity().scale(dt) ) );
	}
	
	public static boolean didCollide(Vector aP, Vector bP, float aR, float bR) {
		if (aP.distance(bP) <=  aR + bR) return true;
		else return false;
	}

	// handles perfectly elastic collisions between 2 round objects "a" and "b" using position (P), velocity (V), and mass (M)
	public static Vector[] elasticCollision(Vector Pa, Vector Pb, Vector Va, Vector Vb, double Ma, double Mb) {
		
		Vector[] basis = VectorUtil.getBasis(Pa, Pb);
		
		Vector A = VectorUtil.changeBasis(Va, basis);
		Vector B = VectorUtil.changeBasis(Vb, basis);
		
		//System.out.println("Positions: A= " + Pa + " B=" + Pb);
		//System.out.println("Velocities:  A=" + Va + " B=" + Vb + " (in original basis)");
		//System.out.println("Velocities:  A=" + A + " B=" + B + " (in collision basis)");
		
		double Va_new = ( ( Ma - Mb ) / ( Ma + Mb ) ) * A.getX() + ( 2 * Mb / ( Ma + Mb ) ) * B.getX();
		double Vb_new = ( 2 * Ma / ( Ma + Mb ) ) * A.getX() + ( ( Mb - Ma ) / ( Ma + Mb ) ) * B.getX();

		A = A.setX((float) Va_new);
		B = B.setX((float) Vb_new);
		
		basis = VectorUtil.invertBasis(basis);
		
		Vector[] result = {
			VectorUtil.changeBasis(A, basis),
			VectorUtil.changeBasis(B, basis)
		};
		
		//System.out.println("Post collision Velocities: A=" + A + " B=" + B + " (in collision basis)");
		//System.out.println("Post collision Velocities: A=" + result[0] + " B=" + result[1] + " (in original basis)");
		
		return result;
	}
		
	public static float rewindToCollision(FreeBody a, FreeBody b) {
		
		Vector distance = a.getPosition().subtract(b.getPosition());
		
		float dA = a.getVelocity().project(distance).length();
		float dB = b.getVelocity().project(distance).length();
		
		//System.out.println("Positions: A= " + a.getPosition() + " B=" + b.getPosition());
		
		float dt = ( a.getRadius() + b.getRadius() - distance.length() ) / ( dA + dB );

		//System.out.println("Rewinding: " + dt);
		
		a.setPosition( a.getPosition().add( a.getVelocity().scale(-dt) ) );
		b.setPosition( b.getPosition().add( b.getVelocity().scale(-dt) ) );
		
		//System.out.println("Rewind Positions: A= " + a.getPosition() + " B=" + b.getPosition());
		
		return dt;
	}
}
