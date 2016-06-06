/**
 * CS3310 - A2
 * Author: Caleb Slater
 */
package files;

import funcs.ArrayFuncs;
import funcs.Strings;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages CountryData.bin
 */
public class CountryData {

    private final int SIZE_OF_CHAR = 1;
    private final int SIZE_OF_SHORT = 2;
    private final int SIZE_OF_INT = 4;
    private final int SIZE_OF_LONG = 8;
    //private final int SIZE_OF_BOOLEAN = 1;
    private final int SIZE_OF_FLOAT = 4;

    private final int SIZE_OF_FIELD_ID = SIZE_OF_SHORT;
    private final int SIZE_OF_FIELD_CODE = SIZE_OF_CHAR * 3;
    private final int SIZE_OF_FIELD_CONTINENT = SIZE_OF_CHAR * 13;
    private final int SIZE_OF_FIELD_NAME = SIZE_OF_CHAR * 17;
    private final int SIZE_OF_FIELD_SIZE = SIZE_OF_INT; //size == surface area
    private final int SIZE_OF_FIELD_YEAR = SIZE_OF_SHORT;
    private final int SIZE_OF_FIELD_POPULATION = SIZE_OF_LONG;
    private final int SIZE_OF_FIELD_LIFE = SIZE_OF_FLOAT;
    private final int SIZE_OF_FIELD_LINK = SIZE_OF_SHORT;

    private final int N_OF_FIELDS = 8;
    private final int SIZE_OF_ALL_COMMAS_TOGETHER = N_OF_FIELDS - 1;
    private final int CR_AND_LF = 2;
    private final int SIZE_OF_ONE_CHAR = 1;

    private final int SIZE_OF_DATA_REC = SIZE_OF_FIELD_ID + SIZE_OF_FIELD_CODE + SIZE_OF_FIELD_CONTINENT + SIZE_OF_FIELD_NAME
            + SIZE_OF_FIELD_SIZE + SIZE_OF_FIELD_YEAR + SIZE_OF_FIELD_POPULATION + SIZE_OF_FIELD_LIFE + SIZE_OF_FIELD_LINK;

    private final String filePath = "src/data/CountryData.bin";

    private RandomAccessFile randAccessFile;
    private UI ui;

    private final String FILE_MODE = "rw";

    private boolean errored = false;
    
    private short nHomeRec, nCollRec, nextEmpty, MAX_N_HOME_LOC;
    private final int SIZE_OF_HEADER_REC = SIZE_OF_SHORT * 4;
    private final int SIZE_OF_FORMATTED_COUNTRY_STR = 74;
    private final byte DATA_REC_EMPTY_VALUE = 0;
    private final byte DATA_REC_TOMBSTONE_VALUE = -1;
    private final int DATA_REC_TOMBSTONE_END = SIZE_OF_DATA_REC - SIZE_OF_FIELD_LINK;
    /**
     * constructor
     */
    public CountryData() {
        File file = new File(filePath);
        ui = new UI(false);
        ui.writeLine("open CountryData File");
        openFile();
    }

    /**
     * truncate data in file
     * prepare header record
     */
    public void clearFile() {
        try {
            randAccessFile.setLength(0);
//            randAccessFile.close();
            nHomeRec = 0;
            nCollRec = 0;
            MAX_N_HOME_LOC = 20;
            nextEmpty = (short) (MAX_N_HOME_LOC + nCollRec + 1);
            System.out.println("Cleared country data file");
//            openFile();
        } catch (IOException ex) {
            ui.writeLine("Error clearing country data file");
        }
    }
    
    /**
     * Open file load header record
     */
    private void openFile() {
        try {
            File file = new File(filePath);
            randAccessFile = new RandomAccessFile(file, FILE_MODE);
            
            byte[] bytes = new byte[SIZE_OF_HEADER_REC];
            
            if( randAccessFile.read(bytes) == SIZE_OF_HEADER_REC ) {
                int bytesOffset = 0;
                nHomeRec = ArrayFuncs.toShort( Arrays.copyOfRange(bytes, bytesOffset, bytesOffset+SIZE_OF_SHORT) );
                bytesOffset += SIZE_OF_SHORT;
                nCollRec = ArrayFuncs.toShort( Arrays.copyOfRange(bytes, bytesOffset, bytesOffset+SIZE_OF_SHORT) );
                bytesOffset += SIZE_OF_SHORT;
                nextEmpty = ArrayFuncs.toShort( Arrays.copyOfRange(bytes, bytesOffset, bytesOffset+SIZE_OF_SHORT) );
                bytesOffset += SIZE_OF_SHORT;
                MAX_N_HOME_LOC = ArrayFuncs.toShort( Arrays.copyOfRange(bytes, bytesOffset, bytesOffset+SIZE_OF_SHORT) );
                bytesOffset += SIZE_OF_SHORT;
                
                ui.writeLine(String.format("HEADER REC:    %d Rec in Home Area,   %d Rec in Collision Area, MAX_N_HOME_LOC: %d\n", nHomeRec, nCollRec, nextEmpty, MAX_N_HOME_LOC));
            } else {
                System.out.println("Failed to read header");
            }
        } catch (IOException ex) {
            ui.writeLine("Error opening country data file");
        }
    }
    
    /**
     * Parse line then add to data
     *
     * @param line
     * @return
     */
    public int insertCountry(String line) {
        int response = -1;
        String[] fields = line.split(",");
        if (fields.length >= N_OF_FIELDS) {
            //parse fields
            short id = Short.parseShort(fields[0]);
            String code = Strings.forceStrLength(3, fields[1]);
            String name = Strings.forceStrLength(17, fields[2]);
            String continent = Strings.forceStrLength(13, fields[3]);
            //String region           = Strings.forceStrLength(20, fields[4]);
            int surfaceArea = Integer.parseInt(fields[5]);
            short yearOfIndep = fields[6].equals("NULL") ? 0 : Short.parseShort(fields[6]);
            long population = Long.parseLong(fields[7]);
            float lifeExpectancy = fields[8].equals("NULL") ? 0 : Float.parseFloat(fields[8]);
            
            boolean errorInserting = hashCountry(id, code, name, continent, surfaceArea, yearOfIndep, population, lifeExpectancy);
            if( errorInserting ) {
                System.out.println("Failed to insert " + name);
            } else {
                response = 1;
                ui.writeLine("OK, " + name + " inserted");
            }
        } else {
            System.out.println("Field count is wrong: " + line);
        }

        return response;
    }

    /**
     * get country line by code
     *
     * @param qCode
     * @return
     * @throws IOException
     */
    public byte[] queryCountryByCode(String qCode) throws IOException {
        byte[] bytes = getRecordInfoByCode(qCode);
        int bytesOffset = SIZE_OF_LONG;
        int count = ArrayFuncs.toInt(Arrays.copyOfRange(bytes, bytesOffset, bytesOffset + SIZE_OF_INT));
        bytesOffset += SIZE_OF_INT;
        String country = ArrayFuncs.toString(Arrays.copyOfRange(bytes, bytesOffset, bytes.length));
        
        if ( country == null ) {
            bytes = new byte[SIZE_OF_INT];
            bytes = ArrayFuncs.concatenateBytes( bytes, 0, ArrayFuncs.toByteArr(count) );
        } else {
            bytes = new byte[SIZE_OF_INT + country.length()];
            bytes = ArrayFuncs.concatenateBytes( bytes, 0, ArrayFuncs.toByteArr(count) );
            bytes = ArrayFuncs.concatenateBytes( bytes, SIZE_OF_INT, ArrayFuncs.toByteArr(country) );
        }
        return bytes;
    }
    
    /**
     * Delete country from data
     * Really only set all but link to 0's
     * @param qCode
     * @return boolean
     */
    public boolean deleteCountryByCode(String qCode) {
        byte[] bytes = getRecordInfoByCode(qCode);
        int bytesOffset = 0;
        long offset = ArrayFuncs.toLong(Arrays.copyOfRange(bytes, bytesOffset, bytesOffset + SIZE_OF_LONG));
        
        boolean successfullyDeleted = true;
        if( offset == -1 ) {
            //nothing to delete
            successfullyDeleted = false;
        } else {
            try {
                byte[] binary = readRecord(offset);
                int startFieldLink = SIZE_OF_DATA_REC - SIZE_OF_FIELD_LINK;
                for( int i = 0; i < startFieldLink; i++) {
                    binary[i] = DATA_REC_TOMBSTONE_VALUE;
                }
                randAccessFile.seek(offset);
                randAccessFile.write(binary);
            } catch (IOException ex ) {
                System.out.println("Error deleting file");
            }
        }
        return successfullyDeleted;
    }
    
    public byte[] getRecordInfoByCode(String qCode) {
        String country = "";
        int homeRRN = hashFunction(qCode);
        int count = 0;
        boolean continueWithChain = true;
        long offset = SIZE_OF_HEADER_REC + SIZE_OF_DATA_REC * (homeRRN-1);
        while ( continueWithChain ) {
            byte[] bytes;
            try {
                bytes = readRecord(offset);
            } catch( IOException ex ) {
                break; //error
            }

            String code = ArrayFuncs.toString(Arrays.copyOfRange(bytes, SIZE_OF_FIELD_ID, SIZE_OF_FIELD_ID + SIZE_OF_FIELD_CODE));
            short link = ArrayFuncs.toShort(Arrays.copyOfRange(bytes, SIZE_OF_DATA_REC - SIZE_OF_FIELD_LINK, SIZE_OF_DATA_REC));
            if( code.equals(qCode) ) {
                //found code exit and get nice string
                continueWithChain = false;
                country = formatCountryStr(bytes);
            } else if ( link == -1 ) {
                //end of chain exit
                country = new String(new char[SIZE_OF_FORMATTED_COUNTRY_STR]); //string populated w/0 bytes
                continueWithChain = false;
            } else {
                //update offset to next element
                offset = SIZE_OF_HEADER_REC + SIZE_OF_DATA_REC * (link-1);
            }
            //System.out.printf("Code:%s at offset:%d link:%d\n", code, offset, link);
            count++;
        }
        
        byte[] bytes = new byte[SIZE_OF_LONG + SIZE_OF_INT + country.length()];
        int bytesOffset = 0;
        
        bytes = ArrayFuncs.concatenateBytes(bytes, bytesOffset, ArrayFuncs.toByteArr(offset));
        bytesOffset += SIZE_OF_LONG;
        bytes = ArrayFuncs.concatenateBytes(bytes, bytesOffset, ArrayFuncs.toByteArr(count));
        bytesOffset += SIZE_OF_INT;
        bytes = ArrayFuncs.concatenateBytes(bytes, bytesOffset, ArrayFuncs.toByteArr(country));
        
        return bytes;
    }

    /**
     *
     * @param offset -1 for continue from current place
     * @return
     * @throws IOException
     */
    public byte[] readRecord(long offset) throws IOException {
        byte[] bytes = new byte[SIZE_OF_DATA_REC];
        if( offset == -1 ) {
            //set offset to current offset
            offset = randAccessFile.getFilePointer();
        }
        //System.out.println("OFFSET:" + (offset-SIZE_OF_HEADER_REC) / SIZE_OF_DATA_REC);        
        if ( (offset + SIZE_OF_DATA_REC) > randAccessFile.length() ) {
            //error offset > file length
            bytes = new byte[0];
            System.out.println("Don't alter file length since id isn't in file");
        } else {
            randAccessFile.seek(offset);
            randAccessFile.read(bytes);
        }

        return bytes;
    }

    /**
     * Calculate home rrn by 3 char code
     * @param code
     * @return 
     */
    public int hashFunction(String code) {
        int homeRRN;
        if (code.length() == 3) {
            char[] codeArr = code.toUpperCase().toCharArray();

            int multiplied = codeArr[0] * codeArr[1] * codeArr[2];
            homeRRN = (multiplied % MAX_N_HOME_LOC) + 1;
        } else {
            homeRRN = -1;
        }

        return homeRRN;
    }
    
    /**
     * Given data use hash function to store/chain data
     * @param id
     * @param code
     * @param name
     * @param continent
     * @param surfaceArea
     * @param yearOfIndep
     * @param population
     * @param lifeExpectancy
     * @return 
     */
    private boolean hashCountry(short id, String code, String name, String continent, int surfaceArea, short yearOfIndep, long population, float lifeExpectancy) {
        boolean error = false;
        int homeRRN = hashFunction(code);
        long offset = SIZE_OF_HEADER_REC + SIZE_OF_DATA_REC * (homeRRN-1);
//        System.out.println("OFFSSET ORIG:"+((offset-SIZE_OF_HEADER_REC) / SIZE_OF_DATA_REC));
        try {
            short link = -1;
            byte[] row = readRecord(offset);
            if( row.length != SIZE_OF_DATA_REC || ArrayFuncs.isByteArrByte(row, DATA_REC_EMPTY_VALUE) ) {
                //System.out.println("Empty home record for " + code);
                //nothing here so write here
                nHomeRec++;
            } else {
                //System.out.println("Collision for " + code);
                //collision
                nCollRec++;
                
                //update home rrn to link to new record
                int linkStart = SIZE_OF_DATA_REC - SIZE_OF_FIELD_LINK;
                
                //new record has home's link
                link = ArrayFuncs.toShort( Arrays.copyOfRange(row, linkStart, linkStart + SIZE_OF_FIELD_LINK) );
                
                //rewrite row with new link
                row = ArrayFuncs.concatenateBytes(row, linkStart, ArrayFuncs.toByteArr(nextEmpty));
                randAccessFile.seek(offset);
                randAccessFile.write(row);
                
                //update offset for new record insert
                offset = SIZE_OF_HEADER_REC + SIZE_OF_DATA_REC * (nextEmpty-1);
                //System.out.printf("Collision[%s]-> next:%d homeRRN:%d\n", code, nextEmpty, homeRRN);
                
                //increment next empty
                nextEmpty++;
            }
            
//            if ( id == 9 ) {
//                System.out.println("");
//                System.out.println("ID:"+id);
//                System.out.println("Code:"+code);
//                System.out.println("name:"+name);
//                System.out.println("continent:"+continent);
//                System.out.println("surfaceArea:"+surfaceArea);
//                System.out.println("yearOfIndep:"+yearOfIndep);
//                System.out.println("population:"+population);
//                System.out.println("lifeExpectancy:"+lifeExpectancy);
//            }
//            System.out.println("OFFSET["+code+"]:" + (offset-SIZE_OF_HEADER_REC) / SIZE_OF_DATA_REC);
            
            randAccessFile.seek(offset);
            byte[] binary = convertFieldsToByteArr(id, code, name, continent, surfaceArea, yearOfIndep, population, lifeExpectancy, link);
            randAccessFile.write(binary);
        } catch (IOException ex) {
            error = true;
        }
        return error;
        
    }
    
    /**
     * Returns formated string from binary "line"
     *
     * @param bytes
     * @return
     */
    public String formatCountryStr(byte[] bytes) {
        String str = null;

        if (bytes.length == SIZE_OF_DATA_REC) {
            int byteOffset = 0;

            short id = ArrayFuncs.toShort(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_ID));
            byteOffset += SIZE_OF_FIELD_ID;
            String code = ArrayFuncs.toString(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_CODE));
            byteOffset += SIZE_OF_FIELD_CODE;
            String name = ArrayFuncs.toString(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_NAME));
            byteOffset += SIZE_OF_FIELD_NAME;
            String continent = ArrayFuncs.toString(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_CONTINENT));
            byteOffset += SIZE_OF_FIELD_CONTINENT;
            int surfaceArea = ArrayFuncs.toInt(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_SIZE));
            byteOffset += SIZE_OF_FIELD_SIZE;
            short yearOfIndep = ArrayFuncs.toShort(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_YEAR));
            byteOffset += SIZE_OF_FIELD_YEAR;
            long population = ArrayFuncs.toLong(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_POPULATION));
            byteOffset += SIZE_OF_FIELD_POPULATION;
            float lifeExpectancy = ArrayFuncs.toFloat(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_LIFE));
            byteOffset += SIZE_OF_FIELD_LIFE;
            short link = ArrayFuncs.toShort(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_LINK));
            byteOffset += SIZE_OF_FIELD_LINK;
            
            /**
            System.out.println("ID:"+id);
            System.out.println("Code:"+code);
            System.out.println("name:"+name);
            System.out.println("continent:"+continent);
            System.out.println("surfaceArea:"+surfaceArea);
            System.out.println("yearOfIndep:"+yearOfIndep);
            System.out.println("population:"+population);
            System.out.println("lifeExpectancy:"+lifeExpectancy);
            **/
            
            str = String.format("%03d %-3s %-13s %-17s %8d %5d %10d %2.1f", id, code, name, continent, surfaceArea, yearOfIndep, population, lifeExpectancy);
        }
        return str;
    }
    
    /**
     * For pretty print
     * @param bytes
     * @return 
     */
    public String formatCountryStrWithLink(byte[] bytes) {
        String str = null;

        if (bytes.length == SIZE_OF_DATA_REC) {
            int byteOffset = 0;

            short id = ArrayFuncs.toShort(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_ID));
            byteOffset += SIZE_OF_FIELD_ID;
            String code = ArrayFuncs.toString(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_CODE));
            byteOffset += SIZE_OF_FIELD_CODE;
            String name = ArrayFuncs.toString(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_NAME));
            byteOffset += SIZE_OF_FIELD_NAME;
            String continent = ArrayFuncs.toString(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_CONTINENT));
            byteOffset += SIZE_OF_FIELD_CONTINENT;
            int surfaceArea = ArrayFuncs.toInt(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_SIZE));
            byteOffset += SIZE_OF_FIELD_SIZE;
            short yearOfIndep = ArrayFuncs.toShort(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_YEAR));
            byteOffset += SIZE_OF_FIELD_YEAR;
            long population = ArrayFuncs.toLong(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_POPULATION));
            byteOffset += SIZE_OF_FIELD_POPULATION;
            float lifeExpectancy = ArrayFuncs.toFloat(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_LIFE));
            byteOffset += SIZE_OF_FIELD_LIFE;
            short link = ArrayFuncs.toShort(Arrays.copyOfRange(bytes, byteOffset, byteOffset + SIZE_OF_FIELD_LINK));
            byteOffset += SIZE_OF_FIELD_LINK;
            
            /**
            System.out.println("ID:"+id);
            System.out.println("Code:"+code);
            System.out.println("name:"+name);
            System.out.println("continent:"+continent);
            System.out.println("surfaceArea:"+surfaceArea);
            System.out.println("yearOfIndep:"+yearOfIndep);
            System.out.println("population:"+population);
            System.out.println("lifeExpectancy:"+lifeExpectancy);
            **/
            
            str = String.format("%03d %-3s %-13s %-17s %8d %5d %10d %2.1f %d", id, code, name, continent, surfaceArea, yearOfIndep, population, lifeExpectancy, link);
        }
        return str;
    }
    
    /**
     * Convert data into byte array for easy saving
     * @param id
     * @param code
     * @param name
     * @param continent
     * @param surfaceArea
     * @param yearOfIndep
     * @param population
     * @param lifeExpectancy
     * @param link
     * @return 
     */
    public byte[] convertFieldsToByteArr(short id, String code, String name, String continent, int surfaceArea, short yearOfIndep, long population, float lifeExpectancy, short link) {
        byte[] binary = new byte[SIZE_OF_DATA_REC];
        int binOffset = 0;

        binary = ArrayFuncs.concatenateBytes(binary, binOffset, ArrayFuncs.toByteArr(id));
        binOffset += SIZE_OF_FIELD_ID;
        binary = ArrayFuncs.concatenateBytes(binary, binOffset, ArrayFuncs.toByteArr(code));
        binOffset += SIZE_OF_FIELD_CODE;
        binary = ArrayFuncs.concatenateBytes(binary, binOffset, ArrayFuncs.toByteArr(name));
        binOffset += SIZE_OF_FIELD_NAME;
        binary = ArrayFuncs.concatenateBytes(binary, binOffset, ArrayFuncs.toByteArr(continent));
        binOffset += SIZE_OF_FIELD_CONTINENT;
        binary = ArrayFuncs.concatenateBytes(binary, binOffset, ArrayFuncs.toByteArr(surfaceArea));
        binOffset += SIZE_OF_FIELD_SIZE;
        binary = ArrayFuncs.concatenateBytes(binary, binOffset, ArrayFuncs.toByteArr(yearOfIndep));
        binOffset += SIZE_OF_FIELD_YEAR;
        binary = ArrayFuncs.concatenateBytes(binary, binOffset, ArrayFuncs.toByteArr(population));
        binOffset += SIZE_OF_FIELD_POPULATION;
        binary = ArrayFuncs.concatenateBytes(binary, binOffset, ArrayFuncs.toByteArr(lifeExpectancy));
        binOffset += SIZE_OF_FIELD_LIFE;
        binary = ArrayFuncs.concatenateBytes(binary, binOffset, ArrayFuncs.toByteArr(link));
        binOffset += SIZE_OF_FIELD_LINK;

        return binary;
    }

    /**
     * close files
     */
    public void deconstruct() {
        ui.writeLine("close CountryData File");
        try {
            byte[] bytes = new byte[SIZE_OF_HEADER_REC];
            
            int bytesOffset = 0;
            bytes = ArrayFuncs.concatenateBytes(bytes, bytesOffset, ArrayFuncs.toByteArr(nHomeRec));
            bytesOffset += SIZE_OF_SHORT;
            bytes = ArrayFuncs.concatenateBytes(bytes, bytesOffset, ArrayFuncs.toByteArr(nCollRec));
            bytesOffset += SIZE_OF_SHORT;
            bytes = ArrayFuncs.concatenateBytes(bytes, bytesOffset, ArrayFuncs.toByteArr(nextEmpty));
            bytesOffset += SIZE_OF_SHORT;
            bytes = ArrayFuncs.concatenateBytes(bytes, bytesOffset, ArrayFuncs.toByteArr(MAX_N_HOME_LOC));
            bytesOffset += SIZE_OF_SHORT;
            
            System.out.printf("nHome:%d nColl:%d nextEmtpy:%d MAX_N_HOME_LOC:%d\n", nHomeRec, nCollRec, nextEmpty, MAX_N_HOME_LOC);
            randAccessFile.seek(0);
            randAccessFile.write(bytes);
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

    public short getnHomeRec() {
        return nHomeRec;
    }

    public void setnHomeRec(short nHomeRec) {
        this.nHomeRec = nHomeRec;
    }

    public short getnCollRec() {
        return nCollRec;
    }

    public void setnCollRec(short nCollRec) {
        this.nCollRec = nCollRec;
    }

    public short getNextEmpty() {
        return nextEmpty;
    }

    public void setNextEmpty(short nextEmpty) {
        this.nextEmpty = nextEmpty;
    }

    public short getMAX_N_HOME_LOC() {
        return MAX_N_HOME_LOC;
    }

    public void setMAX_N_HOME_LOC(short MAX_N_HOME_LOC) {
        this.MAX_N_HOME_LOC = MAX_N_HOME_LOC;
    }

    public int getSIZE_OF_CHAR() {
        return SIZE_OF_CHAR;
    }

    public int getSIZE_OF_SHORT() {
        return SIZE_OF_SHORT;
    }

    public int getSIZE_OF_INT() {
        return SIZE_OF_INT;
    }

    public int getSIZE_OF_LONG() {
        return SIZE_OF_LONG;
    }

    public int getSIZE_OF_FLOAT() {
        return SIZE_OF_FLOAT;
    }

    public int getSIZE_OF_FIELD_ID() {
        return SIZE_OF_FIELD_ID;
    }

    public int getSIZE_OF_FIELD_CODE() {
        return SIZE_OF_FIELD_CODE;
    }

    public int getSIZE_OF_FIELD_CONTINENT() {
        return SIZE_OF_FIELD_CONTINENT;
    }

    public int getSIZE_OF_FIELD_NAME() {
        return SIZE_OF_FIELD_NAME;
    }

    public int getSIZE_OF_FIELD_SIZE() {
        return SIZE_OF_FIELD_SIZE;
    }

    public int getSIZE_OF_FIELD_YEAR() {
        return SIZE_OF_FIELD_YEAR;
    }

    public int getSIZE_OF_FIELD_POPULATION() {
        return SIZE_OF_FIELD_POPULATION;
    }

    public int getSIZE_OF_FIELD_LIFE() {
        return SIZE_OF_FIELD_LIFE;
    }

    public int getSIZE_OF_FIELD_LINK() {
        return SIZE_OF_FIELD_LINK;
    }

    public int getN_OF_FIELDS() {
        return N_OF_FIELDS;
    }

    public int getSIZE_OF_ALL_COMMAS_TOGETHER() {
        return SIZE_OF_ALL_COMMAS_TOGETHER;
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

    public String getFilePath() {
        return filePath;
    }

    public String getFILE_MODE() {
        return FILE_MODE;
    }

    public int getSIZE_OF_HEADER_REC() {
        return SIZE_OF_HEADER_REC;
    }

    public int getSIZE_OF_FORMATTED_COUNTRY_STR() {
        return SIZE_OF_FORMATTED_COUNTRY_STR;
    }

    public byte getDATA_REC_EMPTY_VALUE() {
        return DATA_REC_EMPTY_VALUE;
    }

    public byte getDATA_REC_TOMBSTONE_VALUE() {
        return DATA_REC_TOMBSTONE_VALUE;
    }

    public int getDATA_REC_TOMBSTONE_END() {
        return DATA_REC_TOMBSTONE_END;
    }
    
}
