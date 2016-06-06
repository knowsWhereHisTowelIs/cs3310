/**
 * CS3310 - A2
 * Author: Caleb Slater
 */
package controllers;

import files.CountryData;
import files.UI;
import funcs.ArrayFuncs;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 * Quick and dirty way of debuging CountryData.csv
 */
public class PrettyPrint {
    private UI ui;
    private CountryData countryData;
    
    private boolean errored = false;
    
    /**
     * Setup for input
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PrettyPrint prettyPrint = new PrettyPrint();
        
        //prettyPrint.ui.writeLine("--------------------PRETTY PRINT----------------------");
        
        prettyPrint.ui.writeLine("open CountryData file");
        if( prettyPrint.errored == false ) {
            prettyPrint.printLines();
        } else {
            System.out.println("Failed to open files");
        }
        //System.out.println("\nEOF");
        
        prettyPrint.ui.writeLine("close CountryData file");
        prettyPrint.ui.writeLine("PrettyPrint done");
        prettyPrint.deconstruct();
    }
    
    /**
     * Constructor
     */
    public PrettyPrint() {
        ui = new UI(true); //must be first so appends
        countryData = new CountryData();
    }
    
    /**
     * Read line by line and output debug info
     */
    public void printLines() {
        String line;
        int i = 1;
        
        ui.writeLine("HOME AREA ***************************************************************************");
        ui.writeLine("RRN> ID CODE  NAME             CONTINENT         SIZE     YEAR  POPULATION L.EX LINK");
        while( (line = readLine(i)) != null ) {
            if( (i-1) == countryData.getMAX_N_HOME_LOC() ) {
                ui.writeLine("COLLISION AREA **********************************************************************");
            }
            ui.writeLine( String.format("%03d> %s", i, line));
            i++;
        }
    }
    
    /**
     * Get "Line" from file
     * @param row
     * @return 
     */
    public String readLine(int row) {
        String line;
        try {
            int offset = row == 1 ? countryData.getSIZE_OF_HEADER_REC() : -1;
            byte[] bytes = countryData.readRecord(offset);
            if( bytes.length != countryData.getSIZE_OF_DATA_REC() ) {
                line = null;
            } else if( ArrayFuncs.isByteArrByte(bytes, countryData.getDATA_REC_EMPTY_VALUE()) ) {
                line = "EMPTY";
            } else if( ArrayFuncs.isByteArrByte(Arrays.copyOfRange(bytes, 0, countryData.getDATA_REC_TOMBSTONE_END()), countryData.getDATA_REC_TOMBSTONE_VALUE() ) ) {
                short link = ArrayFuncs.toShort(Arrays.copyOfRange(bytes, countryData.getDATA_REC_TOMBSTONE_END(), countryData.getDATA_REC_TOMBSTONE_END() + countryData.getSIZE_OF_FIELD_LINK()));
                line = "TOMBSTONE                                                                  "+link;
            } else {
                line = countryData.formatCountryStrWithLink(bytes);
            }
        } catch (IOException ex) {
            line = null;
        }
        
        return line;
    }

    /**
     * close files
     */
    private void deconstruct() {
        countryData.deconstruct();
        ui.deconstruct();
    }
}