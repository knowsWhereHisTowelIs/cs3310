/**
 * CS3310 - A1
 * Author: Caleb Slater
 */
package files;

import funcs.Strings;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Manages CountryData.csv
 */
public class CountryData {

    private final int SIZE_OF_FIELD_ID = 3;
    private final int SIZE_OF_FIELD_CODE = 3;
    private final int SIZE_OF_FIELD_CONTINENT = 13;
    private final int SIZE_OF_FIELD_NAME = 17;
    private final int SIZE_OF_FIELD_SIZE = 8;
    private final int SIZE_OF_FIELD_YEAR = 5;
    private final int SIZE_OF_FIELD_POPULATION = 10;
    private final int SIZE_OF_FIELD_LIFE = 4;

    private final int SIZE_OF_ALL_FIELDS_MAX = SIZE_OF_FIELD_ID + SIZE_OF_FIELD_CODE
            + SIZE_OF_FIELD_CONTINENT + SIZE_OF_FIELD_NAME
            + SIZE_OF_FIELD_SIZE + SIZE_OF_FIELD_YEAR + SIZE_OF_FIELD_POPULATION
            + SIZE_OF_FIELD_LIFE;

    private final int N_OF_FIELDS = 8;
    private final int SIZE_OF_ALL_COMMAS_TOGETHER = N_OF_FIELDS - 1;
    private final int CR_AND_LF = 2;
    private final int SIZE_OF_ONE_CHAR = 1;

    private final int SIZE_OF_DATA_REC = (SIZE_OF_ALL_FIELDS_MAX + SIZE_OF_ALL_COMMAS_TOGETHER + CR_AND_LF) * SIZE_OF_ONE_CHAR;
    private final int SIZE_OF_DATA_REC_WO_NEW_LN = (SIZE_OF_ALL_FIELDS_MAX + SIZE_OF_ALL_COMMAS_TOGETHER) * SIZE_OF_ONE_CHAR;
    private final int SIZE_OF_HEADER_REC = 0;

    private final String filePath = "src/data/CountryData.csv";

    private RandomAccessFile randAccessFile;
    private UI ui;

    private final String FILE_MODE = "rw";

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

            randAccessFile.close();

            FileWriter fileOut = new FileWriter("file.txt");
            fileOut.write("");
            fileOut.close();
            System.out.println("Cleared country data file");

            File file = new File(filePath);
            randAccessFile = new RandomAccessFile(file, FILE_MODE);
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
        if (fields.length >= N_OF_FIELDS) {
            int id = Integer.parseInt(fields[0]);
            String idStr = String.format("%03d", id);
            String code = Strings.forceStrLength(3, fields[1]);
            String name = Strings.forceStrLength(17, fields[2]);
            String continent = Strings.forceStrLength(13, fields[3]);
            //String region           = Strings.forceStrLength(20, fields[4]);
            String size = Strings.forceStrLength(8, fields[5]);
            String yearOfIndep = fields[6].equals("NULL") ? "0    " : Strings.forceStrLength(5, fields[6]);
            String population = Strings.forceStrLength(10, fields[7]);
            String lifeExpectancy = fields[8].equals("NULL") ? "0.00" : Strings.forceStrLength(4, fields[8]);

            String formattedLine = String.format("%s,%s,%s,%s,%s,%s,%s,%s\r\n", idStr, code, name, continent, size, yearOfIndep, population, lifeExpectancy);

            long offset = SIZE_OF_HEADER_REC + SIZE_OF_DATA_REC * (id - 1);

            /**
             * System.out.println("idStr " + idStr ); System.out.println("code "
             * + code ); System.out.println("name " + name );
             * System.out.println("continent " + continent );
             * System.out.println("region " + region ); System.out.println("size
             * " + size ); System.out.println("yearOfIndep " + yearOfIndep );
             * System.out.println("population " + population );
             * System.out.println("lifeExpectancy" + lifeExpectancy );
            *
             */
            try {
                randAccessFile.seek(offset);
                randAccessFile.writeBytes(formattedLine);
                response = 1;
                //System.out.printf("\nSuccessfully inserted:%s length:%d \n", code, formattedLine.length());
                //System.out.println(formattedLine.substring(0, formattedLine.length() - 1));
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
     * Delete country from data
     * @param id 
     */
    public void deleteCountry(int id) {
        long offset = SIZE_OF_HEADER_REC + SIZE_OF_DATA_REC * (id - 1);
        String deletedLine = "\0";

        try {
            if (queryCountry(id).equals("")) {
                ui.writeLine("** ERROR: no country with id " + id);
            } else {
                System.out.println("Don't alter file length since id isn't in file");
                String country = queryCountry(id);
                String name = new String();
                if (country.length() > 25) {
                    name = country.substring(8, 25);
                }

                randAccessFile.seek(offset);
                randAccessFile.writeBytes(deletedLine);
                ui.writeLine("OK, " + name + " deleted");
            }
        } catch (IOException ex) {

        }
    }

    /**
     * Get list of all active countries 
     * @return 
     */
    public ArrayList<String> getListOfAllCountries() {
        ArrayList<String> list = new ArrayList();
        try {
            randAccessFile.seek((long) 0);
            String line;
            while (!(line = getCountryLineFromCurrentOffset()).equals("")) {
                //System.out.println("LINE: '" + line + "'");
                if (line.charAt(0) == '\0') {
                    //empty/deleted
                    //System.out.println("NULL" + line);
                } else {
                    list.add(line);
                }
            }
            //System.out.format("LIST FINAL-------------------\n%s\n-----------------------------\n", list.toString());
        } catch (IOException ex) {
            //list = null;
        }

        return list;
    }

    /**
     * format raw csv line
     * @param line
     * @return 
     */
    public String formatCountryStr(String line) {
        String str;
        if (line.length() >= SIZE_OF_DATA_REC_WO_NEW_LN) {
            //System.out.println(line + " l:" + line.length());
            StringBuilder builder = new StringBuilder();
            int sizeOfSpacer = 1;

            int startOffset = 0;
            int endOffset = SIZE_OF_FIELD_ID;
            //System.out.println("Start:"+startOffset+" End:" + endOffset);
            builder.append(line.substring(startOffset, endOffset)).append(" ");

            startOffset = endOffset + sizeOfSpacer;
            endOffset = startOffset + SIZE_OF_FIELD_CODE;
            //System.out.println("Start:"+startOffset+" End:" + endOffset);
            builder.append(line.substring(startOffset, endOffset)).append(" ");

            startOffset = endOffset + sizeOfSpacer;
            endOffset = startOffset + SIZE_OF_FIELD_NAME;
            //System.out.println("Start:"+startOffset+" End:" + endOffset);
            builder.append(line.substring(startOffset, endOffset)).append(" ");

            startOffset = endOffset + sizeOfSpacer;
            endOffset = startOffset + SIZE_OF_FIELD_CONTINENT;
            //System.out.println("Start:"+startOffset+" End:" + endOffset);
            builder.append(line.substring(startOffset, endOffset)).append(" ");

            startOffset = endOffset + sizeOfSpacer;
            endOffset = startOffset + SIZE_OF_FIELD_SIZE;
            //System.out.println("Start:"+startOffset+" End:" + endOffset);
            builder.append(line.substring(startOffset, endOffset)).append(" ");

            startOffset = endOffset + sizeOfSpacer;
            endOffset = startOffset + SIZE_OF_FIELD_YEAR;
            //System.out.println("Start:"+startOffset+" End:" + endOffset);
            builder.append(line.substring(startOffset, endOffset)).append(" ");

            startOffset = endOffset + sizeOfSpacer;
            endOffset = startOffset + SIZE_OF_FIELD_POPULATION;
            //System.out.println("Start:"+startOffset+" End:" + endOffset);
            builder.append(line.substring(startOffset, endOffset)).append(" ");

            startOffset = endOffset + sizeOfSpacer;
            endOffset = startOffset + SIZE_OF_FIELD_LIFE;
            //System.out.println("Start:"+startOffset+" End:" + endOffset);
            builder.append(line.substring(startOffset, endOffset));

            str = builder.toString();
        } else {
            System.out.println("LINE TO SHORT: " + line.length());
            str = "";
        }
        return str;
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

        if (country.equals("")) {
            //ui.writeLine("Country not found\n");
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

    public int getSIZE_OF_HEADER_REC() {
        return SIZE_OF_HEADER_REC;
    }

    public String getFilePath() {
        return filePath;
    }

}
