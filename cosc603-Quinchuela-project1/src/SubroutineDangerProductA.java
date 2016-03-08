

// TODO: Auto-generated Javadoc
/**
 * The Class SubroutineDangerProductA. 
 * Extracted Class where related methods are contained in their related classes
 */
public class SubroutineDangerProductA {
	
	/** A indicators for dry-wet  */
	private double[] A = { 30.0, 19.2, 13.8, 22.5 };

	/**
	 * A.  Calculates the difference between the dry and wet bulb temperature according to indicator A
	 *
	 * @param dry the dry bulb temperature 
	 * @param wet the wet bulb temperature
	 * @return the double value for A 
	 */
	public double a(double dry, double wet) {
		double dif = dry - wet;
		double a = 0;
		if (dif < 4.5) {
			a = A[0];
		} else if (dif < 12.5) {
			a = A[1];
		} else if (dif < 27.5) {
			a = A[2];
		} else {
			a = A[3];
		}
		return a;
	}
}