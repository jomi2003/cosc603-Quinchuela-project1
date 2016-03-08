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
 * Drying Factor as 						df
 * Fine Fuel Moisture as 					ffm
 * Adjusted (10 day lag) Fuel Moisture as 	adfm
 * Grass Spread Index as 					grass
 * Timber Spread Index as 					timber
 * Fire Load Rating (man hour base) as 		fload
 * Build Up Index as 						bui
 * 
 */

/**
 * @author Jose Quinchuela    <a href="mailto: jquinc1@students.towson.edu>"
 * @version 1.0.0 
 */

// TODO: Auto-generated Javadoc
/**
 * The Class SubroutineDanger. It calculates df, ffm, adfm, grass, timber, fload
 * and bui indices.
 */
public class SubroutineDanger {

	private SubroutineDangerProductD subroutineDangerProductD = new SubroutineDangerProductD();

	private SubroutineDangerProductB subroutineDangerProductB = new SubroutineDangerProductB();

	private SubroutineDangerProductA subroutineDangerProductA = new SubroutineDangerProductA();

	/**
	 * Drying Factor as df 
	 * Fine Fuel Moisture as ffm 
	 * Adjusted (10 day lag) Fuel Moisture as adfm 
	 * Grass Spread Index as grass 
	 * Timber Spread Index as timber 
	 * Fire Load Rating (man hour base) as fload 
	 * Build Up Index as bui
	 */
	double df, ffm, adfm, grass, timber, fload, bui;

	/** yesterday's index */
	double buo;

	/** The precipitation factor. */
	double precip;

	/** C comprises the range of dry-wet indicators */
	double[] C = { 4.5, 12.5, 27.5 }; 

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
		this.ffm = this.adfm = 99;
		this.buo = buo;

		// isnow is positive or true if there is snow.
		if (isnow == true) {
			grass = 0;
			timber = 0;
			setBuildUpIndex(precip);
		} else {
			setFineFuelMoisture(dry, wet);
			subroutineDangerProductD.calculateDryingFactor(this, ffm);
			adjustFFMForHerb(iherb);
			setBuildUpIndex(precip);
			adjustBUIByDryingFactor();
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
			if (bui >= 0){
			bui = -50 * Math.log(1 - (1 - Math.exp((-1.0)*buo/50)) * Math.exp(((-1.0)*1.175) * (precip - 0.1)));
			}
			else if (bui < 0) {
				bui = 0;
			}	
		}
		else {
			grass =  0;
			timber = 0;
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
		double a = subroutineDangerProductA.a(dry, wet);
		double b = subroutineDangerProductB.b(dry, wet);
		double dif = dry - wet;
		//a and b values are switched due to a glitch in the original code.
		ffm = a * Math.exp(b) * dif;
	}

	/**
	 * Calculates the drying factor. The drying factor indicator is added to the
	 * fine fuel moisture index.
	 */
	// method that calculates the drying factor
	public void calculateDryingFactor() {
		subroutineDangerProductD.calculateDryingFactor(this, ffm);
	}

	/**
	 * Adjusts fine fuel moisture for herb state.
	 *
	 * @param iherb the herb state is used to adjust the calculated ffm by adding 5 percent for transition stage.
	 */
	public void adjustFFMForHerb(double iherb) {
		if ((ffm - 1) < 0) {
			ffm = 1;
		} 
		else 
			{
			ffm = ffm + (iherb - 1) * 5;
		}
	}

	/**
	 * Adjust bui by adding the drying factor. After correction for rain, the
	 * drying factor is added to obtain the current buildup index
	 */
	public void adjustBUIByDryingFactor() {
		bui = buo + df;
	}

	/**
	 * Sets the adjusted fuel moisture index. ffm is the current fine fuel
	 * moisture, bui is today's buildup index recovery and e is the base of
	 * natural logs.
	 */
	public void setADFM() {
		adfm = 0.9 * ffm + 0.5 + 9.5 * Math.exp((-1.0 * bui)/ 50);
	}

	/**
	 * Sets the grass and timber indices. If both fuel moistures are greater
	 * than 30 percent, the timber and grass spread indices are set to 1. 
	 * Two formulas are necessary to calculate grass and timber indices according to windspeed.
	 * 
	 * @param wind Test to see if windspeed is greater than 14 mph.  
	 * 
	 */
	public void setGrassTimber(double wind) {
		if (adfm >= 30 && ffm >= 30) {
			grass = 1;
			timber = 1;
		} else {
			if (wind >= 14){
				if(grass <= 0 && timber <= 0){
				grass = 0.00918 * (wind + 14) * Math.pow((33 - adfm), (1.65)) - 3;
				timber = 0.00918 * (wind + 14) * Math.pow((33 - ffm), (1.65)) - 3;
				}
				if (grass > 99 && timber > 99) {
					grass = 99;
					timber = 99;
				}
			} else {
				grass = 0.01312 * (wind + 6) * Math.pow((33 - adfm), (1.65)) - 3;
				timber = 0.01312 * (wind + 6) * Math.pow((33 - ffm), (1.65)) - 3;
				//this could also work with timber < 1 
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
			fload = 1.75 * Math.log10(timber) + 0.32 * Math.log10(bui) - 1.640;
			if (fload <= 0) {
				fload = 0;
			} else {
				fload = Math.pow(10, fload);
			}		
		}

	public void setDf(double df) {
		this.df = df;
	}
	}
