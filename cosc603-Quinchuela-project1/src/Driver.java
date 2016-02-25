// TODO: Auto-generated Javadoc

/**
 * @author JQ           <a href="mailto: jquinc1@students.towson.edu>"
 * @version 1.0.0
 */

/**
 * The Class Driver. Tests for functionality of the code.
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
		SubroutineDanger test1 = new SubroutineDanger(12, 10, false, 1, 15, 1, 2);
		System.out.println("The Drying Factor is: " + test1.df);
		System.out.println("The Fine Fuel Moisture is: " + test1.ffm);
		System.out.println("The Adjusted (10 day lag) Fuel Moisture is: " + test1.adfm);
		System.out.println("The Grass Spread Index is: " + test1.grass);
		System.out.println("The Timber Spread Index is: " + test1.timber);
		System.out.println("The Fire Load Rating (man-hour base) is: " + test1.fload);
		System.out.println("The Build Up Index is: " + test1.bui);	
		
		
		SubroutineDanger test2 = new SubroutineDanger(12, 10, true, 1, 15, 1, 2);
		System.out.println("The Drying Factor is: " + test2.df);
		System.out.println("The Fine Fuel Moisture is: " + test2.ffm);
		System.out.println("The Adjusted (10 day lag) Fuel Moisture is: " + test2.adfm);
		System.out.println("The Grass Spread Index is: " + test2.grass);
		System.out.println("The Timber Spread Index is: " + test2.timber);
		System.out.println("The Fire Load Rating (man-hour base) is: " + test2.fload);
		System.out.println("The Build Up Index is: " + test2.bui);	
		
		
	}

}
