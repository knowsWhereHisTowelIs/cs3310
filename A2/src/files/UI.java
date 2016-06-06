/**
 * CS3310 - A2
 * Author: Caleb Slater
 */
package files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Manage Log.txt
 */
public class UI {
    private static final String OUTPUT_FILE_PATH = "src/data/Log.txt";
    
    private static FileWriter outputFileWriter;
    private static BufferedWriter outputBufferedWriter;
    
    private static boolean setup;
    private static boolean errored;
    private static final boolean debug = true;
    
    /**
     * constructor
     * @param append Append or truncate file
     */
    public UI(boolean append) {
        try {
            //File f = new File(OUTPUT_FILE_PATH);
            //System.out.println("PATH " + f.getAbsolutePath());
            if ( setup == false ) {
                outputFileWriter = new FileWriter(OUTPUT_FILE_PATH, append);
                outputBufferedWriter = new BufferedWriter(outputFileWriter);
                
                //must be after
                writeLine("open Log File");
                setup = true;
                errored = false;
            }
        } catch (IOException ex) {
            System.out.println("UI Output Error: " + ex.getMessage());
            errored = true;
            setup = false;
        }
    }
    
    /**
     * write line adds new line at  end
     * @param str
     * @return 
     */
    public int writeLine(String str){ 
        int response = 1;
        
        try {
            outputBufferedWriter.write(str + "\r\n");
            if( debug ) {
                System.out.println(str);
            }
        } catch ( IOException ex ) {
            response = -1;
            System.out.println("UI writeLine error writing: ~~~" + str + "~~~\n");
        }
        
        return response;
    }

    /**
     * close files
     */
    public void deconstruct() {
        writeLine("close Log File");
        try {
            outputBufferedWriter.close();
        } catch (IOException ex) {
            System.out.println("UI File buffer close");
        }
            
        try {
            outputFileWriter.close();
        } catch (IOException ex) {
            System.out.println("UI File writer close");
        }
    }
    
}
