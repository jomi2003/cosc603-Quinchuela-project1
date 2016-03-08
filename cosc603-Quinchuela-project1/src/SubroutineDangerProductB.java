

// TODO: Auto-generated Javadoc
/**
 * The Class SubroutineDangerProductB.
 * Extracted Class where related methods are contained in their related classes
 */
public class SubroutineDangerProductB {
	
	/** The B indicators for dry-wet */
	private double[] B = { -.1859, -.0859, -0.579, -.0774 };

	/**
	 * B.  Calculates the difference between the dry and wet bulb temperature according to indicator B
	 *
	 * @param dry the dry bulb temperature
	 * @param wet the wet bulb temperature
	 * @return the double value for B
	 */
	public double b(double dry, double wet) {
		double dif = dry - wet;
		double b = 0;
		if (dif < 4.5) {
			b = B[0];
		} else if (dif < 12.5) {
			b = B[1];
		} else if (dif < 27.5) {
			b = B[2];
		} else {
			b = B[3];
		}
		return b;
	}
}