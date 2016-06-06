/**
 * CS3310 - A3
 * Author: Caleb Slater
 */
package files;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Manages TransData.csv
 */
public class TransactionData {
    private final String inputFilePath = "src/data/TransData.csv";
    
    private FileReader inputFileReader;
    private BufferedReader inputBufferedReader;
    private UI ui;
    private boolean errored = false;
    
    /**
     * constructor
     */
    public TransactionData() {
        ui = new UI(false);
        ui.writeLine("open TransData File");
        try {
            inputFileReader = new FileReader(inputFilePath);
            inputBufferedReader = new BufferedReader(inputFileReader);
        } catch (FileNotFoundException ex ){
            errored = true;
        }
    }
    
    /**
     * get next action from current offset
     * @return 
     */
    public String getNextTransaction() {
        String transaction;
        
        try {
            transaction = inputBufferedReader.readLine();
        } catch (IOException ex) {
            transaction = null;
        }
        
        return transaction;
    }

    /**
     * close files
     */
    public void deconstruct() {
        ui.writeLine("close CountryData File");
        try {
            inputBufferedReader.close();
            inputFileReader.close();
        } catch (IOException ex) {
            System.out.println("Trans deconstruct error");
        }
    }
}
