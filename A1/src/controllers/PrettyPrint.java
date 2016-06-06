/**
 * CS3310 - A1
 * Author: Caleb Slater
 */
package controllers;

import files.CountryData;
import files.UI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Quick and dirty way of debuging CountryData.csv
 */
public class PrettyPrint {
    private final String filePath = "src/data/CountryData.csv";
    
    private RandomAccessFile randAccessFile;
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
        prettyPrint.openFiles();
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
     * Open files, alert if error
     */
    public void openFiles() {
        try {
            File file = new File(filePath);
            randAccessFile = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException ex) {
            System.out.println("RawData Error: " + ex.getMessage());
            errored = true;
        }
    }
    
    /**
     * Read line by line and output debug info
     */
    public void printLines() {
        String line;
        int i = 1;
        
        ui.writeLine("RRN> ID CODE NAME              CONTINENT      SIZE    YEAR  POPULATION L.EX");
        while( (line = readLine()) != null ) {
            StringBuilder lineData = new StringBuilder();
            lineData.append( String.format("%03d> ", i));
            if ( line.charAt(0) == '\0' ) {
                lineData.append("EMPTY");
            } else {
                lineData.append( countryData.formatCountryStr(line) );
            }
            ui.writeLine(lineData.toString());
            i++;
        }
    }
    
    /**
     * Get "Line" from file
     * @return 
     */
    public String readLine() {
        String line;
        byte byteArr[] = new byte[countryData.getSIZE_OF_DATA_REC()];
        
        int length;
        try {
            length = randAccessFile.read(byteArr);
            if( length == countryData.getSIZE_OF_DATA_REC() ) {
                line = (new String(byteArr)).substring(0, length - countryData.getCR_AND_LF());
            } else {
                line = null;
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
        try {
            randAccessFile.close();
        } catch (IOException ex) {
            System.out.println("Pretty Print close error");
        }
        countryData.deconstruct();
        ui.deconstruct();
    }
}