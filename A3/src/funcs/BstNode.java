package funcs;

import files.CountryData;
import java.util.ArrayList;

public class BstNode {
    private short leftChildPtr;
    private String keyValue;
    private int dataPtr;
    private short rightChildPtr;
    
    public static final int SIZE_OF_FIELD_LEFT_CHILD_PTR = 2;
    public static final int SIZE_OF_FIELD_KEY_VALUE = CountryData.SIZE_OF_FIELD_NAME;
    public static final int SIZE_OF_FIELD_IN_BYTES_KEY_VALUE = SIZE_OF_FIELD_KEY_VALUE * CountryData.SIZE_OF_ONE_CHAR;
    public static final int SIZE_OF_FIELD_DATA_PTR = 4;
    public static final int SIZE_OF_FIELD_RIGHT_CHILD_PTR = 2;

    public BstNode(short leftChildPtr, String keyValue, int dataPtr, short rightChildPtr) {
        this.leftChildPtr = leftChildPtr;
        this.keyValue = Strings.forceStrLength(SIZE_OF_FIELD_KEY_VALUE, keyValue);
        this.dataPtr = dataPtr;
        this.rightChildPtr = rightChildPtr;
    }
    
    public byte[] toBytes() {
//        ArrayList byteArr = new ArrayList();
//        for( byte b : ArrayFuncs.toByteArr(leftChildPtr) ) {
//            byteArr.add(b);
//        }
//        for( byte b : ArrayFuncs.toByteArr(keyValue) ) {
//            byteArr.add(b);
//        }
//        for( byte b : ArrayFuncs.toByteArr(dataPtr) ) {
//            byteArr.add(b);
//        }
//        for( byte b : ArrayFuncs.toByteArr(rightChildPtr) ) {
//            byteArr.add(b);
//        }
//        byte[] bytes = new byte[byteArr.size()];
//        for (int i = 0; i < byteArr.size(); i++) {
//            bytes[i] = byteArr.get(i);
//        }
//        return bytes;
        return ArrayFuncs.concatByteArrs(
                ArrayFuncs.toByteArr(leftChildPtr) 
                , ArrayFuncs.toByteArr(keyValue)
                , ArrayFuncs.toByteArr(dataPtr) 
                , ArrayFuncs.toByteArr(rightChildPtr)
        );
    }

    public short getLeftChildPtr() {
        return leftChildPtr;
    }

    public void setLeftChildPtr(short leftChildPtr) {
        this.leftChildPtr = leftChildPtr;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = Strings.forceStrLength(SIZE_OF_FIELD_KEY_VALUE, keyValue);
    }

    public int getDataPtr() {
        return dataPtr;
    }

    public void setDataPtr(int dataPtr) {
        this.dataPtr = dataPtr;
    }

    public short getRightChildPtr() {
        return rightChildPtr;
    }

    public void setRightChildPtr(short rightChildPtr) {
        this.rightChildPtr = rightChildPtr;
    }
    
    
}
