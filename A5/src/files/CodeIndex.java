/**
 * CS3310 - A5 
 * Author: Caleb Slater
 */
package files;

import funcs.Strings;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class CodeIndex {

    private int M;
    private int rootPtr;
    private int N;

    private boolean errored = false;
    private UI ui;
    private RandomAccessFile randAccessFile;
    private String filePath;
    private String FILE_MODE = "rw";

    private long headerLength = 0;
    private int sizeOfDataRec = 0;
    private final int SIZE_OF_KEY_VALUE = 3;
    private final int SIZE_OF_DATA_REC_PTR = 2;
    private final int SIZE_OF_TREE_PTR = 2;
    private final int SIZE_OF_SEPERATOR = 1;
    private final int SIZE_OF_NEW_LINE = 2; // \r\n

    /**
     * 
     * @param dataSet 
     */
    public CodeIndex(int dataSet) {
        filePath = "src/data/CodeIndex_"+dataSet+".csv";
        File file = new File(filePath);
        ui = new UI(false);
        ui.writeLine("open CodeIndex File");
        try {
            randAccessFile = new RandomAccessFile(file, FILE_MODE);
            parseHeader();
        } catch (FileNotFoundException ex) {
            errored = true;
            System.out.println("Couldn't create " + file);
        }
    }

    /**
     * parse header load M, rootPtr, N, sizeOfDataRec, headerLength
     */
    private void parseHeader() {
        String line = readLineFromCurrentOffset();
        String[] parts = line.split(",");
        if (parts.length == 3) {
            M = Integer.parseInt(parts[0]);
            rootPtr = Integer.parseInt(parts[1]);
            N = Integer.parseInt(parts[2]);
            sizeOfDataRec = (SIZE_OF_KEY_VALUE + SIZE_OF_DATA_REC_PTR + SIZE_OF_TREE_PTR) * (M - 1)
                    + ( (M-1) * 3 * SIZE_OF_SEPERATOR)
                    + SIZE_OF_TREE_PTR
                    + SIZE_OF_NEW_LINE;
        } else {
            sizeOfDataRec = 0;
        }
        try {
            headerLength = randAccessFile.getFilePointer();
        } catch (IOException ex) {
            headerLength = 0;
        }
    }

    /**
     * Read a node by index
     * @param nodeIndex
     * @return 
     */
    private String readANode(int nodeIndex) {
        long offset = headerLength + sizeOfDataRec * (nodeIndex-1);
        boolean successfullySeeked = seek(offset);
        return readLineFromCurrentOffset();
    }

    /**
     * read line from current offset
     * @return 
     */
    private String readLineFromCurrentOffset() {
        String line;
        try {
            line = randAccessFile.readLine();
        } catch (IOException exception) {
            line = null;
        }
        return line;
    }

    /**
     * close files
     */
    public void deconstruct() {
        ui.writeLine("close CodeIndex File");
        try {
            randAccessFile.close();
        } catch (IOException ex) {
            System.out.println("CodeIndex deconstruct error");
        }
    }

    /**
     * searches through nodes and returns codes
     *
     * @param code
     * @return -1 on failure else country id
     */
    public int[] queryByCode(String code) {
        int drp = -1;
        int nodes = 0;
        int dataRecords = 0;
        
        String line;
        int linePtr = rootPtr;
        int numOfFields = M*3 - 2; // M-1 kvs, M-1 drps, M tps
        String[] keyValues = new String[M];
        keyValues[M-1] = "___"; // ascii 'infinite' value
        
        while (linePtr > 0 && (line = readANode(linePtr)) != null) {
            String[] parts = line.split(",");
            if ( parts.length == numOfFields ) {
                nodes++;
                //copy array
                String[] tmpKVs = Arrays.copyOfRange(parts, 0, M-1);
                for( int i = 0; i < tmpKVs.length; i++ ) {
                    keyValues[i] = tmpKVs[i];
                }
                
                int[] dataRecordPointers = Strings.strArrToInt(parts, M-1, M*2);
                int[] treePointers = Strings.strArrToInt(parts, M*2-2, parts.length);
                
                int[] searchANodeInfo = searchANode(code, keyValues, dataRecordPointers, treePointers);
                linePtr = searchANodeInfo[0];
                drp = searchANodeInfo[1];
                dataRecords += searchANodeInfo[2];
            } else {
                linePtr = 0;
            }
        }
        int[] info = new int[3];
        info[0] = drp;
        info[1] = nodes;
        info[2] = dataRecords;
        return info;
    }
    
    /**
     * Search through a node line 
     * @param code
     * @param keyValues
     * @param dataRecordPointers
     * @param treePointers
     * @return drp, nodes, dataRecords searched
     */
    private int[] searchANode(String code, String[] keyValues, int[] dataRecordPointers, int[] treePointers) {
        int[] info = new int[3];
        int linePtr = -1;
        int drp = -1;
        int dataRecords = 0;
        for( int i = 0; i < keyValues.length; i++ ) {
            dataRecords++;
            int compareResult = code.compareTo(keyValues[i]);
            if( compareResult < 0 ) {
                //will always be executed if last row
                linePtr = treePointers[i];
                break;
            } else if ( compareResult == 0 ) {
                drp = dataRecordPointers[i];
                linePtr = -1;
                break;
            }
        }
        info[0] = linePtr;
        info[1] = drp;
        info[2] = dataRecords;
        return info;
    }

    /**
     * Nice lseek
     * @param offset
     * @return 
     */
    private boolean seek(long offset) {
        boolean isSuccessful = true;
        try {
            long length = randAccessFile.length();
            
            if (offset <= length) {
                randAccessFile.seek(offset);
            } else {
                //don't change file length
                System.out.format("SEEK ERROR offset[%d] > length[%d]\n", offset, length);
                isSuccessful = false;
            }
        } catch (IOException ex) {
            isSuccessful = false;
        }
        return isSuccessful;
    }
}
