package bounce;

import jig.Vector;

public class VectorUtil {

	public VectorUtil() {
	}

	public static Vector changeBasis(Vector v, Vector[] basis) {
		return new Vector(
			v.getX()*basis[0].getX() + v.getY()*basis[1].getX(),
			v.getX()*basis[0].getY() + v.getY()*basis[1].getY()
		);
	}
	
	public static Vector[] invertBasis(Vector[] basis) {
		
		// invert basis to convert back to standard basis
		float detB = 1 / ( basis[0].getX() * basis[1].getY() - basis[0].getY() * basis[1].getX() );
		Vector inverse[] = { new Vector( detB * basis[1].getY() , detB * basis[0].getY() * -1 ), new Vector( detB * basis[1].getX() * -1 , detB * basis[0].getX() ) };
		
		return inverse;
	}
	
	public static Vector[] getBasis(Vector a, Vector b) {
		
		Vector X = new Vector(a.getX() - b.getX(), a.getY() - b.getY()).unit();
		Vector[] result = {
			X,
			X.getPerpendicular().scale(-1)
		};
		
		return result;
	}
}
