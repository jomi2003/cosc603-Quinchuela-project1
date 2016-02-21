// TODO: Auto-generated Javadoc
/**
 * The Class Driver.
 */

/**
 * @author JQ 
 * @version 1.0.0
 */

public class Driver{
	/**
	 * The main method calls the constructor and initialize values for input variables.
	 *
	 * @param args 	dry,  	dry bulb temperature
	 * 				wet, 	wet bulb temperature
	 * 				isnow, 	some positive non zero number if there is snow on the ground									 
	 * 				wind,  	the current wind speed in miles per hour
	 * 				buo,	the last value of the build up index
	 * 				iherb,	the current herb state of the district 1=cured, 2=transition, 3=green
	 */
	public static void main(String args[]){
		SubroutineDanger test = new SubroutineDanger(12, 8, true, 0, 15, 1, 1);
		System.out.println("The Drying Factor is: " + test.df);
		System.out.println("The Fine Fuel Moisture is: " + test.ffm);
		System.out.println("The Adjusted (10 day lag) Fuel Moisture is: " + test.adfm);
		System.out.println("The Grass Spread Index is: " + test.grass);
		System.out.println("The Timber Spread Index is: " + test.timber);
		System.out.println("The Fire Load Rating (man-hour base) is: " + test.fload);
		System.out.println("The Build Up Index is: " + test.bui);	
		
	}

}
