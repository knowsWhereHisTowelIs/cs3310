/**
 * CS3310 - A4
 * Author: Caleb Slater
 */
package files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author mini
 */
public class InputStream {
    private UI ui;
    private RandomAccessFile randAccessFile;
    private final String inputFilePath = "src/data/InputStream.csv";
    public static final String FILE_MODE = "rw";
    private boolean errored = false;

    public InputStream() {
        ui = new UI(false);
        ui.writeLine("open Input Stream File");
        try {
            File file = new File(inputFilePath);
            randAccessFile = new RandomAccessFile(file, FILE_MODE);
        } catch (FileNotFoundException ex) {
            System.out.println("Input Stream Error: " + ex.getMessage());
            errored = true;
        }
    }
    
    /**
     * Read InputStream.csv line by line
     * @return 
     */
    public String readLine() {
        String line;
        try {
            line = randAccessFile.readLine();
        } catch (IOException ex) {
            line = null;
        }
        return line;
    }
    
    /**
     * Say where to start to read
     * @param offset
     */
    public void setOffset( long offset ) {
        try {
            randAccessFile.seek( offset );
        } catch (IOException ex) {
            ui.writeLine("Error seeking");
        }
    }
    
    /**
     * current lseek value
     * @return 
     */
    public long getCurrentOffset() {
        long offset = -1;
        try {
            offset = randAccessFile.getFilePointer();
        } catch ( IOException ex ) {
            //error
        }
        return offset;
    }
    
    public void deconstruct() {
        try {
            ui.writeLine("close Input Stream File");
            randAccessFile.close();
        } catch( IOException ex ) {
            System.out.println("Raw data deconstruct error");
        }
    }

    public boolean isErrored() {
        return errored;
    }

    public void setErrored(boolean errored) {
        this.errored = errored;
    }
}
