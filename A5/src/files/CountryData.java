/**
 * CS3310 - A5
 * Author: Caleb Slater
 */
package files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Manages CountryData_###.txt
 */
public class CountryData {

    private final int SIZE_OF_FIELD_ID = 2;
    private final int SIZE_OF_FIELD_CODE = 2;
    private final int SIZE_OF_FIELD_OTHER = 17;

    private final int SIZE_OF_ALL_FIELDS_MAX = SIZE_OF_FIELD_ID + SIZE_OF_FIELD_CODE + SIZE_OF_FIELD_OTHER;

    private final int N_OF_FIELDS = 3;
    private final int SIZE_OF_ALL_SEPERATORS = N_OF_FIELDS - 1;
    private final int CR_AND_LF = 2;
    private final int SIZE_OF_ONE_CHAR = 1;

    private final int SIZE_OF_DATA_REC = (SIZE_OF_ALL_FIELDS_MAX + SIZE_OF_ALL_SEPERATORS + CR_AND_LF) * SIZE_OF_ONE_CHAR;
    private final int SIZE_OF_DATA_REC_WO_NEW_LN = (SIZE_OF_ALL_FIELDS_MAX + SIZE_OF_ALL_SEPERATORS) * SIZE_OF_ONE_CHAR;
    private final int SIZE_OF_HEADER_REC = 0;

    private final String filePath;

    private RandomAccessFile randAccessFile;
    private UI ui;

    private final String FILE_MODE = "rw";

    private boolean errored = false;

    /**
     * constructor
     * @param dataSet
     */
    public CountryData(int dataSet) {
        filePath = "src/data/CountryData_"+dataSet+".txt";
        File file = new File(filePath);
        ui = new UI(false);
        ui.writeLine("open CountryData File");
        try {
            randAccessFile = new RandomAccessFile(file, FILE_MODE);
        } catch (FileNotFoundException ex) {
            errored = true;
            System.out.println("Couldn't create " + file);
        }
    }

    /**
     * truncate data in file
     */
    public void clearFile() {
        try {
            randAccessFile.setLength( (long) 0 );
        } catch (IOException ex) {
            ui.writeLine("Error clearing country data file");
        }
    }
    
    /**
     * Read "line" from current offset
     * @return 
     */
    public String getCountryLineFromCurrentOffset() {
        String line = "";

        byte[] bytes = new byte[SIZE_OF_DATA_REC];
        try {
            int readLength = randAccessFile.read(bytes);
            if (readLength == SIZE_OF_DATA_REC) {
                line = new String(bytes);
            }
        } catch (IOException ex) {
            //error leave line as ""
        }

        return line;
    }

    /**
     * get country line by ID
     * @param id
     * @return
     * @throws IOException 
     */
    public String queryCountry(int id) throws IOException {
        String country = "";

        byte[] bytes = new byte[SIZE_OF_DATA_REC];
        long offset = SIZE_OF_HEADER_REC + SIZE_OF_DATA_REC * (id - 1);
        if ((offset + SIZE_OF_DATA_REC) > randAccessFile.length()) {
            //index out of bounds but don't write in log
            System.out.println("Don't alter file length since id isn't in file");
        } else {
            randAccessFile.seek(offset);
            randAccessFile.read(bytes);
            if (bytes[0] != '\0') {
                //not deleted/empty
                country = (new String(bytes)).substring(0, SIZE_OF_DATA_REC - (SIZE_OF_ONE_CHAR * CR_AND_LF));
            }
        }

        return country;
    }

    /**
     * close files
     */
    public void deconstruct() {
        ui.writeLine("close CountryData File");
        try {
            randAccessFile.close();
        } catch (IOException ex) {
            System.out.println("Country data deconstruct error");
        }
    }

    //-------------GETTERS AND SETTERS--------------
    public RandomAccessFile getRandAccessFile() {
        return randAccessFile;
    }

    public void setRandAccessFile(RandomAccessFile randAccessFile) {
        this.randAccessFile = randAccessFile;
    }

    public boolean isErrored() {
        return errored;
    }

    public void setErrored(boolean errored) {
        this.errored = errored;
    }

    public int getSIZE_OF_ALL_FIELDS_MAX() {
        return SIZE_OF_ALL_FIELDS_MAX;
    }

    public int getN_OF_FIELDS() {
        return N_OF_FIELDS;
    }

    public int getSIZE_OF_ALL_COMMAS_TOGETHER() {
        return SIZE_OF_ALL_SEPERATORS;
    }

    public int getCR_AND_LF() {
        return CR_AND_LF;
    }

    public int getSIZE_OF_ONE_CHAR() {
        return SIZE_OF_ONE_CHAR;
    }

    public int getSIZE_OF_DATA_REC() {
        return SIZE_OF_DATA_REC;
    }

    public int getSIZE_OF_HEADER_REC() {
        return SIZE_OF_HEADER_REC;
    }

    public String getFilePath() {
        return filePath;
    }

}
