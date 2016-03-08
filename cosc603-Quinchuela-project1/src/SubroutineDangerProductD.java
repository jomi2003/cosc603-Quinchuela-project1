

// TODO: Auto-generated Javadoc
/**
 * The Class SubroutineDangerProductD.
 * Extracted Class where related methods are contained in their related classes
 */
public class SubroutineDangerProductD {
	
	/** The drying factor indicator */
	private double[] D = { 16.0, 10.0, 7.0, 5.0, 4.0, 3.0 };

	/**
	 * Calculates the drying factor. The drying factor indicator is added to the fine fuel moisture index.
	 *
	 * @param subroutineDanger the subroutine danger calling object to set the drying factor
	 * @param ffm the fine fuel moisture
	 */
	public void calculateDryingFactor(SubroutineDanger subroutineDanger, double ffm) {
		for (int i = 1; i <= 6; i++) {
			if (ffm - D[i - 1] > 0) {
				subroutineDanger.setDf(i - 1);
				return;
			}
		}
		subroutineDanger.setDf(7);
	}
}