/**
 * Routing for computing national fire danger ratings and fire load index 
 * Data needed for the calculations are:
 * dry,  	dry bulb temperature
 * wet,  	wet bulb temperature
 * isnow, 	some positive non zero number if there is snow on the ground									 
 * wind,  	the current wind speed in miles per hour
 * buo,		the last value of the build up index
 * iherb,	the current herb state of the district 1=cured, 2=transition, 3=green
 * Data returned from the subroutine are:
 * Drying Factor as 							df
 * Fine Fuel Moisture as 						ffm
 * Adjusted (10 day lag) Fuel Moisture as 		adfm
 * Grass Spread Index as 						grass
 * Timber Spread Index as 						timber
 * Fire Load Rating (man hour base) as 			fload
 * Build Up Index as 							bui
 * 
 */

/**
 * @author JQ  
 * @version 1.0 
 */

import java.lang.reflect.Array;
import java.math.*;

// TODO: Auto-generated Javadoc
/**
 * The Class SubroutineDanger. It calculates df, ffm, adfm, grass, timber, fload
 * and bui indices.
 */
public class SubroutineDanger {

	/**
	 * Drying Factor as df Fine Fuel Moisture as ffm Adjusted (10 day lag) Fuel
	 * Moisture as adfm Grass Spread Index as grass Timber Spread Index as
	 * timber Fire Load Rating (man hour base) as fload Build Up Index as bui
	 */
	double df, ffm, adfm, grass, timber, fload, bui;

	/** yesterday's index */
	double buo;

	/** The precipitation factor. */
	double precip;

	/** A comprises the piecewise regression coeficient indicators */
	double[] A = { 30.0, 19.2, 13.8, 22.5 };

	/** B comprises the piecewise regression coeficient indicators */
	double[] B = { -.1859, -.0859, -0.579, -.0774 };

	/** C comprises the range of dry-wet indicators */
	double[] C = { 4.5, 12.5, 27.5 }; // drywet indicators for ffm

	/** D comprises the dryfactor indicator */
	double[] D = { 16.0, 10.0, 7.0, 5.0, 4.0, 3.0 }; // dryfactor indicators for
														// dryFactor method.

	/**
	 * Instantiates a new subroutine danger.
	 *
	 * @param dry the dry bulb temperature
	 * @param wet the wet bulb temperature
	 * @param isnow some positive non zero number if there is snow on the ground
	 * @param precip the precipitation factor
	 * @param wind the current windspeed in miles per hour
	 * @param buo the last value of the build up index
	 * @param iherb the current herb state of the district 1=cured, 2=transition, 3=green
	 */
	public SubroutineDanger(double dry, double wet, boolean isnow, double precip, double wind, double buo, int iherb) {
		// initialize all indices to 0
		this.df = this.grass = this.timber = this.fload = this.bui = 0.0;
		this.ffm = this.adfm = 0.99;
		this.buo = buo;

		// isnow is positive if there is snow
		if (isnow) {
			grass = 0;
			timber = 0;
			setBuildUpIndex(precip);
		} else {
			setFineFuelMoisture(dry, wet);
			calculateDryingFactor();
			adjustFFMForHerb(iherb);
			setBuildUpIndex(precip);
			adjustBUOByDryingFactor();
			setADFM();
			setGrassTimber(wind);
			if (timber > 0 && bui > 0) {
				setFload();
			}
		}

	}

	/**
	 * Sets the builds up index where buo is yesterday's buildup The bui is
	 * adjusted for precipitation before adding the drying factor
	 * 
	 * @param precip is the past 24 hours precipitation in inches and hundredths
	 */
	// method that calculates build up index when precipitation exceeded or
	// equaled to 0.1 inches
	public void setBuildUpIndex(double precip) {
		if (precip > 0.1) {
			bui = -50 * ((Math.log((1 - (-1.0 * Math.exp(buo / 50))))) * Math.exp(1.175) * precip);
			if (bui < 0) {
				bui = 0;
			}
		}
	}

	/**
	 * Sets the fine fuel moisture index. The depression of the wet bulb is used
	 * to decide which set of A and B indicators will be used
	 * 
	 * @param dry is the dry bulb temperature
	 * @param wet is the wet bulb temperature
	 */
	// method that calculates fine fuel moisture according to dry and wet bulb
	// values
	public void setFineFuelMoisture(double dry, double wet) {
		double dif = dry - wet;
		double a = 0;
		double b = 0;
		if (dif < 4.5) {
			a = A[0];
			b = B[0];
		} else if (dif < 12.5) {
			a = A[1];
			b = B[1];
		} else if (dif < 27.5) {
			a = A[2];
			b = B[2];
		} else {
			a = A[3];
			b = B[3];
		}

		ffm = a * Math.exp(b) * dif;
	}

	/**
	 * Calculates the drying factor. The drying factor indicator is added to the
	 * fine fuel moisture index.
	 */
	// method that calculates the drying factor
	public void calculateDryingFactor() {
		for (int i = 1; i <= 6; i++) {
			if (ffm - D[i - 1] > 0) {
				df = i - 1;
				return;
			}
		}
		df = 7;
	}

	/**
	 * Adjusts fine fuel moisture for herb state.
	 *
	 * @param iherb the herb state is used to adjust the calculated ffm by adding 5 percent for transition stage.
	 */
	public void adjustFFMForHerb(double iherb) {
		if (ffm <= 1) {
			ffm = 1;
		} else {
			ffm = ffm + (iherb - 1) * 0.05;
		}
	}

	/**
	 * Adjust bui by adding the drying factor. After correction for rain, the
	 * drying factor is added to obtain the current buildup index
	 */
	public void adjustBUOByDryingFactor() {
		bui = buo + df;
	}

	/**
	 * Sets the adjusted fuel moisture index. ffm is the current fine fuel
	 * moisture, bui is today's buildup index recovery and e is the base of
	 * natural logs.
	 */
	public void setADFM() {
		adfm = 0.9 * ffm + 0.5 + 9.5 * Math.exp(-1.0 * bui / 50);
	}

	/**
	 * Sets the grass and timber indices. If both fuel moistures are greater
	 * than 30 percent, the timber and grass spread indices are set to 1. 
	 * Two formulas are necessary to calculate grass and timber indices according to windspeed.
	 * 
	 * @param wind Test to see if windspeed is greater than 14 mph. 
	 * @param  
	 * 
	 */
	public void setGrassTimber(double wind) {
		if (adfm >= 0.30 && ffm >= 0.30) {
			grass = 1;
			timber = 1;
		} else {
			if (wind >= 14 && grass <= 0 && timber <= 0) {
				grass = 0.00918 * (wind + 14) * (.33 - adfm) * 1.65 - 3;
				timber = 0.00918 * (wind + 14) * (.33 - adfm) * 1.65 - 3;
				if (grass > .99 && timber > .99) {
					grass = .99;
					timber = .99;
				}
			} else {
				grass = 0.01312 * (wind + 6) * (.33 - adfm) * 1.65 - 3;
				timber = 0.01312 * (wind + 6) * (.33 - adfm) * 1.65 - 3;

				if (timber <= 0) {
					timber = 1;
				}
				if (grass < 0) {
					grass = 1;
				}
				if (grass == 0) {
					grass = 0;
				}

			}
		}
	}

	/**
	 * Sets the fire load index. It is necessary that neither timber nor buildup spread indices be zero
	 * If either timber or buildup spread indices are zero, fire load index is zero.
	 */
	public void setFload() {
		if (timber > 0 && bui > 0) {
			fload = 1.75 * Math.log10(timber) + 0.32 * Math.log10(bui) - 1.640;
			if (fload < 0) {
				fload = 0;
			} else {
				fload = 10 * fload;
			}
		} else if (timber == 0 && bui == 0) {
			fload = 0;
		}
	}
}
