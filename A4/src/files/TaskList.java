/**
 * CS3310 - A4
 * Author: Caleb Slater
 */
package files;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author mini
 */
public class TaskList {
    private final String inputFilePath = "src/data/TaskList.csv";
    
    private FileReader inputFileReader;
    private BufferedReader inputBufferedReader;
    
    private UI ui;
    
    private boolean errored = false;
    
    public TaskList() {
        ui = new UI(false);
        ui.writeLine("open TaskList File");
        try {
            inputFileReader = new FileReader(inputFilePath);
            inputBufferedReader = new BufferedReader(inputFileReader);
        } catch (FileNotFoundException ex) {
            System.out.println("RawData Error: " + ex.getMessage());
            errored = true;
        }
    }
    
    /**
     * Get line from offset
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
     * close files. destroy object
     */
    public void deconstruct() {
        try {
            ui.writeLine("close TaskList File");
            inputBufferedReader.close();
            inputFileReader.close();
        } catch( IOException ex ) {
            System.out.println("TaskList deconstruct error");
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

    public boolean isErrored() {
        return errored;
    }

    public void setHasErrored(boolean hasErrored) {
        this.errored = hasErrored;
    }
    
}
