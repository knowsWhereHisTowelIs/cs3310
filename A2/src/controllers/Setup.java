/**
 * CS3310 - A2
 * Author: Caleb Slater
 */
package controllers;

import files.*;
public class Setup {
    /**
     * Take rawData file and output to country data
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        UI ui = new UI(false); //must be first so truncates
        RawData rawData = new RawData();
        CountryData countryData = new CountryData();
        
        //ui.writeLine("--------------------SETUP----------------------");
        
        int count = 0;
        if( rawData.isErrored() == false ) {
            countryData.clearFile();
            String line;
            while ( (line = rawData.readLine()) != null ) {
                int response = countryData.insertCountry(line);
                if ( response == 1 ) {
                    count++;
                }
            }
        }
        
        ui.writeLine("Setup done – " + count + " countries stored in CountryData file");
        rawData.deconstruct();
        countryData.deconstruct();
        ui.deconstruct();
    }
}
