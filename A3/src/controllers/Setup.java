/**
 * CS3310 - A3
 * Author: Caleb Slater
 */
package controllers;

import files.*;
import java.io.IOException;
public class Setup {
    /**
     * Doesn't manipulate country data
     * only creates index based on input
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        UI ui = new UI(false); //must be first so truncates
        CountryData countryData = new CountryData();
        NameIndex nameIndex = new NameIndex(true);
        RawData rawData = new RawData();
        
        int count = 0;
        countryData.setOffset( (long) 0 );
        nameIndex.clearFile();
        
        if( rawData.isErrored() == false ) {
            countryData.clearFile();
            String line;
            while ( (line = rawData.readLine()) != null ) {
                int response = countryData.insertCountry(line);
                String[] parts = line.split(",");
                if ( response == 1 && parts.length >= 3) {
                    int id = Integer.parseInt( parts[0] );
                    String name = parts[2];
                    nameIndex.addNode(name, id);
                    count++;
                }
            }
        }
        
        //since we can't use null caracters must format file
        try {
            countryData.cleanFile();
        } catch (IOException ex) {
            ui.writeLine("error cleaning country data");
        }
        
        ui.writeLine("Setup done – " + count + " countries stored in CountryData file");
        countryData.deconstruct();
        nameIndex.deconstruct();
        ui.deconstruct();
    }
}
