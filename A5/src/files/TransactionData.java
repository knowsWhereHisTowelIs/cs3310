/**
 * CS3310 - A5
 * Author: Caleb Slater
 */
package files;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Manages TransDataA5_###.csv
 */
public class TransactionData {
    private String inputFilePath;
    
    private FileReader inputFileReader;
    private BufferedReader inputBufferedReader;
    private UI ui;
    private boolean errored = false;
    
    /**
     * constructor
     * @param dataSet
     */
    public TransactionData(int dataSet) {
        inputFilePath = "src/data/TransDataA5_"+dataSet+".csv";
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
