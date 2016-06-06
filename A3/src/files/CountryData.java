/**
 * CS3310 - A3
 * Author: Caleb Slater
 */
package files;

import funcs.ArrayFuncs;
import funcs.Strings;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * Manages CountryData.csv
 */
public class CountryData {
    public static final int SIZE_OF_FIELD_CODE = 3;
    public static final int SIZE_OF_FIELD_NAME = 18;
    public static final int SIZE_OF_FIELD_CONTINENT = 13;
    public static final int SIZE_OF_FIELD_POPULATION = 10;
    public static final int SIZE_OF_FIELD_LIFE = 4;

    public static final int SIZE_OF_ALL_FIELDS_MAX = SIZE_OF_FIELD_CODE
            + SIZE_OF_FIELD_CONTINENT + SIZE_OF_FIELD_NAME
            + SIZE_OF_FIELD_POPULATION + SIZE_OF_FIELD_LIFE;

    public static final int CR_AND_LF = 2;
    public static final int SIZE_OF_ONE_CHAR = 1;

    public static final int SIZE_OF_DATA_REC = (SIZE_OF_ALL_FIELDS_MAX + CR_AND_LF) * SIZE_OF_ONE_CHAR;
    public static final int SIZE_OF_DATA_REC_WO_NEW_LN = SIZE_OF_ALL_FIELDS_MAX * SIZE_OF_ONE_CHAR;
    public static final int SIZE_OF_HEADER_REC = 0;

    public static final String filePath = "src/data/CountryData.csv";
    public static final String FILE_MODE = "rw";

    private RandomAccessFile randAccessFile;
    private UI ui;

    private boolean errored = false;

    /**
     * constructor
     */
    public CountryData() {
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
     * trucate data in file
     */
    public void clearFile() {
        try {
            randAccessFile.setLength(0);
        } catch (IOException ex) {
            ui.writeLine("Error clearing country data file");
        }
    }
    
    
    /**
     * Parse line then add to data
     * @param line
     * @return 
     */
    public int insertCountry(String line) {
        int response = -1;
        String[] fields = line.split(",");
        int minNumOfFields = 6;
        if (fields.length >= minNumOfFields) {
            int id = Integer.parseInt(fields[0]);
            String code = Strings.forceStrLength(SIZE_OF_FIELD_CODE, fields[1]);
            String name = Strings.forceStrLength(SIZE_OF_FIELD_NAME, fields[2]);
            String continent = Strings.forceStrLength(SIZE_OF_FIELD_CONTINENT, fields[3]);
            long population = Long.valueOf(fields[4]);
            float lifeExpectancy = fields[5].equals("NULL") ? (float) 0.00 : Float.valueOf(fields[5]);

            String formattedLine = String.format("%s%s%s%010d%2.1f\r\n", code, name, continent, population, lifeExpectancy);

            long offset = SIZE_OF_HEADER_REC + SIZE_OF_DATA_REC * (id - 1);
            try {
                randAccessFile.seek(offset);
                randAccessFile.writeBytes(formattedLine);
                response = 1;
            } catch (IOException ex) {
                System.out.println("Failed to insert " + name);
            }

            ui.writeLine("OK, " + name + " inserted");
        } else {
            System.out.println("Field count is wrong: " + line);
        }

        return response;
    }
    
    /**
     * return country based on rrn
     * @param RRN
     * @return 
     */
    public String getThisData(int RRN) {
        long offset = SIZE_OF_HEADER_REC + SIZE_OF_DATA_REC * (RRN - 1);
        setOffset(offset);
        return getCountryLineFromCurrentOffset();
    }

    /**
     * format raw csv line
     * @param line
     * @return 
     */
    public String formatCountryStr(String line) {
        String str;
        if (line.length() >= SIZE_OF_DATA_REC_WO_NEW_LN) {
            int startOffset = 0;
            int endOffset = SIZE_OF_FIELD_CODE;
            String code = line.substring(startOffset, endOffset);

            startOffset = endOffset;
            endOffset = startOffset + SIZE_OF_FIELD_NAME;
            String name = line.substring(startOffset, endOffset);

            startOffset = endOffset;
            endOffset = startOffset + SIZE_OF_FIELD_CONTINENT;
            String continent = line.substring(startOffset, endOffset);

            startOffset = endOffset;
            endOffset = startOffset + SIZE_OF_FIELD_POPULATION;
            long population = Long.valueOf( line.substring(startOffset, endOffset) );

            startOffset = endOffset;
            endOffset = startOffset + SIZE_OF_FIELD_LIFE;
            float life = Float.valueOf(line.substring(startOffset, endOffset));
            
            String popStr = String.format("%,10d", population);
            int popStrLen = 13; //10 #s + 3 commas
            while( popStr.length() < popStrLen ) {
                popStr = " " + popStr;
            }
            
            str = String.format("%s %s %s %s %2.1f", code, name, continent, popStr, life);
        } else {
            str = "";
        }
        return str;
    }
    
    /**
     * get country line by ID
     * @param id
     * @return
     * @throws IOException 
     */
    public String queryCountry(int id) throws IOException {
        String country = null;

        byte[] bytes = new byte[SIZE_OF_DATA_REC];
        long offset = SIZE_OF_HEADER_REC + SIZE_OF_DATA_REC * (id - 1);
        if ((offset + SIZE_OF_DATA_REC) > randAccessFile.length()) {
            System.out.println("Don't alter file length since id isn't in file");
        } else {
            randAccessFile.seek(offset);
            randAccessFile.read(bytes);
            //country = String.valueOf(bytes);
            if (bytes[0] != '\0') {
                //not deleted/empty
                country = (new String(bytes)).substring(0, SIZE_OF_DATA_REC - (SIZE_OF_ONE_CHAR * CR_AND_LF));
            }
        }

        return country;
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
                //line = String.valueOf(bytes);
                line = new String(bytes);
            }
        } catch (IOException ex) {
            //line = null;
            //ex.printStackTrace();
        }

        return line;
    }
    
    /**
     * Read countrydata.txt line by line
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
     * replace invalid rows with spaces \r\n
     */
    public void cleanFile() throws IOException {
        byte nullByte = 0;
        byte[] emptyLine = ArrayFuncs.toByteArr( Strings.forceStrLength(SIZE_OF_DATA_REC_WO_NEW_LN, "") + "\r\n" );
        
        String line;
        long offset = 0;
        randAccessFile.seek(offset);
        
        byte[] bytes = new byte[SIZE_OF_DATA_REC];
        boolean continueReading = true;
        while( continueReading ) {
            try {
                int readLength = randAccessFile.read(bytes);
                if( readLength == SIZE_OF_DATA_REC ) {
                    boolean isNull = false;
                    for( int i = 0; i < bytes.length; i++ ) {
                        if( bytes[i] == nullByte ) {
                            isNull = true;
                            break;
                        }
                    }
                    if( isNull ) {
                        randAccessFile.seek(offset);
                        randAccessFile.write( emptyLine );
                    }
                } else {
                    continueReading = false;
                }
                offset += SIZE_OF_DATA_REC;
            } catch ( IOException ex ) {
                continueReading = false;
            }

        }
        
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

    public UI getUi() {
        return ui;
    }

    public void setUi(UI ui) {
        this.ui = ui;
    }

    public boolean isErrored() {
        return errored;
    }

    public void setErrored(boolean errored) {
        this.errored = errored;
    }
    
}
