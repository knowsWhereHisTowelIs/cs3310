/**
 * CS3310 - A3
 * Author: Caleb Slater
 */
package files;

import funcs.ArrayFuncs;
import funcs.BstNode;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author CallMeTim
 */
public class NameIndex {

    private final int SIZE_OF_CHAR = 1;
    private final int SIZE_OF_SHORT = 2;
    private final int SIZE_OF_INT = 4;
    private final int SIZE_OF_LONG = 8;
    //private final int SIZE_OF_BOOLEAN = 1;
    private final int SIZE_OF_FLOAT = 4;

    private final int SIZE_OF_FIELD_ROOT_PTR = SIZE_OF_SHORT;
    private final int SIZE_OF_FIELD_N = SIZE_OF_SHORT;
    private final int SIZE_OF_HEADER_REC = SIZE_OF_FIELD_ROOT_PTR + SIZE_OF_FIELD_N;

    private final int SIZE_OF_DATA_REC = BstNode.SIZE_OF_FIELD_LEFT_CHILD_PTR
            + BstNode.SIZE_OF_FIELD_IN_BYTES_KEY_VALUE
            + BstNode.SIZE_OF_FIELD_DATA_PTR 
            + BstNode.SIZE_OF_FIELD_RIGHT_CHILD_PTR;

    private short rootPtr, N; //N is next element... specified to be not self documenting
    private UI ui;
    private final String filePath = "src/data/IndexBackup.bin";
    private RandomAccessFile randAccessFile;
    private boolean errored = false;
    private final String FILE_MODE = "rw";
    private ArrayList<BstNode> nodes = new ArrayList<BstNode>();

    public NameIndex(boolean isSetup) {
        ui = new UI(false);
        openFile();
        if( isSetup ) {
            clearFile();
        }
        loadFileData();
    }

    /**
     * open file in memory
     */
    private void openFile() {
        File file = new File(filePath);
        ui.writeLine("open IndexBackup File");
        try {
            randAccessFile = new RandomAccessFile(file, FILE_MODE);
        } catch (FileNotFoundException ex) {
            errored = true;
            System.out.println("Couldn't create " + file);
        }
    }
    
    /**
     * truncate file data
     */
    public void clearFile() {
        try {
            randAccessFile.setLength( 0 );
        } catch (IOException ex) {
            ui.writeLine("error clearing file");
        }
    }
    
    /**
     * Load index file after saved
     */
    private void loadFileData() {
        //set defaults incase of error
        rootPtr = -1;
        N = 0;
        try {
            if (randAccessFile.length() >= SIZE_OF_HEADER_REC) {
                byte[] bytes = new byte[SIZE_OF_HEADER_REC];
                int bytesOffset;

                if (randAccessFile.read(bytes) == SIZE_OF_HEADER_REC) {
                    bytesOffset = 0;
                    rootPtr = ArrayFuncs.toShort(Arrays.copyOfRange(bytes, bytesOffset, bytesOffset + SIZE_OF_FIELD_ROOT_PTR));
                    bytesOffset += SIZE_OF_FIELD_ROOT_PTR;
                    N = ArrayFuncs.toShort(Arrays.copyOfRange(bytes, bytesOffset, bytesOffset + SIZE_OF_FIELD_N));
                }
                byte[] data = new byte[SIZE_OF_DATA_REC];
                while (randAccessFile.read(data) == SIZE_OF_DATA_REC) {
                    bytesOffset = 0;
                    short leftChildPtr = ArrayFuncs.toShort(Arrays.copyOfRange(data, bytesOffset, bytesOffset + BstNode.SIZE_OF_FIELD_LEFT_CHILD_PTR));
                    bytesOffset += BstNode.SIZE_OF_FIELD_LEFT_CHILD_PTR;
                    String keyVal = ArrayFuncs.toString(Arrays.copyOfRange(data, bytesOffset, bytesOffset + BstNode.SIZE_OF_FIELD_KEY_VALUE));
                    bytesOffset += BstNode.SIZE_OF_FIELD_KEY_VALUE;
                    int dataPtr = ArrayFuncs.toInt(Arrays.copyOfRange(data, bytesOffset, bytesOffset + BstNode.SIZE_OF_FIELD_DATA_PTR));
                    bytesOffset += BstNode.SIZE_OF_FIELD_DATA_PTR;
                    short rightChildPtr = ArrayFuncs.toShort(Arrays.copyOfRange(data, bytesOffset, bytesOffset + BstNode.SIZE_OF_FIELD_RIGHT_CHILD_PTR));
                    
                    BstNode node = new BstNode(leftChildPtr, keyVal, dataPtr, rightChildPtr);
                    nodes.add(node);
                }
            }
        } catch (IOException ex) {
            //use defaults
        }
    }
    
    /**
     * get null or BstNode if name is in tree
     * @param name
     * @return 
     */
    public Object[] getNodeDataByName(String name) {
        BstNode returnNode = null;
        BstNode node = rootPtr == -1 ? null : nodes.get(rootPtr);
        int nodesVisited = 0;
        String nameFormatted = name.trim();
        while( node != null ) {
            int nameCmp = nameFormatted.compareToIgnoreCase(node.getKeyValue().trim());
            if( nameCmp == 0 ) {
                returnNode = node;
                node = null;
            } else if ( nameCmp < 0 && node.getLeftChildPtr() != -1 ) {
                node = nodes.get( node.getLeftChildPtr() );
            } else if( nameCmp > 0 && node.getRightChildPtr() != -1 ) {
                node = nodes.get( node.getRightChildPtr());
            } else {
                //not found
                node = null;
            }
            
            nodesVisited++;
        }
        
        Object[] ret = new Object[2];
        ret[0] = returnNode;
        ret[1] = nodesVisited;
        return ret;
    }
    
    /**
     * Add node to BstNode Tree
     * @param keyValue
     * @param dataRecPointer 
     */
    public void addNode(String keyValue, int dataRecPointer) {
        boolean foundSpot = false;
        short parentIndex = -1;
        short currentIndex = rootPtr;
        while (foundSpot == false) {
            if (currentIndex == -1) {
                foundSpot = true;
                currentIndex = 0; //set root pointer
                rootPtr = currentIndex;
            } else {
                parentIndex = currentIndex;
                BstNode parent = nodes.get(currentIndex);
                if (keyValue.compareToIgnoreCase(parent.getKeyValue()) < 0) {
                    currentIndex = parent.getLeftChildPtr();
                    if (currentIndex == -1) {
                        parent.setLeftChildPtr(N);
                    }
                } else {
                    currentIndex = parent.getRightChildPtr();
                    if (currentIndex == -1) {
                        parent.setRightChildPtr(N);
                    }
                }
                nodes.set(parentIndex, parent);
            }
        }

        BstNode node = new BstNode( (short) -1, keyValue, dataRecPointer, (short) -1);
        
        nodes.add(N, node);
        //increment next number of elements
        N++;
    }

    /**
     * Left Parent Right traversal of BST
     * @param node 
     */
    public ArrayList<BstNode> getInOrderTraversal(int ptr) {
        ArrayList<BstNode> traversal = new ArrayList<>();
        if ( ptr != -1 ) {
            BstNode node = nodes.get(ptr);
            if ( node.getLeftChildPtr() != -1 ) {
                traversal.addAll( getInOrderTraversal( node.getLeftChildPtr() ) );
            }
            
            traversal.add(node);
            
            if ( node.getRightChildPtr() != -1 ) {
                traversal.addAll( getInOrderTraversal( node.getRightChildPtr() ) );
            }
        }
        return traversal;
    }

    /**
     * destroy object and save data
     */
    public void deconstruct() {
        try {
            saveData();
        } catch (IOException ex) {
            ui.writeLine("Error saving name index");
        }
    }

    /**
     * save header and bst node
     * @throws IOException 
     */
    private void saveData() throws IOException {
        //clear file
        randAccessFile.setLength(0);

        //header rec
        randAccessFile.writeShort(rootPtr);
        randAccessFile.writeShort(N);
        //data
        for (BstNode node : nodes) {
            byte[] bytes = node.toBytes();
            randAccessFile.write(bytes);
        }
        
        randAccessFile.close();
        ui.writeLine("close IndexBackup File");
    }

    // -----------GETTERs and SETTERs----------
    
    public short getRootPtr() {
        return rootPtr;
    }

    public void setRootPtr(short rootPtr) {
        this.rootPtr = rootPtr;
    }

    public short getN() {
        return N;
    }

    public void setN(short N) {
        this.N = N;
    }

    public UI getUi() {
        return ui;
    }

    public void setUi(UI ui) {
        this.ui = ui;
    }

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

    public ArrayList<BstNode> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<BstNode> nodes) {
        this.nodes = nodes;
    }
    
    
}
