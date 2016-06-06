/**
 * CS3310 - A2
 * Author: Caleb Slater
 */
package files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 */
public class RawData {
    private final String inputFilePath = "src/data/RawDataA2.csv";
    private final String outputFilePath = "src/data/CountryData.bin";
    
    private FileReader inputFileReader;
    private BufferedReader inputBufferedReader;
    
    private FileWriter outputFileWriter;
    private BufferedWriter outputBufferedWriter;
    
    private UI ui;
    
    private boolean errored = false;
    
    public RawData() {
        ui = new UI(false);
        ui.writeLine("open RawData File");
        try {
            inputFileReader = new FileReader(inputFilePath);
            inputBufferedReader = new BufferedReader(inputFileReader);
        } catch (FileNotFoundException ex) {
            System.out.println("RawData Error: " + ex.getMessage());
            errored = true;
        }
        ui.writeLine("open CountryData File");
        try { 
            outputFileWriter = new FileWriter(outputFilePath);
            outputBufferedWriter = new BufferedWriter(outputFileWriter);
        } catch (IOException ex ) {
            System.out.println("RawData error: " + ex.getMessage());
        }
    }
    
    /**
     * read next line
     * @return 
     */
    public String readLine() {
        String line;
        try {
            line = inputBufferedReader.readLine();
        } catch (IOException ex) {
            line = null;
        }
        return line;
    }	

    /**
     * close files
     */
    public void deconstruct() {
        try {
            ui.writeLine("close RawData File");
            inputBufferedReader.close();
            inputFileReader.close();
            
            ui.writeLine("close CountryData File");
            outputBufferedWriter.close();
            outputFileWriter.close();
        } catch( IOException ex ) {
            System.out.println("Raw data deconstruct error");
        }
    }
    
    //-----------------GETTERS AND SETTERS----------------
    
    public FileReader getInputFileReader() {
        return inputFileReader;
    }

    public void setInputFileReader(FileReader inputFileReader) {
        this.inputFileReader = inputFileReader;
    }

    public BufferedReader getInputBufferedReader() {
        return inputBufferedReader;
    }

    public void setInputBufferedReader(BufferedReader inputBufferedReader) {
        this.inputBufferedReader = inputBufferedReader;
    }

    public FileWriter getOutputFileWriter() {
        return outputFileWriter;
    }

    public void setOutputFileWriter(FileWriter outputFileWriter) {
        this.outputFileWriter = outputFileWriter;
    }

    public BufferedWriter getOutputBufferedWriter() {
        return outputBufferedWriter;
    }

    public void setOutputBufferedWriter(BufferedWriter outputBufferedWriter) {
        this.outputBufferedWriter = outputBufferedWriter;
    }

    public boolean isErrored() {
        return errored;
    }

    public void setHasErrored(boolean hasErrored) {
        this.errored = hasErrored;
    }
    
}
